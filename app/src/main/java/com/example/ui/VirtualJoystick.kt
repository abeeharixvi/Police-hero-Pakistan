package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    radiusDp: Float = 60f,
    onValueChange: (x: Float, y: Float) -> Unit
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size((radiusDp * 2).dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dragVector = offset - center
                        val dist = dragVector.getDistance()
                        val maxR = size.width / 2f
                        val clampedDist = dist.coerceAtMost(maxR)
                        val angle = atan2(dragVector.y, dragVector.x)
                        val clampedOffset = Offset(cos(angle) * clampedDist, sin(angle) * clampedDist)
                        thumbOffset = clampedOffset
                        onValueChange(clampedOffset.x / maxR, clampedOffset.y / maxR)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val maxR = size.width / 2f
                        val newOffset = thumbOffset + dragAmount
                        val dist = newOffset.getDistance()
                        val clampedDist = dist.coerceAtMost(maxR)
                        val angle = atan2(newOffset.y, newOffset.x)
                        val clampedOffset = Offset(cos(angle) * clampedDist, sin(angle) * clampedDist)
                        thumbOffset = clampedOffset
                        onValueChange(clampedOffset.x / maxR, clampedOffset.y / maxR)
                    },
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onValueChange(0f, 0f)
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onValueChange(0f, 0f)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f

            // Outer Base Ring
            drawCircle(
                color = Color(0x770B192C),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = Color(0xFF1E88E5),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Inner Thumb Stick
            drawCircle(
                color = Color(0xFF64B5F6),
                radius = outerRadius * 0.4f,
                center = center + thumbOffset
            )
            drawCircle(
                color = Color(0xFFFFFFFF),
                radius = outerRadius * 0.4f,
                center = center + thumbOffset,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
