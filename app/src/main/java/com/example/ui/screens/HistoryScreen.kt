package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmRecord
import com.example.domain.models.AppLanguage
import com.example.domain.models.UserAccount
import com.example.domain.models.UserRole
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenLight
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmRed
import com.example.ui.theme.FarmRedLight
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    searchQuery: String,
    farms: List<FarmRecord>,
    totalFarms: Int,
    totalArea: Double,
    onSearchChange: (String) -> Unit,
    onDeleteFarm: (FarmRecord) -> Unit,
    onDeleteAllFarms: () -> Unit,
    currentUser: UserAccount? = null,
    onOpenAuthModal: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    onOpenAdminDashboard: (() -> Unit)? = null,
    onOpenDeleteAccount: (() -> Unit)? = null,
    onUpdateContactInfo: ((fullName: String, phone: String, province: String, municipality: String, crop: String, farmArea: Double, rsbsa: String, agency: String) -> Unit)? = null,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var farmToDelete by remember { mutableStateOf<FarmRecord?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showEditContactDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBF9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ==========================================
        // 1. USER PROFILE SECTION
        // ==========================================
        val profileSectionTitle = when (currentLanguage) {
            AppLanguage.ENGLISH -> "User Profile & Credentials"
            AppLanguage.TAGALOG -> "Profile ng Gumagamit at Kredensyal"
            AppLanguage.TAGLISH -> "User Profile & Account Info"
            AppLanguage.ILOCANO -> "Profile ti Agus-usar ken Kredensial"
            AppLanguage.CEBUANO -> "Profile sa Gumagamit ug Kredensyal"
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = profileSectionTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
            }

            if (onUpdateContactInfo != null) {
                TextButton(
                    onClick = { showEditContactDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = FarmGreenPrimary),
                    modifier = Modifier.testTag("btn_header_edit_contact_info")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Contact",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Edit Info"
                            AppLanguage.TAGALOG -> "I-edit"
                            AppLanguage.TAGLISH -> "Edit Info"
                            AppLanguage.ILOCANO -> "Editen"
                            AppLanguage.CEBUANO -> "Usba"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenPrimary
                    )
                }
            }
        }

        // Active Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = if (currentUser?.isAdmin == true) Color(0xFF90CAF9) else FarmBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .testTag("card_user_profile_history"),
            colors = CardDefaults.cardColors(
                containerColor = if (currentUser?.isAdmin == true) Color(0xFFF3F8FE) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top Row: Avatar + Name + Role Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isAdmin = currentUser?.isAdmin == true
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isAdmin) Color(0xFF1565C0) else FarmGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Default.Shield else Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.fullName ?: "Juan Dela Cruz",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Account",
                                tint = if (isAdmin) Color(0xFF1565C0) else FarmGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // Role & Affiliation Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isAdmin) Color(0xFFE3F2FD) else FarmGreenLight)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = currentUser?.displayRoleLabel ?: "Registered Farmer",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAdmin) Color(0xFF0D47A1) else FarmGreenHeader
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = if (isAdmin) (currentUser?.badgeOrPersonnelId ?: "CLSU Admin")
                                else "RSBSA: ${currentUser?.rsbsaNumber?.ifBlank { "RSBSA-03-49-12345" } ?: "RSBSA-03-49-12345"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FarmTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==========================================
                // STATUS LABEL & ACCREDITATION BADGE (ADMIN APPROVAL)
                // ==========================================
                val isAdmin = currentUser?.isAdmin == true
                val isApproved = currentUser?.isApprovedByAdmin == true
                val approvalStatusText = currentUser?.approvalStatus?.ifBlank { "Approved (CLSU Soil-Certified)" } ?: "Approved (CLSU Soil-Certified)"
                val approverInfo = currentUser?.approvedBy?.ifBlank { "Arjay Aquino (Head, Dept. of Soil Science, CLSU)" } ?: "Arjay Aquino (Head, Dept. of Soil Science, CLSU)"
                val approvalDate = currentUser?.approvalDate?.ifBlank { "Aug 2026" } ?: "Aug 2026"

                val statusBgColor = when {
                    isAdmin -> Color(0xFFE8F1FC)
                    isApproved -> Color(0xFFE8F5E9)
                    approvalStatusText.contains("Review", ignoreCase = true) -> Color(0xFFFFF8E1)
                    else -> Color(0xFFFFEBEE)
                }

                val statusBorderColor = when {
                    isAdmin -> Color(0xFF90CAF9)
                    isApproved -> Color(0xFFA5D6A7)
                    approvalStatusText.contains("Review", ignoreCase = true) -> Color(0xFFFFE082)
                    else -> Color(0xFFFFCDD2)
                }

                val statusIconColor = when {
                    isAdmin -> Color(0xFF1565C0)
                    isApproved -> Color(0xFF2E7D32)
                    approvalStatusText.contains("Review", ignoreCase = true) -> Color(0xFFF57F17)
                    else -> Color(0xFFC62828)
                }

                val statusIcon = when {
                    isAdmin -> Icons.Default.Shield
                    isApproved -> Icons.Default.CheckCircle
                    approvalStatusText.contains("Review", ignoreCase = true) -> Icons.Default.HourglassTop
                    else -> Icons.Default.Warning
                }

                val statusLabelTitle = when {
                    isAdmin -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "STATUS: AUTHORIZED CLSU ADMINISTRATOR"
                        AppLanguage.TAGALOG -> "STATUS: AWTORISADONG ADMIN NG CLSU"
                        AppLanguage.TAGLISH -> "STATUS: AUTHORIZED CLSU ADMIN"
                        AppLanguage.ILOCANO -> "STATUS: AWTORISADO NGA ADMIN TI CLSU"
                        AppLanguage.CEBUANO -> "STATUS: AWTORISADONG ADMIN SA CLSU"
                    }
                    isApproved -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "STATUS: APPROVED BY ADMIN (CLSU CERTIFIED)"
                        AppLanguage.TAGALOG -> "STATUS: APROBADO NG ADMIN (KUMPIRMADO NG CLSU)"
                        AppLanguage.TAGLISH -> "STATUS: APPROVED BY CLSU ADMIN"
                        AppLanguage.ILOCANO -> "STATUS: NAAKLON TI ADMIN (SERTIPIKADO TI CLSU)"
                        AppLanguage.CEBUANO -> "STATUS: GIAPROBAHAN SA ADMIN (SERTIPIKADO SA CLSU)"
                    }
                    else -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "STATUS: UNDER ADMIN REVIEW"
                        AppLanguage.TAGALOG -> "STATUS: SINUSURI PA NG ADMIN"
                        AppLanguage.TAGLISH -> "STATUS: UNDER ADMIN REVIEW"
                        AppLanguage.ILOCANO -> "STATUS: MABALBALIW PA TI ADMIN"
                        AppLanguage.CEBUANO -> "STATUS: GISUSI PA SA ADMIN"
                    }
                }

                val statusDescription = when {
                    isAdmin -> "Department of Soil Science, College of Agriculture, Central Luzon State University"
                    isApproved -> "Accredited & Verified by $approverInfo • $approvalDate"
                    else -> "Pending Land Survey & Soil Fertility Validation with CLSU Soil Science Lab"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBgColor)
                        .border(1.dp, statusBorderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("badge_admin_approval_status")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(statusIconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = "Status Icon",
                                tint = statusIconColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = statusLabelTitle,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusIconColor,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = statusDescription,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = FarmTextDark.copy(alpha = 0.85f),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = FarmBorder.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))

                // Detail Attributes Grid (2 Columns)
                val userProvince = currentUser?.province?.ifBlank { "Nueva Ecija" } ?: "Nueva Ecija"
                val userMunicipality = currentUser?.municipality?.ifBlank { "Muñoz" } ?: "Muñoz"
                val userCrop = currentUser?.primaryCrop?.ifBlank { "Inbred Rice (NSIC Rc 222)" } ?: "Inbred Rice (NSIC Rc 222)"
                val userArea = currentUser?.farmAreaHectares ?: 1.50
                val userPhone = currentUser?.phoneNumber?.ifBlank { "0917-123-4567" } ?: "0917-123-4567"
                val userAgency = currentUser?.agency?.ifBlank { "DA-PhilRice" } ?: "DA-PhilRice"
                val userDateReg = currentUser?.dateRegistered?.ifBlank { "Aug 2026" } ?: "Aug 2026"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Location Info
                    ProfileDetailTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Place,
                        label = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "LOCATION"
                            AppLanguage.TAGALOG -> "LOKASYON"
                            AppLanguage.TAGLISH -> "LOCATION"
                            AppLanguage.ILOCANO -> "LUGAR"
                            AppLanguage.CEBUANO -> "LUGAR"
                        },
                        value = "$userMunicipality, $userProvince"
                    )

                    // Farm Area / Size Info
                    ProfileDetailTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Agriculture,
                        label = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "FARM SIZE"
                            AppLanguage.TAGALOG -> "SUKAT NG BUKID"
                            AppLanguage.TAGLISH -> "FARM AREA"
                            AppLanguage.ILOCANO -> "SUKAT TI TALON"
                            AppLanguage.CEBUANO -> "SUKAT SA YUTA"
                        },
                        value = String.format("%.2f Hectares", userArea)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary Crop Info
                    ProfileDetailTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Spa,
                        label = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "PRIMARY CROP"
                            AppLanguage.TAGALOG -> "PANGUNAHING PANANIM"
                            AppLanguage.TAGLISH -> "PRIMARY CROP"
                            AppLanguage.ILOCANO -> "PANGUNA NGA MULA"
                            AppLanguage.CEBUANO -> "PANGUNANG PANANOM"
                        },
                        value = userCrop
                    )

                    // Contact Phone
                    ProfileDetailTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Phone,
                        label = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "CONTACT"
                            AppLanguage.TAGALOG -> "TELEPONO"
                            AppLanguage.TAGLISH -> "CONTACT NO."
                            AppLanguage.ILOCANO -> "TELEPONO"
                            AppLanguage.CEBUANO -> "KONTAK"
                        },
                        value = userPhone
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Agency / Organization
                    ProfileDetailTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Business,
                        label = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "AFFILIATION"
                            AppLanguage.TAGALOG -> "AHOENSYA / KOOPERATIBA"
                            AppLanguage.TAGLISH -> "AGENCY / DEPT"
                            AppLanguage.ILOCANO -> "AHEENSIYA"
                            AppLanguage.CEBUANO -> "AHENSYA"
                        },
                        value = userAgency
                    )

                    // Registration Date
                    ProfileDetailTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CalendarMonth,
                        label = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "MEMBER SINCE"
                            AppLanguage.TAGALOG -> "REHISTRADO MULA"
                            AppLanguage.TAGLISH -> "MEMBER SINCE"
                            AppLanguage.ILOCANO -> "REHISTRADO"
                            AppLanguage.CEBUANO -> "REHISTRADO"
                        },
                        value = userDateReg
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Actions Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onUpdateContactInfo != null) {
                        OutlinedButton(
                            onClick = { showEditContactDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = FarmGreenPrimary
                            ),
                            border = BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_profile_edit_contact_info")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContactPhone,
                                    contentDescription = null,
                                    tint = FarmGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val editContactLabel = when (currentLanguage) {
                                    AppLanguage.ENGLISH -> "Edit Contact"
                                    AppLanguage.TAGALOG -> "Baguhin ang Info"
                                    AppLanguage.TAGLISH -> "Edit Contact Info"
                                    AppLanguage.ILOCANO -> "Sukatan ti Info"
                                    AppLanguage.CEBUANO -> "Usba ang Info"
                                }
                                Text(
                                    text = editContactLabel,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (currentUser?.isAdmin == true && onOpenAdminDashboard != null) {
                        Button(
                            onClick = onOpenAdminDashboard,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1565C0),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_profile_open_admin_dashboard")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CLSU Admin",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (onLogout != null) {
                        OutlinedButton(
                            onClick = onLogout,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = FarmRed
                            ),
                            border = BorderStroke(1.dp, FarmRed.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_profile_logout")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = FarmRed,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val logoutLabel = when (currentLanguage) {
                                    AppLanguage.ENGLISH -> "Log Out"
                                    AppLanguage.TAGALOG -> "Mag-logout"
                                    AppLanguage.TAGLISH -> "Log Out"
                                    AppLanguage.ILOCANO -> "Ag-logout"
                                    AppLanguage.CEBUANO -> "Mo-logout"
                                }
                                Text(
                                    text = logoutLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmRed,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 2. SAVED FARM MEASUREMENTS & RECORDS SECTION
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val titleText = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Farm Measurement History"
                AppLanguage.TAGALOG -> "Nakaraang Tala ng Bukid"
                AppLanguage.TAGLISH -> "Farm History & GPS Records"
                AppLanguage.ILOCANO -> "Nakalabas a Rekord ti Talon"
                AppLanguage.CEBUANO -> "Talaan sa Yuta"
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = FarmGreenHeader,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = titleText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (farms.isNotEmpty()) {
                val clearAllText = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Clear All"
                    AppLanguage.TAGALOG -> "Burahin Lahat"
                    AppLanguage.TAGLISH -> "Clear All"
                    AppLanguage.ILOCANO -> "Punasan Amin"
                    AppLanguage.CEBUANO -> "I-delete Tanan"
                }
                TextButton(
                    onClick = { showDeleteAllDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = FarmRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Clear All",
                        tint = FarmRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = clearAllText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FarmRed
                    )
                }
            }
        }

        // Search Bar
        val searchPlaceholder = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Search saved farms by name or crop..."
            AppLanguage.TAGALOG -> "Maghanap ng bukid ayon sa pangalan o binhi..."
            AppLanguage.TAGLISH -> "Search saved farms or crops..."
            AppLanguage.ILOCANO -> "Biroken ti naidulin a talon..."
            AppLanguage.CEBUANO -> "Pangitaa ang na-save nga yuta..."
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(searchPlaceholder, color = Color.Gray, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_history"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = FarmBorder,
                focusedBorderColor = FarmGreenPrimary,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Total Farms & Total Area Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Farms Card (Dark Green)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp),
                colors = CardDefaults.cardColors(containerColor = FarmGreenHeader),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    val totalFarmsLabel = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Total Farms"
                        AppLanguage.TAGALOG -> "Kabuuan ng Bukid"
                        AppLanguage.TAGLISH -> "Total ng Bukid"
                        AppLanguage.ILOCANO -> "Pagsasao ti Talon"
                        AppLanguage.CEBUANO -> "Tanan nga Yuta"
                    }
                    Text(
                        text = totalFarmsLabel,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalFarms",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Total Area Card (Light Green)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = FarmGreenLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    val totalAreaLabel = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Total Area"
                        AppLanguage.TAGALOG -> "Kabuuan ng Sukat"
                        AppLanguage.TAGLISH -> "Total Area"
                        AppLanguage.ILOCANO -> "Kabuuan nga Sukat"
                        AppLanguage.CEBUANO -> "Tanan nga Sukat"
                    }
                    Text(
                        text = totalAreaLabel,
                        fontSize = 13.sp,
                        color = FarmTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", totalArea),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ha",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Farm Records List
        if (farms.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val emptyText = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "No saved farms found"
                            AppLanguage.TAGALOG -> "Walang nahanap na na-save na bukid"
                            AppLanguage.TAGLISH -> "No saved farms found"
                            AppLanguage.ILOCANO -> "Awan ti naidulin a talon"
                            AppLanguage.CEBUANO -> "Walay na-save nga yuta"
                        }
                        Text(
                            text = emptyText,
                            color = FarmTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            farms.forEach { farm ->
                FarmRecordItemCard(
                    farm = farm,
                    onDelete = { farmToDelete = farm }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // ==========================================
        // 3. ACCOUNT & DATA MANAGEMENT SECTION
        // ==========================================
        if (onOpenDeleteAccount != null) {
            Spacer(modifier = Modifier.height(20.dp))
            
            val accountSettingsHeader = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Account & Data Privacy"
                AppLanguage.TAGALOG -> "Account at Pagkapribado ng Datos"
                AppLanguage.TAGLISH -> "Account Settings & Data Privacy"
                AppLanguage.ILOCANO -> "Account ken Panagtalinaed ti Datos"
                AppLanguage.CEBUANO -> "Account ug Pagkapribado sa Datos"
            }
            Text(
                text = accountSettingsHeader,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val deleteAccountLabel = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Delete Account & Reset Data"
                AppLanguage.TAGALOG -> "Burahin ang Account at I-reset ang Datos"
                AppLanguage.TAGLISH -> "Delete Account & Reset All Data"
                AppLanguage.ILOCANO -> "Pukawen ti Account ken Datos"
                AppLanguage.CEBUANO -> "I-delete ang Account ug I-reset ang Datos"
            }
            OutlinedButton(
                onClick = onOpenDeleteAccount,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmRed),
                border = BorderStroke(1.dp, FarmRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_history_delete_account")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PersonRemove,
                        contentDescription = deleteAccountLabel,
                        tint = FarmRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = deleteAccountLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // ==========================================
    // EDIT CONTACT INFORMATION DIALOG
    // ==========================================
    if (showEditContactDialog && currentUser != null) {
        var editName by remember { mutableStateOf(currentUser.fullName) }
        var editPhone by remember { mutableStateOf(currentUser.phoneNumber) }
        var editMunicipality by remember { mutableStateOf(currentUser.municipality) }
        var editProvince by remember { mutableStateOf(currentUser.province) }
        var editCrop by remember { mutableStateOf(currentUser.primaryCrop) }
        var editAreaText by remember { mutableStateOf(if (currentUser.farmAreaHectares > 0.0 && currentUser.farmAreaHectares != 2.4) String.format(Locale.US, "%.2f", currentUser.farmAreaHectares) else "") }
        var editRsbsa by remember { mutableStateOf(currentUser.rsbsaNumber) }
        var editAgency by remember { mutableStateOf(currentUser.agency) }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showEditContactDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FarmGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContactPhone,
                            contentDescription = null,
                            tint = FarmGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Edit Contact Information"
                                AppLanguage.TAGALOG -> "I-edit ang Impormasyon ng Kontak"
                                AppLanguage.TAGLISH -> "Edit Contact Details"
                                AppLanguage.ILOCANO -> "Editen ti Kontak nga Impormasion"
                                AppLanguage.CEBUANO -> "Usba ang Impormasyon sa Kontak"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmTextDark
                        )
                        Text(
                            text = "Update profile, phone, location & farm details",
                            fontSize = 11.sp,
                            color = FarmTextSecondary
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (inputError != null) {
                        Text(
                            text = inputError!!,
                            color = FarmRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Full Name
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it; inputError = null },
                        label = { Text("Full Name / Pangalan", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_fullname"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Phone Number
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it; inputError = null },
                        label = { Text("Phone Number / Telepono", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        placeholder = { Text("e.g. 0917-123-4567", color = Color.Gray, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_phone"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Municipality / City
                    OutlinedTextField(
                        value = editMunicipality,
                        onValueChange = { editMunicipality = it; inputError = null },
                        label = { Text("Municipality / Bayan / Lungsod", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        placeholder = { Text("Science City of Muñoz", color = Color.Gray, fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_municipality"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Province
                    OutlinedTextField(
                        value = editProvince,
                        onValueChange = { editProvince = it; inputError = null },
                        label = { Text("Province / Lalawigan", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        placeholder = { Text("Nueva Ecija", color = Color.Gray, fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_province"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Primary Crop / Variety
                    OutlinedTextField(
                        value = editCrop,
                        onValueChange = { editCrop = it; inputError = null },
                        label = { Text("Primary Crop / Pananim o Binhi", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Spa, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        placeholder = { Text("Lowland Irrigated Rice (NSIC Rc 222)", color = Color.Gray, fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_crop"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Farm Area in Hectares
                    OutlinedTextField(
                        value = editAreaText,
                        onValueChange = { editAreaText = it; inputError = null },
                        label = { Text("Farm Area (Hectares) / Sukat ng Bukid", fontSize = 12.sp) },
                        placeholder = { Text("Enter Farm Area", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_area"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // RSBSA / ID
                    OutlinedTextField(
                        value = editRsbsa,
                        onValueChange = { editRsbsa = it; inputError = null },
                        label = { Text("RSBSA ID / Personnel ID", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        placeholder = { Text("03-49-12-00421", color = Color.Gray, fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_rsbsa"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Affiliation / Agency
                    OutlinedTextField(
                        value = editAgency,
                        onValueChange = { editAgency = it; inputError = null },
                        label = { Text("Affiliation / Kooperatiba / Ahensya", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Business, contentDescription = null, tint = FarmGreenPrimary)
                        },
                        placeholder = { Text("DA-PhilRice / CLSU", color = Color.Gray, fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_agency"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isBlank()) {
                            inputError = "Please enter your name"
                            return@Button
                        }
                        if (editPhone.isBlank()) {
                            inputError = "Please enter your phone number"
                            return@Button
                        }
                        val areaDouble = editAreaText.toDoubleOrNull() ?: 1.5
                        onUpdateContactInfo?.invoke(
                            editName.trim(),
                            editPhone.trim(),
                            editProvince.trim(),
                            editMunicipality.trim(),
                            editCrop.trim(),
                            areaDouble,
                            editRsbsa.trim(),
                            editAgency.trim()
                        )
                        showEditContactDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FarmGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("btn_save_contact_info"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Save Changes"
                            AppLanguage.TAGALOG -> "I-save ang Pagbabago"
                            AppLanguage.TAGLISH -> "Save Changes"
                            AppLanguage.ILOCANO -> "Idulin ti Nabaliwan"
                            AppLanguage.CEBUANO -> "I-save ang Nausab"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditContactDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 13.sp,
                        color = FarmTextDark,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Delete Single Farm Confirmation Modal
    farmToDelete?.let { farm ->
        AlertDialog(
            onDismissRequest = { farmToDelete = null },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(FarmRedLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Farm Warning",
                            tint = FarmRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Delete Farm History?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${farm.name}\" (${String.format("%.2f", farm.areaHectares)} ha) from your history? This action cannot be undone.",
                    fontSize = 14.sp,
                    color = FarmTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onDeleteFarm(farm)
                            farmToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FarmRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("button_confirm_delete_farm_history"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Yes, Delete Record",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    OutlinedButton(
                        onClick = { farmToDelete = null },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FarmBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 46.dp)
                            .testTag("button_cancel_delete_farm_history"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FarmTextDark
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            color = FarmTextDark,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            },
            dismissButton = null,
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete All Farms Confirmation Modal
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(FarmRedLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Clear History Warning",
                            tint = FarmRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Clear All Farm History?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ALL $totalFarms farm history records? This will permanently erase your saved records.",
                    fontSize = 14.sp,
                    color = FarmTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onDeleteAllFarms()
                            showDeleteAllDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FarmRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("button_confirm_clear_all_history"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Yes, Clear All History",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    OutlinedButton(
                        onClick = { showDeleteAllDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FarmBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 46.dp)
                            .testTag("button_cancel_clear_all_history"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FarmTextDark
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            color = FarmTextDark,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            },
            dismissButton = null,
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun ProfileDetailTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF1F8F1))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FarmGreenPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextSecondary
                )
                Text(
                    text = value,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FarmTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FarmRecordItemCard(
    farm: FarmRecord,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FarmGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = farm.name,
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = farm.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = farm.dateFormatted,
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { expanded = !expanded }
                ) {
                    Text(
                        text = String.format("%.2f", farm.areaHectares),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                    Text(
                        text = "hectares",
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = Color.Gray
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Divider(color = FarmBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 1: Perimeter & Walked
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Perimeter
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Perimeter ",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Text(
                                text = "${farm.perimeterMeters.toInt()} m",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }

                        // Walked
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CompassCalibration,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Walked ",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            val walkedVal = if (farm.walkedMeters > 0) farm.walkedMeters else farm.perimeterMeters
                            Text(
                                text = "${walkedVal.toInt()} m",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 2: GPS Accuracy & Boundary Points
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GPS Accuracy",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = farm.gpsAccuracy.ifBlank { "Fair" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Boundary Points",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val ptCount = if (farm.boundaryPointsCount > 0) {
                                farm.boundaryPointsCount
                            } else {
                                val count = farm.pointsJson.windowed(3).count { it == "lat" }
                                if (count > 0) count else 0
                            }
                            Text(
                                text = "$ptCount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 3: Date Measured & Delete Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Date Measured",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val fullDateStr = remember(farm.timestamp) {
                                try {
                                    SimpleDateFormat("M/d/yyyy, h:mm:ss a", Locale.US).format(Date(farm.timestamp))
                                } catch (e: Exception) {
                                    farm.dateFormatted
                                }
                            }
                            Text(
                                text = fullDateStr,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = FarmRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
