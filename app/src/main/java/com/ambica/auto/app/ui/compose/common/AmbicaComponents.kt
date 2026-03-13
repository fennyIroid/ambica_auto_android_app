package com.ambica.auto.app.ui.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ambica.auto.app.R
import com.ambica.auto.app.ui.theme.colorSplashOrange

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorSplashOrange),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = Color.White)
    }
}

/**
 * @param passwordVisible If provided, the eye-toggle icon is shown. Pair with [onTogglePasswordVisibility].
 *                        If null (default), no toggle is shown and [isPassword] alone controls masking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbicaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean? = null,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    errorText: String? = null,
) {
    // When caller manages visibility externally use their value; otherwise fall back to local state
    var localVisible by remember { mutableStateOf(false) }
    val visible = passwordVisible ?: localVisible
    val toggleVisible: () -> Unit = onTogglePasswordVisibility ?: { localVisible = !localVisible }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = errorText != null,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorSplashOrange,
            cursorColor = colorSplashOrange,
        ),
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                Icon(
                    painter = painterResource(if (visible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                    contentDescription = if (visible) "Hide password" else "Show password",
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { toggleVisible() },
                )
            }
        } else null,
    )
    if (!errorText.isNullOrBlank()) {
        Text(
            text = errorText,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

