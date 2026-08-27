package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.domain.models.UserAccount
import com.example.domain.models.UserRole
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmBrownDark
import com.example.ui.theme.FarmBrownHeader
import com.example.ui.theme.FarmBrownLight
import com.example.ui.theme.FarmBrownPrimary
import com.example.ui.theme.FarmBrownSecondary
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import com.example.ui.theme.FarmYellowAccent

enum class AuthMode {
    NONE,
    FARMER_LOGIN,
    FARMER_REGISTER,
    ADMIN_LOGIN
}

@Composable
fun LandingAuthScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onFarmerLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onFarmerRegister: (name: String, phone: String, rsbsa: String, prov: String, mun: String, area: Double, crop: String) -> Result<UserAccount>,
    onAdminLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeAuthMode by remember { mutableStateOf(AuthMode.NONE) }
    var showLangMenu by remember { mutableStateOf(false) }

    BackHandler(enabled = activeAuthMode != AuthMode.NONE) {
        activeAuthMode = AuthMode.NONE
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F3))
            .testTag("landing_auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Top Section with Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                FarmBrownHeader,
                                FarmBrownPrimary,
                                Color(0xFF8D5B43)
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    // Top Bar with Language selector & Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Official Institutional Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.ENGLISH -> "DA-PhilRice Nutrient Advisory"
                                    AppLanguage.TAGALOG -> "Gabay sa Nutrisyon ng Palay (DA-PhilRice)"
                                    AppLanguage.TAGLISH -> "DA-PhilRice Nutrient Advisory"
                                    AppLanguage.ILOCANO -> "Pammagbaga iti Abono (DA-PhilRice)"
                                    AppLanguage.CEBUANO -> "Giya sa Nutrisyon (DA-PhilRice)"
                                },
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Language Selector
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .clickable { showLangMenu = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("btn_landing_language")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = FarmBrownPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = currentLanguage.displayName,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmBrownPrimary
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = FarmBrownPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showLangMenu,
                                onDismissRequest = { showLangMenu = false },
                                modifier = Modifier
                                    .background(Color.White)
                                    .padding(vertical = 4.dp)
                            ) {
                                AppLanguage.values().forEach { lang ->
                                    val isSelected = lang == currentLanguage
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = FarmBrownPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                                Text(
                                                    text = lang.displayName,
                                                    color = if (isSelected) FarmBrownPrimary else Color(0xFF2A1E17),
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 13.5.sp
                                                )
                                            }
                                        },
                                        onClick = {
                                            onLanguageSelected(lang)
                                            showLangMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title & Description
                    Text(
                        text = "NutriGuide PH",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Precision Farm Land Measurement & RCEF Fertilizer Management System"
                            AppLanguage.TAGALOG -> "Eksaktong Pagsusukat ng Sakahan at Gabay sa Pataba ng RCEF"
                            AppLanguage.TAGLISH -> "Precision Farm Land Measurement & RCEF Fertilizer Calculator"
                            AppLanguage.ILOCANO -> "Eksakto a Panagrukod ti Talon ken Sistema ti Abono ti RCEF"
                            AppLanguage.CEBUANO -> "Eksaktong Pagsukod sa Uma ug Sistema sa Abono sa RCEF"
                        },
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3 Feature Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FeaturePill(
                            icon = Icons.Default.GpsFixed,
                            label = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "GPS Parcel Walk"
                                AppLanguage.TAGALOG -> "GPS Sukat ng Lupa"
                                AppLanguage.TAGLISH -> "GPS Parcel Walk"
                                AppLanguage.ILOCANO -> "GPS Rukod ti Daga"
                                AppLanguage.CEBUANO -> "GPS Sukod sa Yuta"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FeaturePill(
                            icon = Icons.Default.Science,
                            label = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Soil N-P-K Bags"
                                AppLanguage.TAGALOG -> "Kalkula ng N-P-K"
                                AppLanguage.TAGLISH -> "Soil N-P-K Bags"
                                AppLanguage.ILOCANO -> "Bolsa ti N-P-K"
                                AppLanguage.CEBUANO -> "Kalkula sa N-P-K"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FeaturePill(
                            icon = Icons.Default.FactCheck,
                            label = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "RSBSA Records"
                                AppLanguage.TAGALOG -> "Rekord ng RSBSA"
                                AppLanguage.TAGLISH -> "RSBSA Records"
                                AppLanguage.ILOCANO -> "Rekord ti RSBSA"
                                AppLanguage.CEBUANO -> "Rekord sa RSBSA"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Landing Action Hub
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Choose Portal Access"
                        AppLanguage.TAGALOG -> "Pumili ng Pag-login o Pag-rehistro"
                        AppLanguage.TAGLISH -> "Choose Your Login or Sign Up Portal"
                        AppLanguage.ILOCANO -> "Pilien ti Pag-rekord wenno Login"
                        AppLanguage.CEBUANO -> "Pilia ang Portal sa Pag-login"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Access tailored tools for field farmers or authorized municipal agronomists."
                        AppLanguage.TAGALOG -> "Pumili ayon sa iyong tungkulin: magsasaka o opisyal ng Department of Agriculture."
                        AppLanguage.TAGLISH -> "Tailored portal para sa mga magsasaka at accredited DA / PhilRice personnel."
                        AppLanguage.ILOCANO -> "Para kadagiti mannalon wenno opisial ti Department of Agriculture."
                        AppLanguage.CEBUANO -> "Para sa mga mag-uuma o mga opisyal sa Department of Agriculture."
                    },
                    fontSize = 13.sp,
                    color = FarmTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card 1: Farmer Sign In (Dedicated Login Card)
                PortalAccessCard(
                    title = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "🌾 Farmer Sign In"
                        AppLanguage.TAGALOG -> "🌾 Mag-login bilang Magsasaka"
                        AppLanguage.TAGLISH -> "🌾 Farmer Sign In / Mag-login"
                        AppLanguage.ILOCANO -> "🌾 Sumrek kas Mannalon"
                        AppLanguage.CEBUANO -> "🌾 Mag-login isip Mag-uuma"
                    },
                    subtitle = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Access saved farm land measurements, nutrient recommendations, and RSBSA voucher eligibility."
                        AppLanguage.TAGALOG -> "I-access ang na-save na sukat ng sakahan, rekomendasyon sa pataba, at RSBSA voucher eligibility."
                        AppLanguage.TAGLISH -> "I-access ang na-save na farm land measurements, nutrient recommendations, at RSBSA voucher."
                        AppLanguage.ILOCANO -> "Kitaen dagiti naidulin a rukod ti talon, pammagbaga iti abono, ken RSBSA voucher."
                        AppLanguage.CEBUANO -> "I-access ang na-save nga sukod sa uma, rekomendasyon sa abono, ug RSBSA voucher."
                    },
                    badge = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "LOGIN PORTAL"
                        AppLanguage.TAGALOG -> "PAG-LOGIN"
                        AppLanguage.TAGLISH -> "LOGIN PORTAL"
                        AppLanguage.ILOCANO -> "PAG-SUMREK"
                        AppLanguage.CEBUANO -> "PORTAL SA PAG-LOGIN"
                    },
                    badgeColor = FarmBrownPrimary,
                    primaryButtonText = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Sign In as Farmer (Login)"
                        AppLanguage.TAGALOG -> "Mag-login bilang Magsasaka"
                        AppLanguage.TAGLISH -> "Sign In as Farmer (Login)"
                        AppLanguage.ILOCANO -> "Sumrek kas Mannalon"
                        AppLanguage.CEBUANO -> "Mag-login isip Mag-uuma"
                    },
                    primaryButtonTag = "btn_landing_farmer_login",
                    icon = Icons.Default.Login,
                    onPrimaryClick = { activeAuthMode = AuthMode.FARMER_LOGIN }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: Farmer Sign Up / Register (Dedicated Sign Up Card)
                PortalAccessCard(
                    title = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "🌱 New Farmer? Sign Up"
                        AppLanguage.TAGALOG -> "🌱 Bagong Magsasaka? Mag-rehistro"
                        AppLanguage.TAGLISH -> "🌱 New Farmer? Sign Up / Register"
                        AppLanguage.ILOCANO -> "🌱 Baro a Mannalon? Agparehistro"
                        AppLanguage.CEBUANO -> "🌱 Bag-ong Mag-uuma? Pag-rehistro"
                    },
                    subtitle = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Create a new farm profile to save GPS parcel boundaries and receive tailored N-P-K fertilizer schedules."
                        AppLanguage.TAGALOG -> "Gumawa ng profile ng sakahan upang ma-save ang sukat ng lupa at makatanggap ng tamang gabay sa pataba."
                        AppLanguage.TAGLISH -> "Gumawa ng bagong farm profile para ma-save ang GPS parcel measurements at N-P-K schedule."
                        AppLanguage.ILOCANO -> "Mangaramid iti profile ti talon tapno maidulin ti rukod ti daga ken maaddaan iti pammagbaga iti abono."
                        AppLanguage.CEBUANO -> "Paghimo og profile sa uma aron ma-save ang sukod sa yuta ug makadawat og eksaktong giya sa abono."
                    },
                    badge = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "NEW ACCOUNT"
                        AppLanguage.TAGALOG -> "BAGONG ACCOUNT"
                        AppLanguage.TAGLISH -> "NEW ACCOUNT"
                        AppLanguage.ILOCANO -> "BARO NGA ACCOUNT"
                        AppLanguage.CEBUANO -> "BAG-ONG ACCOUNT"
                    },
                    badgeColor = FarmGreenDark,
                    primaryButtonText = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Create Farmer Account (Sign Up)"
                        AppLanguage.TAGALOG -> "Mag-rehistro ng Bagong Account"
                        AppLanguage.TAGLISH -> "Create Farmer Account (Sign Up)"
                        AppLanguage.ILOCANO -> "Agrehistro ti Baro nga Account"
                        AppLanguage.CEBUANO -> "Paghimo og Bag-ong Account"
                    },
                    primaryButtonTag = "btn_landing_farmer_signup",
                    buttonColor = FarmGreenDark,
                    icon = Icons.Default.PersonAdd,
                    onPrimaryClick = { activeAuthMode = AuthMode.FARMER_REGISTER }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Card 3: Authorized Admin / Personnel Portal
                PortalAccessCard(
                    title = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "🛡️ DA / PhilRice Personnel Portal"
                        AppLanguage.TAGALOG -> "🛡️ Portal ng DA at PhilRice Personnel"
                        AppLanguage.TAGLISH -> "🛡️ DA / PhilRice Official Portal"
                        AppLanguage.ILOCANO -> "🛡️ Portal ti Opisial ti DA ken PhilRice"
                        AppLanguage.CEBUANO -> "🛡️ Portal sa DA ug PhilRice Personnel"
                    },
                    subtitle = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "For Municipal Agricultural Officers (MAO), PhilRice Extensionists, and Regional Directors to monitor farmers and fertilizer allocations."
                        AppLanguage.TAGALOG -> "Para sa mga Municipal Agricultural Officers (MAO), PhilRice Extensionists, at Regional Directors upang i-monitor ang magsasaka at pamamahagi ng pataba."
                        AppLanguage.TAGLISH -> "Para sa Municipal Agricultural Officers (MAO), PhilRice Extensionists, at Regional Directors upang i-monitor ang allocations."
                        AppLanguage.ILOCANO -> "Para kadagiti Municipal Agricultural Officers (MAO), PhilRice Extensionists, ken Regional Directors."
                        AppLanguage.CEBUANO -> "Para sa mga Municipal Agricultural Officers (MAO), PhilRice Extensionists, ug Regional Directors."
                    },
                    badge = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "RESTRICTED ACCESS"
                        AppLanguage.TAGALOG -> "OPISYAL NA ACCESS"
                        AppLanguage.TAGLISH -> "RESTRICTED ACCESS"
                        AppLanguage.ILOCANO -> "PARA ITI OPISIAL"
                        AppLanguage.CEBUANO -> "LIMITADONG ACCESS"
                    },
                    badgeColor = Color(0xFF0D47A1),
                    primaryButtonText = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Official Admin Sign In"
                        AppLanguage.TAGALOG -> "Mag-login bilang DA Official"
                        AppLanguage.TAGLISH -> "Official Admin Sign In"
                        AppLanguage.ILOCANO -> "Sumrek kas DA Opisial"
                        AppLanguage.CEBUANO -> "Mag-login isip DA Opisiyal"
                    },
                    primaryButtonTag = "btn_landing_admin_login",
                    isBlue = true,
                    icon = Icons.Default.Shield,
                    onPrimaryClick = { activeAuthMode = AuthMode.ADMIN_LOGIN }
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // Interactive Login & Sign Up Sheet / Dialog
        AnimatedVisibility(
            visible = activeAuthMode != AuthMode.NONE,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            LandingAuthFormModal(
                authMode = activeAuthMode,
                currentLanguage = currentLanguage,
                onDismiss = { activeAuthMode = AuthMode.NONE },
                onSwitchMode = { activeAuthMode = it },
                onFarmerLogin = onFarmerLogin,
                onFarmerRegister = onFarmerRegister,
                onAdminLogin = onAdminLogin
            )
        }
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.22f))
            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFFE082),
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
private fun PortalAccessCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    primaryButtonText: String,
    primaryButtonTag: String,
    isBlue: Boolean = false,
    buttonColor: Color? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    secondaryButtonTag: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.5.dp,
                if (isBlue) Color(0xFFBBDEFB) else FarmBorder,
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isBlue) Color(0xFFF4F8FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBlue) Color(0xFF0D47A1) else FarmBrownHeader
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = FarmTextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            val effectiveContainerColor = buttonColor ?: if (isBlue) Color(0xFF0D47A1) else FarmBrownPrimary
            val effectiveIcon = icon ?: if (isBlue) Icons.Default.Shield else Icons.Default.Agriculture

            Button(
                onClick = onPrimaryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = effectiveContainerColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag(primaryButtonTag)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = effectiveIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = primaryButtonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (secondaryButtonText != null && onSecondaryClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onSecondaryClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmBrownPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FarmBrownPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag(secondaryButtonTag ?: "btn_secondary_action")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = secondaryButtonText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LandingAuthFormModal(
    authMode: AuthMode,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSwitchMode: (AuthMode) -> Unit,
    onFarmerLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onFarmerRegister: (name: String, phone: String, rsbsa: String, prov: String, mun: String, area: Double, crop: String) -> Result<UserAccount>,
    onAdminLogin: (identifier: String, passcode: String) -> Result<UserAccount>
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .clickable(enabled = false) {}
                    .testTag("landing_auth_modal_sheet"),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (authMode == AuthMode.ADMIN_LOGIN) Color(0xFF1565C0).copy(alpha = 0.12f)
                                        else FarmBrownLight
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (authMode == AuthMode.ADMIN_LOGIN) Icons.Default.Shield else Icons.Default.Agriculture,
                                    contentDescription = null,
                                    tint = if (authMode == AuthMode.ADMIN_LOGIN) Color(0xFF1565C0) else FarmBrownPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = when (authMode) {
                                    AuthMode.FARMER_LOGIN -> when (currentLanguage) {
                                        AppLanguage.ENGLISH -> "Farmer Login"
                                        AppLanguage.TAGALOG -> "Login ng Magsasaka"
                                        AppLanguage.TAGLISH -> "Farmer Login"
                                        AppLanguage.ILOCANO -> "Panag-login ti Mannalon"
                                        AppLanguage.CEBUANO -> "Pag-login sa Mag-uuma"
                                    }
                                    AuthMode.FARMER_REGISTER -> when (currentLanguage) {
                                        AppLanguage.ENGLISH -> "Farmer Sign Up / Registration"
                                        AppLanguage.TAGALOG -> "Pagpaparehistro ng Magsasaka"
                                        AppLanguage.TAGLISH -> "Farmer Sign Up / Registration"
                                        AppLanguage.ILOCANO -> "Panagrehistro ti Mannalon"
                                        AppLanguage.CEBUANO -> "Pagparehistro sa Mag-uuma"
                                    }
                                    AuthMode.ADMIN_LOGIN -> when (currentLanguage) {
                                        AppLanguage.ENGLISH -> "DA Official Admin Sign In"
                                        AppLanguage.TAGALOG -> "Login ng Opisyal ng DA"
                                        AppLanguage.TAGLISH -> "DA Official Admin Sign In"
                                        AppLanguage.ILOCANO -> "Panag-login ti DA Opisial"
                                        AppLanguage.CEBUANO -> "Pag-login sa DA Opisyal"
                                    }
                                    AuthMode.NONE -> "Authentication"
                                },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (authMode == AuthMode.ADMIN_LOGIN) Color(0xFF0D47A1) else FarmTextDark
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("btn_close_landing_modal")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF757575)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    when (authMode) {
                        AuthMode.FARMER_LOGIN -> {
                            FarmerLoginForm(
                                currentLanguage = currentLanguage,
                                onLogin = onFarmerLogin,
                                onSwitchToRegister = { onSwitchMode(AuthMode.FARMER_REGISTER) },
                                onSwitchToAdmin = { onSwitchMode(AuthMode.ADMIN_LOGIN) }
                            )
                        }
                        AuthMode.FARMER_REGISTER -> {
                            FarmerRegisterForm(
                                currentLanguage = currentLanguage,
                                onRegister = onFarmerRegister,
                                onSwitchToLogin = { onSwitchMode(AuthMode.FARMER_LOGIN) }
                            )
                        }
                        AuthMode.ADMIN_LOGIN -> {
                            AdminLoginForm(
                                currentLanguage = currentLanguage,
                                onAdminLogin = onAdminLogin,
                                onSwitchToFarmer = { onSwitchMode(AuthMode.FARMER_LOGIN) }
                            )
                        }
                        AuthMode.NONE -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun farmerInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF2A1E17),
    unfocusedTextColor = Color(0xFF2A1E17),
    disabledTextColor = Color(0xFF424242),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFFFFFFF),
    focusedLabelColor = Color(0xFF3E2723),
    unfocusedLabelColor = Color(0xFF4E342E),
    focusedPlaceholderColor = Color(0xFF757575),
    unfocusedPlaceholderColor = Color(0xFF757575),
    focusedBorderColor = FarmBrownPrimary,
    unfocusedBorderColor = Color(0xFF8D6E63),
    cursorColor = Color(0xFF3E2723),
    focusedLeadingIconColor = FarmBrownPrimary,
    unfocusedLeadingIconColor = Color(0xFF5D4037),
    focusedTrailingIconColor = FarmBrownPrimary,
    unfocusedTrailingIconColor = Color(0xFF5D4037)
)

