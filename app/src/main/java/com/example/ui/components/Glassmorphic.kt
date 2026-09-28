package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Reusable premium dark security background
@Composable
fun SecurityBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF060914), // Midnight space black
                        Color(0xFF0C1328), // Deep cyber navy
                        Color(0xFF080C1A)  // Slate obsidian
                    )
                )
            )
    ) {
        // Soft hardware-accelerated radial glows in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1F00F5FF), Color.Transparent),
                    radius = size.width * 0.75f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.15f)
                ),
                radius = size.width * 0.75f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.15f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x167C4DFF), Color.Transparent),
                    radius = size.width * 0.85f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.85f)
                ),
                radius = size.width * 0.85f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.85f)
            )
        }
        Box(modifier = Modifier.fillMaxSize(), content = content)
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = GlassBorder,
    backgroundColor: Color = GlassSurface,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed && onClick != null) 0.98f else 1f, label = "pressScale")

    // Shiny translucent linear-gradient for glass backdrop
    val glassGradient = Brush.verticalGradient(
        colors = listOf(
            backgroundColor.copy(alpha = 0.28f),
            backgroundColor.copy(alpha = 0.14f)
        )
    )

    // Luminous border gradient that gives premium 3D edge
    val borderGradient = Brush.linearGradient(
        colors = listOf(
            borderColor.copy(alpha = 0.65f),
            Color(0x0AFFFFFF),
            borderColor.copy(alpha = 0.25f)
        )
    )

    var cardModifier = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .shadow(
            elevation = if (onClick != null && isPressed) 4.dp else 12.dp,
            shape = shape,
            clip = false,
            spotColor = borderColor.copy(alpha = 0.35f),
            ambientColor = borderColor.copy(alpha = 0.15f)
        )
        .clip(shape)
        .background(glassGradient)
        .border(BorderStroke(1.dp, borderGradient), shape)

    if (onClick != null) {
        cardModifier = cardModifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onClick
        )
    }

    Column(
        modifier = cardModifier.padding(16.dp),
        content = content
    )
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    glowColor: Color = GlowingCyan,
    shape: Shape = RoundedCornerShape(14.dp),
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "buttonScale")

    // Back-illuminated glass button filling gradients
    val buttonGradient = if (enabled) {
        if (isPressed) {
            Brush.verticalGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.42f),
                    Color(0x3D141F3C)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.24f),
                    Color(0x1F141F3C)
                )
            )
        }
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x0DFFFFFF),
                Color(0x05FFFFFF)
            )
        )
    }

    // High intensity neon border stroke
    val finalBorderColor = if (enabled) {
        Brush.linearGradient(
            colors = listOf(
                glowColor,
                glowColor.copy(alpha = 0.3f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x1AFFFFFF),
                Color(0x08FFFFFF)
            )
        )
    }

    val finalTextColor = if (enabled) LightText else DimText

    Box(
        modifier = modifier
            .testTag(testTag)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (enabled && isPressed) 6.dp else (if (enabled) 14.dp else 0.dp),
                shape = shape,
                clip = false,
                spotColor = if (enabled) glowColor.copy(alpha = 0.6f) else Color.Transparent,
                ambientColor = if (enabled) glowColor.copy(alpha = 0.25f) else Color.Transparent
            )
            .clip(shape)
            .background(buttonGradient)
            .border(BorderStroke(1.5.dp, finalBorderColor), shape)
            .then(
                if (enabled) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = onClick
                ) else Modifier
            )
            .padding(vertical = 14.dp, horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) glowColor else DimText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = finalTextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    testTag: String = ""
) {
    val borderColor = if (isError) GlowRed else GlassBorder
    val shape = RoundedCornerShape(12.dp)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = DimText) },
        placeholder = { Text(placeholder, color = DimText.copy(alpha = 0.5f)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = LightText,
            unfocusedTextColor = LightText,
            focusedBorderColor = GlowingCyan,
            unfocusedBorderColor = borderColor,
            errorBorderColor = GlowRed,
            focusedContainerColor = Color(0x3D141F3C),
            unfocusedContainerColor = Color(0x1F141F3C),
            errorContainerColor = Color(0x1AFF1744)
        ),
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

@Composable
fun GlowBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .border(BorderStroke(1.2.dp, color.copy(alpha = 0.45f)), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmergencyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlowButton(
        text = text,
        onClick = onClick,
        glowColor = GlowRed,
        modifier = modifier,
        icon = Icons.Default.Warning,
        shape = RoundedCornerShape(12.dp)
    )
}
