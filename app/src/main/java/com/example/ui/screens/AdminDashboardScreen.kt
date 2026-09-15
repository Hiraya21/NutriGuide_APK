package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AdminAuditLog
import com.example.domain.models.AppLanguage
import com.example.domain.models.FarmerRegistryItem
import com.example.domain.models.UserAccount

@Composable
fun AdminDashboardScreen(
    currentUser: UserAccount,
    farmerRegistry: List<FarmerRegistryItem>,
    auditLogs: List<AdminAuditLog>,
    currentLanguage: AppLanguage,
    onBackToFarmerView: () -> Unit,
    onLogout: () -> Unit,
    onUpdateFarmerStatus: (farmerId: String, newStatus: String) -> Unit = { _, _ -> },
    onApproveFarmer: (farmerId: String) -> Unit = { _ -> },
    onBroadcastAlert: (title: String, message: String, priority: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf("All") }

    // Status Dialog Management
    var selectedFarmerForStatusChange by remember { mutableStateOf<FarmerRegistryItem?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Broadcast form states
    var alertTitle by remember { mutableStateOf("CLSU Soil Science Advisory: Monsoon Wet Season Nutrient Management") }
    var alertMessage by remember { mutableStateOf("Advisory from The Department of Soil Science, College of Agriculture, Central Luzon State University (Head: Arjay Aquino): Heavy rainfall expected across Muñoz & Nueva Ecija. Farmers are advised to postpone nitrogen broadcast to prevent leaching in clay loam paddies.") }
    var alertPriority by remember { mutableStateOf("High Advisory") }
    var broadcastSuccessMessage by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onBackToFarmerView()
    }

    // Status Change Dialog
    if (selectedFarmerForStatusChange != null) {
        val farmer = selectedFarmerForStatusChange!!
        var tempStatus by remember(farmer.id) { mutableStateOf(farmer.subsidyStatus) }

        AlertDialog(
            onDismissRequest = { selectedFarmerForStatusChange = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Modify Farmer Roster Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Magsasaka: ${farmer.fullName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "RSBSA: ${farmer.rsbsaId} • ${farmer.barangay}, ${farmer.municipality}",
                        fontSize = 11.sp,
                        color = Color(0xFF616161)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Piliin ang Bagong Status (Dept. of Soil Science Accreditation):",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val statusOptions = listOf(
                        "Approved (CLSU Soil-Certified)" to "Approved & Soil Chemistry Validated",
                        "Approved (DA-RCEF)" to "RSBSA & RCEF Subsidies Active",
                        "Voucher Distributed" to "Fertilizer & Seed Vouchers Released",
                        "Under Review" to "Pending Verification & Land Survey",
                        "Needs Soil Re-Sampling" to "Soil Sample Inconclusive / Retest",
                        "Suspended / Inactive" to "Temporarily Inactive / Non-Compliant"
                    )

                    statusOptions.forEach { (statusKey, description) ->
                        val isSelected = tempStatus == statusKey
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { tempStatus = statusKey },
                            color = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF8FAFC)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { tempStatus = statusKey },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1565C0)),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = statusKey,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF0D47A1) else Color(0xFF263238)
                                    )
                                    Text(
                                        text = description,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onUpdateFarmerStatus(farmer.id, tempStatus)
                            feedbackMessage = "Status ni ${farmer.fullName} pinalitan ng \"$tempStatus\"!"
                            selectedFarmerForStatusChange = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 44.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Save Status Change",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { selectedFarmerForStatusChange = null },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 42.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Cancel",
                            fontSize = 13.sp,
                            color = Color(0xFF546E7A),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            },
            dismissButton = null,
            shape = RoundedCornerShape(14.dp),
            containerColor = Color.White
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .testTag("screen_admin_dashboard")
    ) {
        // Top App Bar Header with CLSU Department of Soil Science Branding
        Surface(
            color = Color(0xFF0D47A1),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp)
            ) {
                // Top Navigation and Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackToFarmerView,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_admin_back_to_farmer")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Farmer View",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "CLSU Department of Soil Science",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "College of Agriculture • Central Luzon State University",
                            fontSize = 10.5.sp,
                            color = Color(0xFFBBDEFB),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Action Buttons (Farmer View Switch & Logout)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackToFarmerView,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF81C784), Color(0xFFA5D6A7)))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_switch_to_farmer_app")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = Color(0xFFA5D6A7)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Farmer View",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_admin_logout")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Log Out",
                                modifier = Modifier.size(13.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Logout", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Personnel Identity Banner Card Spotlight (Arjay Aquino - Head of Department)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D47A1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentUser.fullName.contains("Arjay", ignoreCase = true) || currentUser.isAdmin) "Arjay Aquino" else currentUser.fullName,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(Head of Department)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                            Text(
                                text = "The Department of Soil Science, CAg - CLSU",
                                fontSize = 10.sp,
                                color = Color(0xFFE3F2FD),
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2E7D32))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "CLSU ADMIN",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Feedback message toast
        if (feedbackMessage != null) {
            Surface(
                color = Color(0xFF2E7D32),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feedbackMessage!!,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = { feedbackMessage = null },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Modern Scrollable Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            edgePadding = 12.dp,
            divider = { HorizontalDivider(color = Color(0xFFE0E0E0)) },
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF0D47A1),
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "🏛️ CLSU Overview & KPIs",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 0) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_overview")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "👥 Farmer Roster (${farmerRegistry.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 1) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_farmers")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        "🧪 Soil & Fertilizer Demand",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 2) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_fertilizer")
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = {
                    Text(
                        "📢 Advisory Broadcast",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 3) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_broadcast")
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = {
                    Text(
                        "🛡️ Audit Logs",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 4) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_audit")
            )
        }

        // Tab Content List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    item { AdminOverviewTab(farmerRegistry = farmerRegistry) }
                }
                1 -> {
                    item {
                        FarmerRosterHeader(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            selectedFilter = selectedFilterStatus,
                            onFilterChange = { selectedFilterStatus = it },
                            farmerRegistry = farmerRegistry
                        )
                    }
                    val filteredFarmers = farmerRegistry.filter {
                        (searchQuery.isBlank() || it.fullName.contains(searchQuery, ignoreCase = true) || it.rsbsaId.contains(searchQuery, ignoreCase = true) || it.barangay.contains(searchQuery, ignoreCase = true)) &&
                        (selectedFilterStatus == "All" || it.subsidyStatus.contains(selectedFilterStatus, ignoreCase = true))
                    }
                    if (filteredFarmers.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF9E9E9E),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Walang nahanap na magsasaka sa query \"$searchQuery\"",
                                        fontSize = 13.sp,
                                        color = Color(0xFF616161),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredFarmers) { farmer ->
                            FarmerRegistryCard(
                                farmer = farmer,
                                onQuickApprove = {
                                    onApproveFarmer(farmer.id)
                                    feedbackMessage = "✅ Na-aprubahan si ${farmer.fullName} (CLSU Soil-Certified)!"
                                },
                                onChangeStatusClick = {
                                    selectedFarmerForStatusChange = farmer
                                }
                            )
                        }
                    }
                }
                2 -> {
                    item { RegionalFertilizerDemandTab(farmerRegistry = farmerRegistry) }
                }
                3 -> {
                    item {
                        AdvisoryDispatcherTab(
                            title = alertTitle,
                            message = alertMessage,
                            priority = alertPriority,
                            successMsg = broadcastSuccessMessage,
                            onTitleChange = { alertTitle = it },
                            onMessageChange = { alertMessage = it },
                            onPriorityChange = { alertPriority = it },
                            onSend = {
                                onBroadcastAlert(alertTitle, alertMessage, alertPriority)
                                broadcastSuccessMessage = "Advisory broadcast successfully dispatched from The Department of Soil Science (CLSU) to all registered farmers!"
                            }
                        )
                    }
                }
                4 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF37474F),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CLSU Soil Science Administrative & Security Audit",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF263238)
                                    )
                                }
                                Text(
                                    text = "Every farmer accreditation, status change, and advisory broadcast by Head Arjay Aquino & personnel is cryptographically logged.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                    items(auditLogs) { log ->
                        AuditLogCard(log = log)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AdminOverviewTab(farmerRegistry: List<FarmerRegistryItem>) {
    val totalHectares = farmerRegistry.sumOf { it.farmSizeHa }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Department Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0D47A1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "The Department of Soil Science",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1)
                        )
                        Text(
                            text = "College of Agriculture, Central Luzon State University",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "Head of Department: Arjay Aquino",
                            fontSize = 11.sp,
                            color = Color(0xFF455A64)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFECEFF1))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Leading agricultural research, regional soil fertility mapping, customized nutrient formulations, and precision soil-certified farmer accreditation across Central Luzon.",
                    fontSize = 11.sp,
                    color = Color(0xFF616161),
                    lineHeight = 15.sp
                )
            }
        }

        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Central Luzon Regional Agronomic Metrics",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "CLSU Soil Science Hub",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
            }
        }

        // 4 KPI Metric Cards in a balanced 2x2 grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricKpiCard(
                title = "Accredited Farmers",
                value = "${farmerRegistry.size}",
                subtitle = "Active in CLSU/RSBSA Roster",
                icon = Icons.Default.People,
                cardColor = Color(0xFFE8F5E9),
                accentColor = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )

            MetricKpiCard(
                title = "Total Verified Land",
                value = String.format("%.1f ha", totalHectares),
                subtitle = "Analyzed Rice Farmland",
                icon = Icons.Default.Landscape,
                cardColor = Color(0xFFE3F2FD),
                accentColor = Color(0xFF1565C0),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val approvedCount = farmerRegistry.count { it.subsidyStatus.contains("Approved", ignoreCase = true) }
            val approvedPercent = if (farmerRegistry.isNotEmpty()) (approvedCount * 100) / farmerRegistry.size else 0

            MetricKpiCard(
                title = "CLSU Accreditation",
                value = "$approvedPercent%",
                subtitle = "$approvedCount of ${farmerRegistry.size} Approved",
                icon = Icons.Default.Verified,
                cardColor = Color(0xFFFFF3E0),
                accentColor = Color(0xFFE65100),
                modifier = Modifier.weight(1f)
            )

            MetricKpiCard(
                title = "Soil Analysis Rate",
                value = "94%",
                subtitle = "Soil-Tested Profiles",
                icon = Icons.Default.Assessment,
                cardColor = Color(0xFFF3E5F5),
                accentColor = Color(0xFF7B1FA2),
                modifier = Modifier.weight(1f)
            )
        }

        // Varieties Distribution
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Most Adopted Rice Varieties (Central Luzon / Muñoz)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(10.dp))

                VarietyShareRow("NSIC Rc 222 (Tubigan 21)", "High yielding in irrigated lowland", "42%", 0.42f, Color(0xFF2E7D32))
                VarietyShareRow("NSIC Rc 160 (Tubigan 14)", "Premium grain quality & aromatic", "28%", 0.28f, Color(0xFF1565C0))
                VarietyShareRow("NSIC Rc 480 (GSR 8)", "Green super rice / drought tolerant", "18%", 0.18f, Color(0xFFE65100))
                VarietyShareRow("Others (Rc 216, PSB Rc 10)", "Local traditional & specialty varieties", "12%", 0.12f, Color(0xFF7B1FA2))
            }
        }
    }
}

