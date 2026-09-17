package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmRecord
import com.example.domain.models.AdminAuditLog
import com.example.domain.models.AppLanguage
import com.example.domain.models.FarmerRegistryItem
import com.example.domain.models.UserAccount
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    currentUser: UserAccount,
    farmerRegistry: List<FarmerRegistryItem>,
    auditLogs: List<AdminAuditLog>,
    currentLanguage: AppLanguage,
    farmRecords: List<FarmRecord> = emptyList(),
    onBackToFarmerView: () -> Unit,
    onLogout: () -> Unit,
    onUpdateFarmerStatus: (farmerId: String, newStatus: String) -> Unit = { _, _ -> },
    onApproveFarmer: (farmerId: String) -> Unit = { _ -> },
    onDeleteFarmer: (farmerId: String) -> Unit = { _ -> },
    onEditFarmer: (farmer: FarmerRegistryItem) -> Unit = { _ -> },
    onAddFarmer: (farmer: FarmerRegistryItem) -> Unit = { _ -> },
    onResetFarmerRegistry: () -> Unit = {},
    onDeleteFarmRecord: (farm: FarmRecord) -> Unit = { _ -> },
    onUpdateFarmRecord: (farm: FarmRecord) -> Unit = { _ -> },
    onInsertFarmRecord: (farm: FarmRecord) -> Unit = { _ -> },
    onDeleteAllFarms: () -> Unit = {},
    onSeedSampleFarms: () -> Unit = {},
    onDeleteAuditLog: (logId: String) -> Unit = { _ -> },
    onClearAuditLogs: () -> Unit = {},
    onBroadcastAlert: (title: String, message: String, priority: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf("All") }

    // Status Dialog Management
    var selectedFarmerForStatusChange by remember { mutableStateOf<FarmerRegistryItem?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Farmer Registry CRUD Dialog states
    var farmerForEdit by remember { mutableStateOf<FarmerRegistryItem?>(null) }
    var farmerForDelete by remember { mutableStateOf<FarmerRegistryItem?>(null) }
    var isAddFarmerDialogOpen by remember { mutableStateOf(false) }
    var isResetRegistryDialogOpen by remember { mutableStateOf(false) }

    // Room DB Farm Records CRUD Dialog states
    var farmRecordForEdit by remember { mutableStateOf<FarmRecord?>(null) }
    var farmRecordForDelete by remember { mutableStateOf<FarmRecord?>(null) }
    var isAddFarmDialogOpen by remember { mutableStateOf(false) }
    var isWipeAllFarmsDialogOpen by remember { mutableStateOf(false) }
    var isClearAuditLogsDialogOpen by remember { mutableStateOf(false) }

    // Broadcast form states
    var alertTitle by remember { mutableStateOf("CLSU Soil Science Advisory: Monsoon Wet Season Nutrient Management") }
    var alertMessage by remember { mutableStateOf("Advisory from The Department of Soil Science, College of Agriculture, Central Luzon State University (Head: Arjay Aquino): Heavy rainfall expected across Muñoz & Nueva Ecija. Farmers are advised to postpone nitrogen broadcast to prevent leaching in clay loam paddies.") }
    var alertPriority by remember { mutableStateOf("High Advisory") }
    var broadcastSuccessMessage by remember { mutableStateOf<String?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }
    BackHandler(enabled = !drawerState.isOpen) {
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

    // Farmer Registry CRUD Dialogs
    farmerForEdit?.let { farmer ->
        EditFarmerDialog(
            farmer = farmer,
            onDismiss = { farmerForEdit = null },
            onSave = { updated ->
                onEditFarmer(updated)
                farmerForEdit = null
                feedbackMessage = "Farmer ${updated.fullName} updated!"
            }
        )
    }

    farmerForDelete?.let { farmer ->
        DeleteFarmerDialog(
            farmer = farmer,
            onDismiss = { farmerForDelete = null },
            onConfirmDelete = {
                onDeleteFarmer(farmer.id)
                farmerForDelete = null
                feedbackMessage = "Farmer ${farmer.fullName} removed from registry."
            }
        )
    }

    if (isAddFarmerDialogOpen) {
        AddFarmerDialog(
            onDismiss = { isAddFarmerDialogOpen = false },
            onSave = { newFarmer ->
                onAddFarmer(newFarmer)
                isAddFarmerDialogOpen = false
                feedbackMessage = "New farmer ${newFarmer.fullName} registered!"
            }
        )
    }

    if (isResetRegistryDialogOpen) {
        ResetRegistryDialog(
            onDismiss = { isResetRegistryDialogOpen = false },
            onConfirmReset = {
                onResetFarmerRegistry()
                isResetRegistryDialogOpen = false
                feedbackMessage = "Farmer registry reset to official CLSU seed data."
            }
        )
    }

    // Room SQLite Farm Records CRUD Dialogs
    farmRecordForEdit?.let { farm ->
        EditFarmRecordDialog(
            farm = farm,
            onDismiss = { farmRecordForEdit = null },
            onSave = { updated ->
                onUpdateFarmRecord(updated)
                farmRecordForEdit = null
                feedbackMessage = "Farm Record #${updated.id} updated in database."
            }
        )
    }

    farmRecordForDelete?.let { farm ->
        DeleteFarmRecordDialog(
            farm = farm,
            onDismiss = { farmRecordForDelete = null },
            onConfirmDelete = {
                onDeleteFarmRecord(farm)
                farmRecordForDelete = null
                feedbackMessage = "Farm Record #${farm.id} deleted from SQLite database."
            }
        )
    }

    if (isAddFarmDialogOpen) {
        AddFarmRecordDialog(
            onDismiss = { isAddFarmDialogOpen = false },
            onSave = { newFarm ->
                onInsertFarmRecord(newFarm)
                isAddFarmDialogOpen = false
                feedbackMessage = "New Farm Record '${newFarm.name}' saved to SQLite database."
            }
        )
    }

    if (isWipeAllFarmsDialogOpen) {
        WipeAllFarmsDialog(
            recordCount = farmRecords.size,
            onDismiss = { isWipeAllFarmsDialogOpen = false },
            onConfirmWipe = {
                onDeleteAllFarms()
                isWipeAllFarmsDialogOpen = false
                feedbackMessage = "All farm records purged from SQLite database."
            }
        )
    }

    if (isClearAuditLogsDialogOpen) {
        AlertDialog(
            onDismissRequest = { isClearAuditLogsDialogOpen = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All Audit Logs?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    "Are you sure you want to clear all ${auditLogs.size} audit trail records? A fresh initialization log will be generated.",
                    fontSize = 12.5.sp,
                    color = Color(0xFF334155)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAuditLogs()
                        isClearAuditLogsDialogOpen = false
                        feedbackMessage = "Audit logs console cleared & re-initialized."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.testTag("confirm_clear_audit_logs_btn")
                ) {
                    Text("Clear Logs", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { isClearAuditLogsDialogOpen = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(14.dp)
        )
    }

    val navSections = remember(farmerRegistry.size, auditLogs.size, farmRecords.size) {
        listOf(
            AdminNavSection(
                index = 0,
                title = "Executive Overview & KPIs",
                shortTitle = "Overview",
                subtitle = "Regional Farm & Soil Health Metrics",
                icon = Icons.Default.Dashboard,
                badge = "LIVE",
                testTag = "admin_tab_overview"
            ),
            AdminNavSection(
                index = 1,
                title = "Farmer Registry & RSBSA",
                shortTitle = "Farmers",
                subtitle = "Verified Directory & Subsidies",
                icon = Icons.Default.People,
                badge = "${farmerRegistry.size}",
                testTag = "admin_tab_farmers"
            ),
            AdminNavSection(
                index = 2,
                title = "Database Console & Room Data",
                shortTitle = "Database",
                subtitle = "Manage & Delete SQLite Farm Records",
                icon = Icons.Default.Storage,
                badge = "${farmRecords.size}",
                testTag = "admin_tab_database"
            ),
            AdminNavSection(
                index = 3,
                title = "Soil Fertility & NPK Demand",
                shortTitle = "Fertilizer",
                subtitle = "Paddy Nutrient Allocation",
                icon = Icons.Default.Science,
                badge = "NPK",
                testTag = "admin_tab_fertilizer"
            ),
            AdminNavSection(
                index = 4,
                title = "Emergency & Advisory Dispatch",
                shortTitle = "Broadcast",
                subtitle = "Weather & Farming Bulletins",
                icon = Icons.Default.Notifications,
                badge = "Push",
                testTag = "admin_tab_broadcast"
            ),
            AdminNavSection(
                index = 5,
                title = "Security Audit & DB Maintenance",
                shortTitle = "Audit Logs",
                subtitle = "Administrative Action History",
                icon = Icons.Default.Shield,
                badge = "${auditLogs.size}",
                testTag = "admin_tab_audit"
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0B192C),
                drawerContentColor = Color.White,
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    // Drawer Header with CLSU Brand
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D47A1))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E3A8A))
                                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "CLSU Soil Science",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "College of Agriculture",
                                            fontSize = 11.sp,
                                            color = Color(0xFF93C5FD)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { coroutineScope.launch { drawerState.close() } },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Menu",
                                        tint = Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Live status chip
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF166534).copy(alpha = 0.5f))
                                    .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CLSU AGRI-NET • COMMAND LIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF86EFAC),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Personnel Card (Arjay Aquino - Head of Department)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1D4ED8)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "AA",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = Color(0xFFFFD54F)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (currentUser.fullName.contains("Arjay", ignoreCase = true) || currentUser.isAdmin) "Arjay Aquino" else currentUser.fullName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = Color(0xFF60A5FA),
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Text(
                                            text = "Head, Department of Soil Science",
                                            fontSize = 9.5.sp,
                                            color = Color(0xFFCBD5E1)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Mini Statistics HUD in Drawer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DrawerStatPill(
                            value = "${farmerRegistry.size}",
                            label = "Farmers",
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f)
                        )
                        DrawerStatPill(
                            value = "${farmRecords.size}",
                            label = "DB Rows",
                            color = Color(0xFFFFD54F),
                            modifier = Modifier.weight(1f)
                        )
                        val pendingCount = farmerRegistry.count { it.subsidyStatus.contains("Pending", true) }
                        DrawerStatPill(
                            value = "$pendingCount",
                            label = "Pending",
                            color = if (pendingCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        val totalHa = farmerRegistry.sumOf { it.farmSizeHa } + farmRecords.sumOf { it.areaHectares }
                        DrawerStatPill(
                            value = String.format(Locale.US, "%.1f", totalHa),
                            label = "Hectares",
                            color = Color(0xFF34D399),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "COMMAND MODULES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
                    )

                    // Navigation Drawer Items
                    navSections.forEach { section ->
                        val isSelected = selectedTab == section.index
                        NavigationDrawerItem(
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Color(0xFF1E3A8A) else Color.White.copy(alpha = 0.06f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = section.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(0xFFFFD54F) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            label = {
                                Column {
                                    Text(
                                        text = section.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.5.sp,
                                        color = if (isSelected) Color.White else Color(0xFFE2E8F0)
                                    )
                                    Text(
                                        text = section.subtitle,
                                        fontSize = 9.5.sp,
                                        color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF64748B)
                                    )
                                }
                            },
                            badge = {
                                section.badge?.let { b ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = b,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            },
                            selected = isSelected,
                            onClick = {
                                selectedTab = section.index
                                coroutineScope.launch { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                                .testTag(section.testTag),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFF1E293B),
                                unselectedContainerColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(6.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color(0xFF4ADE80)) },
                        label = {
                            Text(
                                "Switch to Farmer App View",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF86EFAC)
                            )
                        },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onBackToFarmerView()
                        },
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .testTag("drawer_switch_farmer_app"),
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFF87171)) },
                        label = {
                            Text(
                                "Sign Out of Admin Console",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFCA5A5)
                            )
                        },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onLogout()
                        },
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .testTag("drawer_logout_btn"),
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F9))
                .testTag("screen_admin_dashboard")
        ) {
            // High-Tech Top App Bar Header with Burger Menu
            Surface(
                color = Color(0xFF0B192C),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D47A1))
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // The Prominent Burger Menu Button
                    Surface(
                        onClick = { coroutineScope.launch { drawerState.open() } },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.5.dp, Color(0xFFFFD54F)),
                        modifier = Modifier.testTag("btn_admin_burger_menu")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Burger Navigation Menu",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Menu",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { coroutineScope.launch { drawerState.open() } }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = navSections.getOrNull(selectedTab)?.icon ?: Icons.Default.Dashboard,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = navSections.getOrNull(selectedTab)?.title ?: "CLSU Command Center",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "CLSU Soil Science • Tap ☰ to navigate",
                            fontSize = 10.sp,
                            color = Color(0xFF93C5FD),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Switch to Farmer View Action
                    OutlinedButton(
                        onClick = onBackToFarmerView,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_switch_to_farmer_app")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF4ADE80)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Farmer View",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Quick Logout Action
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC2626),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_admin_logout")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log Out",
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Logout",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
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

        // Tab Content List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D47A1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = navSections.getOrNull(selectedTab)?.icon ?: Icons.Default.Dashboard,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = navSections.getOrNull(selectedTab)?.title ?: "",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = navSections.getOrNull(selectedTab)?.subtitle ?: "Section ${selectedTab + 1} of ${navSections.size}",
                                fontSize = 10.5.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

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
                            farmerRegistry = farmerRegistry,
                            onAddFarmerClick = { isAddFarmerDialogOpen = true },
                            onResetRegistryClick = { isResetRegistryDialogOpen = true }
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
                                },
                                onEditClick = {
                                    farmerForEdit = farmer
                                },
                                onDeleteClick = {
                                    farmerForDelete = farmer
                                }
                            )
                        }
                    }
                }
                2 -> {
                    item {
                        RoomDatabaseManagerTab(
                            farmRecords = farmRecords,
                            onAddFarmClick = { isAddFarmDialogOpen = true },
                            onSeedSampleClick = {
                                onSeedSampleFarms()
                                feedbackMessage = "✅ 3 certified research paddy records seeded into SQLite database!"
                            },
                            onWipeAllClick = { isWipeAllFarmsDialogOpen = true },
                            onEditFarmClick = { farmRecordForEdit = it },
                            onDeleteFarmClick = { farmRecordForDelete = it }
                        )
                    }
                }
                3 -> {
                    item { RegionalFertilizerDemandTab(farmerRegistry = farmerRegistry) }
                }
                4 -> {
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
                5 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "CLSU Security Audit & DB Trail",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Button(
                                        onClick = { isClearAuditLogsDialogOpen = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("btn_clear_audit_logs")
                                    ) {
                                        Text("Clear Logs", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    text = "Every farmer accreditation, database mutation, and broadcast by Head Arjay Aquino & personnel is recorded in local audit trail.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            "SQLite: rice_farm_assistant_db",
                                            fontSize = 9.sp,
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                    Surface(
                                        color = Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            "Rows: ${farmRecords.size} farm entries",
                                            fontSize = 9.sp,
                                            color = Color(0xFF4ADE80),
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (auditLogs.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("Audit log history is empty.", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    } else {
                        items(auditLogs) { log ->
                            AuditLogCard(
                                log = log,
                                onDeleteClick = {
                                    onDeleteAuditLog(log.id)
                                    feedbackMessage = "Audit log removed."
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
}

private data class AdminNavSection(
    val index: Int,
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String? = null,
    val testTag: String
)

@Composable
private fun DrawerStatPill(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8)
            )
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
    farmerRegistry: List<FarmerRegistryItem>,
    onAddFarmerClick: () -> Unit = {},
    onResetRegistryClick: () -> Unit = {}
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

            // Action Row: Add Farmer & Reset Defaults
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddFarmerClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_register_new_farmer"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Farmer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onResetRegistryClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_reset_farmer_registry"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF475569))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Seed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    }
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
    onChangeStatusClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
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

            // Action Buttons: Approve, Status, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isApproved) {
                    Button(
                        onClick = onQuickApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .defaultMinSize(minHeight = 40.dp)
                            .testTag("btn_approve_farmer_${farmer.id}"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                "Approve",
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
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Status",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                OutlinedButton(
                    onClick = onEditClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 40.dp)
                        .testTag("btn_edit_farmer_${farmer.id}"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Farmer", modifier = Modifier.size(15.dp))
                }

                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 40.dp)
                        .testTag("btn_delete_farmer_${farmer.id}"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Farmer", modifier = Modifier.size(15.dp), tint = Color(0xFFDC2626))
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
private fun AuditLogCard(
    log: AdminAuditLog,
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("audit_log_item_${log.id}"),
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

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("btn_delete_log_${log.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete audit entry",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
