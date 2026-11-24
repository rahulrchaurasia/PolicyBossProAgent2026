package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VahanDataCard(
    vahanDescription: String,
    registrationNumber: String,
    modifier: Modifier = Modifier,
    isNewCar: Boolean = false
) {
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    val hasValidRegNo = !isNewCar && registrationNumber.isNotBlank() && !registrationNumber.equals("NEW CAR", ignoreCase = true)

    val blueGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF157EFB),
            Color(0xFF66B3FF)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(blueGradient)
    ) {
        // Decorative background watermark shape for depth
        Box(
            modifier = Modifier
                .size(90.dp)
                .offset(x = (-25).dp, y = (-20).dp)
                .background(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side — Car + Verified Shield Visual
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                // Subtle translucent white watermark circle behind car icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                )

                // White car vector icon (36.dp-40.dp)
                Icon(
                    imageVector = Icons.Rounded.DirectionsCar,
                    contentDescription = "Car",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomEnd)
                )

                // Crisp white verified shield badge positioned upper-left of the car
                Icon(
                    imageVector = Icons.Rounded.VerifiedUser,
                    contentDescription = "Verified Vahan",
                    tint = Color.White,
                    modifier = Modifier
                        .size(19.dp)
                        .align(Alignment.TopStart)
                        .offset(x = 2.dp, y = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Right Side — Vahan Description & Registration Number
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Caption "Vahan Data :"
                Text(
                    text = "Vahan Data :",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 2. Full Vehicle Description
                Text(
                    text = vahanDescription,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 3. Registration Number Row (Directly Below Vahan Data)
                if (hasValidRegNo) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = registrationNumber,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.6.sp
                        )

                        // Inline copy icon button
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(registrationNumber))
                                    Toast.makeText(context, "Registration number copied", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = "Copy Registration Number",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                } else if (isNewCar) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NEW CAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.6.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Vahan Data Card - Registered Vehicle", showBackground = true)
@Composable
private fun VahanDataCardPreview() {
    VahanDataCard(
        vahanDescription = "KIA INDIA PRIVATE LIMITED, SELTOS D1.5 6AT GTX PLUS, DIESEL",
        registrationNumber = "RJ 14 CV 0002",
        isNewCar = false,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Vahan Data Card - New Car", showBackground = true)
@Composable
private fun VahanDataCardNewCarPreview() {
    VahanDataCard(
        vahanDescription = "HYUNDAI MOTOR INDIA, CRETA 1.5 SX (O) CVT, PETROL",
        registrationNumber = "",
        isNewCar = true,
        modifier = Modifier.padding(16.dp)
    )
}
