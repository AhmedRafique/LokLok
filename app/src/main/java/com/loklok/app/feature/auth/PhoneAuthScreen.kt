package com.loklok.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokButton
import com.loklok.app.core.designsystem.components.LokLokTopBar
import com.loklok.app.core.designsystem.theme.LokLokCallEnd
import com.loklok.app.core.designsystem.theme.LokLokPrimary

@Composable
fun PhoneAuthScreen(
    viewModel: PhoneAuthViewModel,
    onNavigateToOtp: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isOtpSent) {
        if (uiState.isOtpSent) {
            onNavigateToOtp(uiState.phoneNumber)
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
                text = "Lok Lok will send an SMS verification code to verify your phone number. Carrier SMS charges may apply.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Phone Number Input with Prefix
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = "+971",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.width(90.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LokLokPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    placeholder = { Text("50 123 4567") },
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
                Spacer(modifier = Modifier.height(8.dp))
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