@Composable
private fun MetricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    cardColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Text(
                text = subtitle,
                fontSize = 9.5.sp,
                color = Color(0xFF616161),
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun VarietyShareRow(
    name: String,
    desc: String,
    share: String,
    progress: Float,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text(desc, fontSize = 9.sp, color = Color(0xFF757575))
            }
            Text(share, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFEEEEEE),
        )
    }
}

@Composable
private fun FarmerRosterHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    farmerRegistry: List<FarmerRegistryItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Department of Soil Science — Farmer Roster",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                    Text(
                        text = "Manage accreditation, approve soil vouchers, and update status",
                        fontSize = 10.5.sp,
                        color = Color(0xFF616161)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${farmerRegistry.size} Farmers",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_search_farmer_input"),
                placeholder = { Text("Search by name, RSBSA ID, or Barangay...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Approved", "Under Review", "Needs Soil").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter, fontSize = 10.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1565C0),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FarmerRegistryCard(
    farmer: FarmerRegistryItem,
    onQuickApprove: () -> Unit,
    onChangeStatusClick: () -> Unit
) {
    val isApproved = farmer.subsidyStatus.contains("Approved", ignoreCase = true)
    val statusBgColor = when {
        isApproved -> Color(0xFFE8F5E9)
        farmer.subsidyStatus.contains("Under Review", ignoreCase = true) -> Color(0xFFFFF3E0)
        farmer.subsidyStatus.contains("Needs", ignoreCase = true) -> Color(0xFFFBE9E7)
        else -> Color(0xFFE3F2FD)
    }
    val statusTextColor = when {
        isApproved -> Color(0xFF2E7D32)
        farmer.subsidyStatus.contains("Under Review", ignoreCase = true) -> Color(0xFFE65100)
        farmer.subsidyStatus.contains("Needs", ignoreCase = true) -> Color(0xFFD84315)
        else -> Color(0xFF1565C0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_farmer_card_${farmer.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = farmer.fullName.take(1),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = farmer.fullName,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = "RSBSA ID: ${farmer.rsbsaId}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = farmer.subsidyStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(8.dp))

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("LOKASYON", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
                    Text("${farmer.barangay}, ${farmer.municipality}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF424242))
                }
                Column {
                    Text("SUKAT NG LUPA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
                    Text("${farmer.farmSizeHa} ha", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                Column {
                    Text("PANGUNAHING BINHI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
                    Text(farmer.cropVariety, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF424242))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("HULING SOIL TEST / AKTIBO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
                    Text("${farmer.lastActiveDate} • ${farmer.soilType}", fontSize = 10.5.sp, color = Color(0xFF616161))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("URI NG LUPA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
                    Text(farmer.soilType, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Approve & Change Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isApproved) {
                    Button(
                        onClick = onQuickApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 40.dp)
                            .testTag("btn_approve_farmer_${farmer.id}"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Approve Farmer",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = onChangeStatusClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 40.dp)
                        .testTag("btn_change_status_${farmer.id}"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0D47A1))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Change Status",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegionalFertilizerDemandTab(farmerRegistry: List<FarmerRegistryItem>) {
    val totalHa = farmerRegistry.sumOf { it.farmSizeHa } + 472.4

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "CLSU Department of Soil Science Fertilizer Demands (Wet Season)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        // Summary Metric Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Verified Area (Muñoz Zone)", fontSize = 11.sp, color = Color(0xFFBBDEFB))
                    Text(String.format("%.1f Hectares", totalHa), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D47A1))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("4,074 Total Bags Needed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                }
            }
        }

        // Fertilizer Demands Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Estimated Bag Requirements (50kg Standard Bags)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(10.dp))

                FertilizerDemandRow("Urea (46-0-0)", "Nitrogen Booster (Tillering & Panicle)", "1,164 Bags", Color(0xFF1976D2))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                FertilizerDemandRow("Complete (14-14-14)", "Basal Application Foundation", "1,940 Bags", Color(0xFF2E7D32))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                FertilizerDemandRow("Muriate of Potash (0-0-60)", "Grain Filling Potash Booster", "582 Bags", Color(0xFFE65100))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                FertilizerDemandRow("Ammonium Sulfate (21-0-0 + 24S)", "Sulfur replenishment for Maligaya Clay", "388 Bags", Color(0xFF7B1FA2))
            }
        }
    }
}

@Composable
private fun FertilizerDemandRow(fertilizer: String, note: String, bags: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(fertilizer, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
            Text(note, fontSize = 10.sp, color = Color(0xFF757575))
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(bags, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun AdvisoryDispatcherTab(
    title: String,
    message: String,
    priority: String,
    successMsg: String?,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Broadcast Municipal Advisory", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
            }
            Text("Send real-time alerts directly to all registered farmers' home feed.", fontSize = 11.sp, color = Color(0xFF757575))

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Advisory Title / Paksa") },
                placeholder = { Text("e.g. Babala sa Brown Planthopper") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_advisory_title"),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Advisory Message / Detalye") },
                placeholder = { Text("Isulat ang kumpletong tagubilin sa mga magsasaka...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_advisory_msg"),
                shape = RoundedCornerShape(8.dp),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Priority Level:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("NORMAL", "URGENT", "WEATHER_ALERT").forEach { level ->
                    val isSelected = priority == level
                    val color = when (level) {
                        "WEATHER_ALERT" -> Color(0xFF0288D1)
                        "URGENT" -> Color(0xFFC62828)
                        else -> Color(0xFF2E7D32)
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPriorityChange(level) },
                        label = { Text(level, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.15f),
                            selectedLabelColor = color
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (successMsg != null) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✅ $successMsg",
                        color = Color(0xFF2E7D32),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onSend,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("btn_dispatch_broadcast"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Dispatch Broadcast to All Farmers",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AuditLogCard(log: AdminAuditLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text("${log.personnelName} (${log.personnelId})", fontSize = 11.sp, color = Color(0xFF1565C0))
                Text(log.timestampFormatted, fontSize = 10.sp, color = Color(0xFF757575))
            }
        }
    }
}
