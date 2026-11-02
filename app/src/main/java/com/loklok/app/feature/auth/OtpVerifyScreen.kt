package com.loklok.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokButton
import com.loklok.app.core.designsystem.components.LokLokTopBar
import com.loklok.app.core.designsystem.theme.LokLokCallEnd
import com.loklok.app.core.designsystem.theme.LokLokPrimary

@Composable
fun OtpVerifyScreen(
    phoneNumber: String,
    viewModel: PhoneAuthViewModel,
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onVerificationSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LokLokTopBar(
            title = "Verify $phoneNumber",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Waiting to automatically detect an SMS sent to $phoneNumber.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 6-digit OTP code input
            OutlinedTextField(
                value = uiState.otpCode,
                onValueChange = {
                    if (it.length <= 6) {
                        viewModel.onOtpChange(it)
                        if (it.length == 6) {
                            viewModel.verifyOtp()
                        }
                    }
                },
                placeholder = { Text("• • • • • •", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(0.7f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LokLokPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = LokLokCallEnd,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Didn't receive code?",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                TextButton(onClick = viewModel::sendOtp) {
                    Text(
                        text = "Resend SMS",
                        color = LokLokPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            LokLokButton(
                text = "Verify",
                isLoading = uiState.isLoading,
                enabled = uiState.otpCode.length == 6,
                onClick = viewModel::verifyOtp
            )
        }
    }
}
