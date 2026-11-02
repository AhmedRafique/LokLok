package com.loklok.app.feature.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.theme.LokLokCallAccept
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.TransactionType
import com.loklok.app.domain.model.WalletTransaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LokLokPayScreen(
    viewModel: WalletViewModel,
    onSendMoneyClick: () -> Unit,
    onScanQrClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00B074),
                                    Color(0xFF006B46)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.TopStart)) {
                        Text(
                            text = "Lok Lok Wallet",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${uiState.account.currency} ${String.format(Locale.getDefault(), "%,.2f", uiState.account.balance)}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KYC Verified ✓",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions (Send, Request, QR, Remittance)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PayActionButton(icon = Icons.Default.Send, label = "Send", onClick = onSendMoneyClick)
                PayActionButton(icon = Icons.Default.ArrowDownward, label = "Request", onClick = {})
                PayActionButton(icon = Icons.Default.QrCodeScanner, label = "Scan QR", onClick = onScanQrClick)
                PayActionButton(icon = Icons.Default.SwapHoriz, label = "Remit", onClick = {})
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Everyday Services Grid (Botim Ultra App Style)
            Text(
                text = "Services & Bills",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ServiceItem(icon = Icons.Default.PhoneAndroid, label = "Mobile Top-up")
                ServiceItem(icon = Icons.Default.ElectricBolt, label = "Electricity & Water")
                ServiceItem(icon = Icons.Default.AccountBalance, label = "Bank Transfer")
                ServiceItem(icon = Icons.Default.Receipt, label = "Salik & Tolls")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Recent Transactions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        items(
            items = uiState.recentTransactions,
            key = { it.id }
        ) { tx ->
            TransactionRow(tx = tx)
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = 56.dp)
            )
        }
    }
}

@Composable
fun PayActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = LokLokPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ServiceItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 2,
            lineHeight = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun TransactionRow(tx: WalletTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isIncome = tx.type == TransactionType.P2P_RECEIVE || tx.type == TransactionType.TOP_UP
        val icon = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
        val iconBg = if (isIncome) LokLokCallAccept.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Tx",
                tint = if (isIncome) LokLokCallAccept else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                .format(Date(tx.timestamp))
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        val prefix = if (isIncome) "+ " else "- "
        val amountColor = if (isIncome) LokLokCallAccept else MaterialTheme.colorScheme.onSurface

        Text(
            text = "$prefix${tx.currency} ${String.format(Locale.getDefault(), "%.2f", tx.amount)}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}
