package com.loklok.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokButton
import com.loklok.app.core.designsystem.components.LokLokTopBar
import com.loklok.app.core.designsystem.theme.LokLokCallEnd
import com.loklok.app.core.designsystem.theme.LokLokPrimary

data class QuickCountry(val name: String, val code: String, val flag: String)

val QUICK_COUNTRIES = listOf(
    QuickCountry("UAE", "+971", "🇦🇪"),
    QuickCountry("Saudi", "+966", "🇸🇦"),
    QuickCountry("Egypt", "+20", "🇪🇬"),
    QuickCountry("USA", "+1", "🇺🇸"),
    QuickCountry("UK", "+44", "🇬🇧"),
    QuickCountry("Pakistan", "+92", "🇵🇰"),
    QuickCountry("India", "+91", "🇮🇳"),
    QuickCountry("France", "+33", "🇫🇷"),
    QuickCountry("Germany", "+49", "🇩🇪"),
    QuickCountry("Jordan", "+962", "🇯🇴"),
    QuickCountry("Kuwait", "+965", "🇰🇼")
)

@Composable
fun PhoneAuthScreen(
    viewModel: PhoneAuthViewModel,
    onNavigateToOtp: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isOtpSent) {
        if (uiState.isOtpSent) {
            onNavigateToOtp(uiState.fullPhoneNumber.ifBlank { uiState.phoneNumber })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LokLokTopBar(
            title = "Enter Your Phone Number",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Lok Lok connects your account using your mobile phone number. Enter your country code and phone number below.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick country chips
            Text(
                text = "Popular Country Codes:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QUICK_COUNTRIES.forEach { country ->
                    val isSelected = uiState.countryCode == country.code
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) LokLokPrimary else MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isSelected) 2.dp else 0.5.dp,
                        modifier = Modifier.clickable { viewModel.onCountryCodeChange(country.code) }
                    ) {
                        Text(
                            text = "${country.flag} ${country.code}",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Phone Number Input with Editable Prefix
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.countryCode,
                    onValueChange = { input ->
                        val formatted = if (input.startsWith("+")) input else "+$input"
                        viewModel.onCountryCodeChange(formatted.take(6))
                    },
                    label = { Text("Code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.width(96.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LokLokPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    label = { Text("Phone Number") },
                    placeholder = { Text("e.g. 50 123 4567") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LokLokPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = LokLokCallEnd,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            LokLokButton(
                text = "Next",
                isLoading = uiState.isLoading,
                enabled = uiState.phoneNumber.isNotBlank(),
                onClick = viewModel::sendOtp
            )
        }
    }
}
