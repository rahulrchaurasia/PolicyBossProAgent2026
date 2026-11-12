package com.policyboss.policybosspro.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.ui.components.button.AppBackButton
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun EditPersonalInfoScreen(
    personalInfo: PersonalInformation,
    isSaving: Boolean,
    onSaveClick: (PersonalInformation) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember(personalInfo) { mutableStateOf(personalInfo.fullName) }
    var mobile by remember(personalInfo) { mutableStateOf(personalInfo.mobile) }
    var dob by remember(personalInfo) { mutableStateOf(personalInfo.dob) }
    var email by remember(personalInfo) { mutableStateOf(personalInfo.email) }
    var pan by remember(personalInfo) { mutableStateOf(personalInfo.pan) }
    var address by remember(personalInfo) { mutableStateOf(personalInfo.address) }
    var city by remember(personalInfo) { mutableStateOf(personalInfo.city) }
    var state by remember(personalInfo) { mutableStateOf(personalInfo.state) }
    var pincode by remember(personalInfo) { mutableStateOf(personalInfo.pincode) }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = AppColors.White,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                // Save Button with Right Arrow in Circle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (isSaving) AppColors.ButtonDisabled else AppColors.PrimaryBlue)
                        .clickable(enabled = !isSaving) {
                            onSaveClick(
                                personalInfo.copy(
                                    fullName = fullName,
                                    mobile = mobile,
                                    dob = dob,
                                    email = email,
                                    pan = pan,
                                    address = address,
                                    city = city,
                                    state = state,
                                    pincode = pincode
                                )
                            )
                        }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = AppColors.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        // Centered "Save"
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.White
                        )

                        // Circular white container with arrow on the far right
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.CenterEnd)
                                .clip(CircleShape)
                                .background(AppColors.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_right),
                                contentDescription = "Save and proceed",
                                tint = AppColors.PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Bar: Back Chevron & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBackButton(
                    onClick = onBackClick,
                    tint = AppColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Edit Personal Information",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Full name
            EditFormField(
                label = "Full name",
                value = fullName,
                onValueChange = { fullName = it },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Mobile Number (with +91 prefix)
            EditFormField(
                label = "Mobile Number",
                value = mobile,
                onValueChange = { mobile = it },
                prefix = "+91",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Date of birth (with calendar icon)
            EditFormField(
                label = "Date of birth",
                value = dob,
                onValueChange = { dob = it },
                trailingIconRes = R.drawable.ic_calendar
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Email
            EditFormField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. PAN
            EditFormField(
                label = "PAN",
                value = pan,
                onValueChange = { pan = it.uppercase() },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Address
            EditFormField(
                label = "Address",
                value = address,
                onValueChange = { address = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 7. City
            EditFormField(
                label = "City",
                value = city,
                onValueChange = { city = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 8. State
            EditFormField(
                label = "State",
                value = state,
                onValueChange = { state = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 9. PIN Code
            EditFormField(
                label = "PIN Code",
                value = pincode,
                onValueChange = { pincode = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    trailingIconRes: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.White)
                .border(1.dp, AppColors.BorderPrimary, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (prefix != null) {
                    Text(
                        text = prefix,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(AppColors.Dividers)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    ),
                    keyboardOptions = keyboardOptions,
                    singleLine = true,
                    cursorBrush = SolidColor(AppColors.PrimaryBlue)
                )

                if (trailingIconRes != null) {
                    Icon(
                        painter = painterResource(id = trailingIconRes),
                        contentDescription = null,
                        tint = AppColors.PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditPersonalInfoScreenPreview() {
    EditPersonalInfoScreen(
        personalInfo = AppDummyData.defaultUserProfile.personalInfo,
        isSaving = false,
        onSaveClick = {},
        onBackClick = {}
    )
}