@Composable
private fun adminInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0D47A1),
    unfocusedTextColor = Color(0xFF1E211D),
    disabledTextColor = Color(0xFF424242),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFFFFFFF),
    focusedLabelColor = Color(0xFF1565C0),
    unfocusedLabelColor = Color(0xFF37474F),
    focusedPlaceholderColor = Color(0xFF757575),
    unfocusedPlaceholderColor = Color(0xFF757575),
    focusedBorderColor = Color(0xFF1565C0),
    unfocusedBorderColor = Color(0xFF90CAF9),
    cursorColor = Color(0xFF0D47A1),
    focusedLeadingIconColor = Color(0xFF1565C0),
    unfocusedLeadingIconColor = Color(0xFF455A64),
    focusedTrailingIconColor = Color(0xFF1565C0),
    unfocusedTrailingIconColor = Color(0xFF757575)
)

@Composable
private fun FarmerLoginForm(
    currentLanguage: AppLanguage,
    onLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onSwitchToRegister: () -> Unit,
    onSwitchToAdmin: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Enter your RSBSA ID or Mobile Number to access your farm account."
                AppLanguage.TAGALOG -> "I-enter ang iyong RSBSA ID o Mobile Number upang buksan ang iyong farm account."
                AppLanguage.TAGLISH -> "Enter your RSBSA ID or Mobile Number upang buksan ang iyong farm account."
                AppLanguage.ILOCANO -> "Ikabil ti RSBSA ID wenno Mobile Number tapno mabuksan ti account ti talon."
                AppLanguage.CEBUANO -> "Isulod ang imong RSBSA ID o Mobile Number aron maablihan ang account sa uma."
            },
            fontSize = 12.sp,
            color = FarmTextSecondary,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it; errorMessage = null },
            label = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Mobile Phone or RSBSA Number"
                        AppLanguage.TAGALOG -> "Mobile Phone o RSBSA Number"
                        AppLanguage.TAGLISH -> "Mobile Phone or RSBSA Number"
                        AppLanguage.ILOCANO -> "Mobile Phone wenno RSBSA Number"
                        AppLanguage.CEBUANO -> "Mobile Phone o RSBSA Number"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037),
                    fontWeight = FontWeight.Medium
                )
            },
            placeholder = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "0917-123-4567 or 03-49-12-00421"
                        AppLanguage.TAGALOG -> "0917-123-4567 o 03-49-12-00421"
                        AppLanguage.TAGLISH -> "0917-123-4567 or 03-49-12-00421"
                        AppLanguage.ILOCANO -> "0917-123-4567 wenno 03-49-12-00421"
                        AppLanguage.CEBUANO -> "0917-123-4567 o 03-49-12-00421"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF8D6E63)
                )
            },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FarmBrownPrimary) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("landing_input_farmer_id"),
            colors = farmerInputColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = passcode,
            onValueChange = { passcode = it; errorMessage = null },
            label = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Passcode or PIN"
                        AppLanguage.TAGALOG -> "Passcode o PIN"
                        AppLanguage.TAGLISH -> "Passcode or PIN"
                        AppLanguage.ILOCANO -> "Passcode wenno PIN"
                        AppLanguage.CEBUANO -> "Passcode o PIN"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037),
                    fontWeight = FontWeight.Medium
                )
            },
            placeholder = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Enter your passcode"
                        AppLanguage.TAGALOG -> "Ilagay ang iyong passcode"
                        AppLanguage.TAGLISH -> "Enter your passcode"
                        AppLanguage.ILOCANO -> "Ikabil ti passcode"
                        AppLanguage.CEBUANO -> "Isulod ang passcode"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF8D6E63)
                )
            },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FarmBrownPrimary) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = FarmBrownPrimary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("landing_input_farmer_pass"),
            colors = farmerInputColors()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚠️ $errorMessage",
                color = Color(0xFFD32F2F),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val res = onLogin(identifier.ifBlank { "0917-123-4567" }, passcode.ifBlank { "farmer123" })
                if (res.isFailure) {
                    errorMessage = res.exceptionOrNull()?.message ?: "Login failed"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("landing_btn_submit_farmer_login"),
            colors = ButtonDefaults.buttonColors(containerColor = FarmBrownPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Sign In to Farm Account"
                    AppLanguage.TAGALOG -> "Mag-login sa Aking Farm Account"
                    AppLanguage.TAGLISH -> "Sign In to Farm Account"
                    AppLanguage.ILOCANO -> "Sumrek iti Farm Account"
                    AppLanguage.CEBUANO -> "Mag-login sa Farm Account"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Demo Quick Fill Chips
        Text(
            text = when (currentLanguage) {
                AppLanguage.ENGLISH -> "⚡ Try Demo Registered Farmers:"
                AppLanguage.TAGALOG -> "⚡ Subukan ang mga Demo Registered Farmers:"
                AppLanguage.TAGLISH -> "⚡ Try Demo Registered Farmers:"
                AppLanguage.ILOCANO -> "⚡ Padasen dagiti Demo a Mannalon:"
                AppLanguage.CEBUANO -> "⚡ Sulayi ang mga Demo nga Mag-uuma:"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF757575)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    identifier = "0917-123-4567"
                    passcode = "farmer123"
                    onLogin("0917-123-4567", "farmer123")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("landing_quick_juan"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌾 Juan Dela Cruz", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmBrownDark)
                    Text("Nueva Ecija • 2.4 Ha", fontSize = 9.sp, color = Color(0xFF616161))
                }
            }

            OutlinedButton(
                onClick = {
                    identifier = "0918-987-6543"
                    passcode = "isabela2026"
                    onLogin("0918-987-6543", "isabela2026")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("landing_quick_maria"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌾 Maria Santos", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmBrownDark)
                    Text("Isabela • 1.8 Ha", fontSize = 9.sp, color = Color(0xFF616161))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSwitchToRegister,
                modifier = Modifier.testTag("landing_switch_to_signup")
            ) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "New Farmer? Sign Up"
                        AppLanguage.TAGALOG -> "Bagong Magsasaka? Mag-sign Up"
                        AppLanguage.TAGLISH -> "New Farmer? Mag-sign Up"
                        AppLanguage.ILOCANO -> "Baro a Mannalon? Agrehistro"
                        AppLanguage.CEBUANO -> "Bag-ong Mag-uuma? Pag-sign Up"
                    },
                    fontSize = 12.sp,
                    color = FarmBrownPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onSwitchToAdmin,
                modifier = Modifier.testTag("landing_switch_to_admin")
            ) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "DA Official Portal"
                        AppLanguage.TAGALOG -> "DA Official Portal"
                        AppLanguage.TAGLISH -> "DA Official Portal"
                        AppLanguage.ILOCANO -> "DA Opisial a Portal"
                        AppLanguage.CEBUANO -> "DA Opisyal nga Portal"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF0D47A1),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FarmerRegisterForm(
    currentLanguage: AppLanguage,
    onRegister: (name: String, phone: String, rsbsa: String, prov: String, mun: String, area: Double, crop: String) -> Result<UserAccount>,
    onSwitchToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var rsbsaNumber by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("Nueva Ecija") }
    var municipality by remember { mutableStateOf("Science City of Muñoz") }
    var farmAreaText by remember { mutableStateOf("2.0") }
    var cropVariety by remember { mutableStateOf("NSIC Rc 222 (Tubigan 21)") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Register your name and farm land details for official DA fertilizer computation."
                AppLanguage.TAGALOG -> "Mag-rehistro ng iyong pangalan at lupang pansakahan para sa opisyal na DA fertilizer computation."
                AppLanguage.TAGLISH -> "Register your name at lupang pansakahan para sa DA fertilizer computation."
                AppLanguage.ILOCANO -> "Irehistro ti nagan ken daga ti talon para iti pammagbaga iti abono."
                AppLanguage.CEBUANO -> "Iparehistro ang imong ngalan ug yuta sa uma alang sa kalkula sa abono."
            },
            fontSize = 12.sp,
            color = FarmTextSecondary,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; errorMessage = null },
            label = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Farmer Full Name"
                        AppLanguage.TAGALOG -> "Pangalan ng Magsasaka (Full Name)"
                        AppLanguage.TAGLISH -> "Farmer Full Name"
                        AppLanguage.ILOCANO -> "Nagan ti Mannalon"
                        AppLanguage.CEBUANO -> "Pangalan sa Mag-uuma"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037),
                    fontWeight = FontWeight.Medium
                )
            },
            placeholder = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "e.g. Danilo Ramos"
                        AppLanguage.TAGALOG -> "hal. Danilo Ramos"
                        AppLanguage.TAGLISH -> "e.g. Danilo Ramos"
                        AppLanguage.ILOCANO -> "kas pangarigan Danilo Ramos"
                        AppLanguage.CEBUANO -> "pananglitan Danilo Ramos"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF8D6E63)
                )
            },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FarmBrownPrimary) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("landing_reg_input_name"),
            colors = farmerInputColors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; errorMessage = null },
                label = { Text("Mobile Phone", fontSize = 12.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
                placeholder = { Text("0917-xxx-xxxx", fontSize = 11.sp, color = Color(0xFF8D6E63)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FarmBrownPrimary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(1f)
                    .testTag("landing_reg_input_phone"),
                colors = farmerInputColors()
            )

            OutlinedTextField(
                value = rsbsaNumber,
                onValueChange = { rsbsaNumber = it },
                label = {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "RSBSA ID (If Available)"
                            AppLanguage.TAGALOG -> "RSBSA ID (Kung Mayroon)"
                            AppLanguage.TAGLISH -> "RSBSA ID (If Available)"
                            AppLanguage.ILOCANO -> "RSBSA ID (Nu Adda)"
                            AppLanguage.CEBUANO -> "RSBSA ID (Kung Naa)"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037),
                        fontWeight = FontWeight.Medium
                    )
                },
                placeholder = { Text("03-49-12-...", fontSize = 11.sp, color = Color(0xFF8D6E63)) },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = FarmBrownPrimary) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(1f)
                    .testTag("landing_reg_input_rsbsa"),
                colors = farmerInputColors()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = municipality,
                onValueChange = { municipality = it },
                label = {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Municipality / City"
                            AppLanguage.TAGALOG -> "Munisipyo / Bayan"
                            AppLanguage.TAGLISH -> "Municipality / City"
                            AppLanguage.ILOCANO -> "Ili / Siudad"
                            AppLanguage.CEBUANO -> "Munisipyo / Dakbayan"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037),
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = FarmBrownPrimary) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("landing_reg_input_mun"),
                colors = farmerInputColors()
            )

            OutlinedTextField(
                value = farmAreaText,
                onValueChange = { farmAreaText = it },
                label = {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Farm Area (Ha)"
                            AppLanguage.TAGALOG -> "Laki (Ha)"
                            AppLanguage.TAGLISH -> "Farm Area (Ha)"
                            AppLanguage.ILOCANO -> "Kinasakop (Ha)"
                            AppLanguage.CEBUANO -> "Gidak-on (Ha)"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037),
                        fontWeight = FontWeight.Medium
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(0.8f)
                    .testTag("landing_reg_input_area"),
                colors = farmerInputColors()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cropVariety,
            onValueChange = { cropVariety = it },
            label = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Main Rice Variety"
                        AppLanguage.TAGALOG -> "Pangunahing Pananim / Variety"
                        AppLanguage.TAGLISH -> "Main Crop / Variety"
                        AppLanguage.ILOCANO -> "Pangruna a Mula / Barayti"
                        AppLanguage.CEBUANO -> "Pangulong Tanom / Barayti"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037),
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = { Icon(Icons.Default.Eco, contentDescription = null, tint = FarmBrownPrimary) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("landing_reg_input_crop"),
            colors = farmerInputColors()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚠️ $errorMessage",
                color = Color(0xFFD32F2F),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val areaVal = farmAreaText.toDoubleOrNull() ?: 1.0
                val res = onRegister(
                    fullName.ifBlank { "Danilo Ramos" },
                    phone.ifBlank { "0917-888-9999" },
                    rsbsaNumber,
                    province,
                    municipality,
                    areaVal,
                    cropVariety
                )
                if (res.isFailure) {
                    errorMessage = res.exceptionOrNull()?.message ?: "Registration failed"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("landing_btn_submit_farmer_reg"),
            colors = ButtonDefaults.buttonColors(containerColor = FarmBrownPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Complete Sign Up (Register)"
                    AppLanguage.TAGALOG -> "Kumpletuhin ang Pag-sign Up (Register)"
                    AppLanguage.TAGLISH -> "Complete Sign Up (Register)"
                    AppLanguage.ILOCANO -> "Ging-ginnawaen ti Panagrehistro"
                    AppLanguage.CEBUANO -> "Kompletuha ang Pagparehistro"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onSwitchToLogin) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Already have an account? Sign in here"
                        AppLanguage.TAGALOG -> "May account na? Mag-login dito"
                        AppLanguage.TAGLISH -> "Already have an account? Mag-login dito"
                        AppLanguage.ILOCANO -> "Adda amon account? Sumrek ditoy"
                        AppLanguage.CEBUANO -> "Adunay account na? Mag-login dinhi"
                    },
                    fontSize = 12.sp,
                    color = FarmBrownPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminLoginForm(
    currentLanguage: AppLanguage,
    onAdminLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onSwitchToFarmer: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1565C0).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Authorized Personnel Portal (DA & PhilRice)"
                    AppLanguage.TAGALOG -> "Portal ng Awtorisadong Tauhan (DA & PhilRice)"
                    AppLanguage.TAGLISH -> "Authorized Personnel Portal (DA & PhilRice)"
                    AppLanguage.ILOCANO -> "Portal ti Awtorisado a Personel (DA & PhilRice)"
                    AppLanguage.CEBUANO -> "Portal sa Awtorisadong Kawani (DA & PhilRice)"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1)
            )
        }

        Text(
            text = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Enter your official government email or Personnel ID and Security Passcode to enter the Regional Command Console."
                AppLanguage.TAGALOG -> "I-enter ang iyong opisyal na government email o Personnel ID at Security Passcode para pumasok sa Regional Command Console."
                AppLanguage.TAGLISH -> "Enter your official government email or Personnel ID at Security Passcode to access Regional Command Console."
                AppLanguage.ILOCANO -> "Ikabil ti opisyal a government email wenno Personnel ID ken Security Passcode tapno sumrek iti Console."
                AppLanguage.CEBUANO -> "Isulod ang opisyal nga government email o Personnel ID ug Security Passcode aron makasulod sa Console."
            },
            fontSize = 12.sp,
            color = Color(0xFF455A64),
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it; errorMessage = null },
            label = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Government Email / Personnel ID"
                        AppLanguage.TAGALOG -> "Government Email / Personnel ID"
                        AppLanguage.TAGLISH -> "Government Email / Personnel ID"
                        AppLanguage.ILOCANO -> "Government Email / Personnel ID"
                        AppLanguage.CEBUANO -> "Government Email / Personnel ID"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.Medium
                )
            },
            placeholder = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "admin@da.gov.ph or DA-MAO-2026-09"
                        AppLanguage.TAGALOG -> "admin@da.gov.ph o DA-MAO-2026-09"
                        AppLanguage.TAGLISH -> "admin@da.gov.ph or DA-MAO-2026-09"
                        AppLanguage.ILOCANO -> "admin@da.gov.ph wenno DA-MAO-2026-09"
                        AppLanguage.CEBUANO -> "admin@da.gov.ph o DA-MAO-2026-09"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF78909C)
                )
            },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF1565C0)) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0D47A1), fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("landing_admin_input_id"),
            colors = adminInputColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = passcode,
            onValueChange = { passcode = it; errorMessage = null },
            label = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Admin Security Passcode"
                        AppLanguage.TAGALOG -> "Admin Security Passcode"
                        AppLanguage.TAGLISH -> "Admin Security Passcode"
                        AppLanguage.ILOCANO -> "Admin Security Passcode"
                        AppLanguage.CEBUANO -> "Admin Security Passcode"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.Medium
                )
            },
            placeholder = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Enter admin passcode"
                        AppLanguage.TAGALOG -> "I-enter ang admin passcode"
                        AppLanguage.TAGLISH -> "Enter admin passcode"
                        AppLanguage.ILOCANO -> "Ikabil ti admin passcode"
                        AppLanguage.CEBUANO -> "Isulod ang admin passcode"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF78909C)
                )
            },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1565C0)) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color(0xFF1565C0)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0D47A1), fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("landing_admin_input_pass"),
            colors = adminInputColors()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚠️ $errorMessage",
                color = Color(0xFFD32F2F),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val res = onAdminLogin(identifier.ifBlank { "admin@da.gov.ph" }, passcode.ifBlank { "admin123" })
                if (res.isFailure) {
                    errorMessage = res.exceptionOrNull()?.message ?: "Admin verification failed"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("landing_btn_submit_admin_login"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Verify Credentials & Enter Console"
                    AppLanguage.TAGALOG -> "I-verify ang Datos at Pumasok sa Console"
                    AppLanguage.TAGLISH -> "Verify Credentials & Enter Console"
                    AppLanguage.ILOCANO -> "Beripikaren ti Datos ken Sumrek"
                    AppLanguage.CEBUANO -> "Pamatud-i ang Datos ug Sumulod"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Demo Quick Credentials
        Text(
            text = when (currentLanguage) {
                AppLanguage.ENGLISH -> "🔑 Authorized Personnel Credentials (Demo):"
                AppLanguage.TAGALOG -> "🔑 Datos ng Awtorisadong Tauhan (Demo):"
                AppLanguage.TAGLISH -> "🔑 Authorized Personnel Credentials (Demo):"
                AppLanguage.ILOCANO -> "🔑 Datos ti Awtorisado a Personel (Demo):"
                AppLanguage.CEBUANO -> "🔑 Datos sa Awtorisadong Kawani (Demo):"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF455A64)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    identifier = "admin@da.gov.ph"
                    passcode = "admin123"
                    onAdminLogin("admin@da.gov.ph", "admin123")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("landing_admin_demo_mao"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏛️ DA MAO Officer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text("admin@da.gov.ph", fontSize = 9.sp, color = Color(0xFF616161))
                }
            }

            OutlinedButton(
                onClick = {
                    identifier = "corpuz.m@philrice.gov.ph"
                    passcode = "palay2026"
                    onAdminLogin("corpuz.m@philrice.gov.ph", "palay2026")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("landing_admin_demo_philrice"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔬 PhilRice Agronomist", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text("corpuz.m@philrice.gov.ph", fontSize = 9.sp, color = Color(0xFF616161))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onSwitchToFarmer) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Back to Farmer Login & Sign Up"
                        AppLanguage.TAGALOG -> "Bumalik sa Farmer Login & Sign Up"
                        AppLanguage.TAGLISH -> "Back to Farmer Login & Sign Up"
                        AppLanguage.ILOCANO -> "Agsubli iti Farmer Login ken Sign Up"
                        AppLanguage.CEBUANO -> "Balik sa Pag-login ug Pagparehistro sa Mag-uuma"
                    },
                    fontSize = 12.sp,
                    color = FarmBrownPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
