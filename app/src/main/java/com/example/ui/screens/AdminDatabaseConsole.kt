package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmRecord
import com.example.domain.models.FarmerRegistryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun RoomDatabaseManagerTab(
    farmRecords: List<FarmRecord>,
    onAddFarmClick: () -> Unit,
    onSeedSampleClick: () -> Unit,
    onWipeAllClick: () -> Unit,
    onEditFarmClick: (FarmRecord) -> Unit,
    onDeleteFarmClick: (FarmRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCropFilter by remember { mutableStateOf("All") }

    val filteredRecords = farmRecords.filter { farm ->
        (searchQuery.isBlank() || farm.name.contains(searchQuery, ignoreCase = true) || farm.cropType.contains(searchQuery, ignoreCase = true)) &&
        (selectedCropFilter == "All" || farm.cropType.contains(selectedCropFilter, ignoreCase = true))
    }

    val totalHectares = farmRecords.sumOf { it.areaHectares }
    val totalPerimeter = farmRecords.sumOf { it.perimeterMeters }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Database Header Hero Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E3A8A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SQLite Room Database Console",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Table: `farm_records` • rice_farm_assistant_db",
                                fontSize = 11.sp,
                                color = Color(0xFF93C5FD)
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF166534).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFF22C55E))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "READ/WRITE OK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF86EFAC)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "As Admin (Head of Dept. of Soil Science, CLSU), you have direct root-level permissions to inspect, modify, insert, or permanently delete GPS boundary and surveyed farm records from the local SQLite storage.",
                    fontSize = 11.5.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Database KPI metric cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatabaseMetricCard(
                        title = "SQL ROWS",
                        value = "${farmRecords.size}",
                        unit = "records",
                        accentColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    DatabaseMetricCard(
                        title = "TOTAL AREA",
                        value = String.format(Locale.US, "%.1f", totalHectares),
                        unit = "hectares",
                        accentColor = Color(0xFF34D399),
                        modifier = Modifier.weight(1f)
                    )
                    DatabaseMetricCard(
                        title = "PERIMETER",
                        value = String.format(Locale.US, "%.0f", totalPerimeter),
                        unit = "meters",
                        accentColor = Color(0xFFFFD54F),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Database Action Toolbar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "DATABASE OPERATIONS & DATA MANAGEMENT",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddFarmClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_add_farm_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add Farm", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = onSeedSampleClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_seed_farms_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF0D47A1))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Seed Data", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        }
                    }

                    Button(
                        onClick = onWipeAllClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_wipe_farms_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Wipe All", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Search and Filter Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_db_search_input"),
                    placeholder = { Text("Filter farm records by name or crop...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Lowland", "Hybrid", "Green Super").forEach { cropFilter ->
                        val isSelected = selectedCropFilter == cropFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCropFilter = cropFilter },
                            label = { Text(cropFilter, fontSize = 10.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0F172A),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // List of Farm Records from Room SQLite DB
        if (filteredRecords.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (farmRecords.isEmpty()) "No Farm Records in SQLite Database" else "No matching records found for \"$searchQuery\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (farmRecords.isEmpty()) "Click 'Seed Data' above or '+ Add Farm' to populate the Room database with certified survey data." else "Try adjusting your search keywords or crop filter.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    if (farmRecords.isEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onSeedSampleClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Seed 3 Certified Paddy Records", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            filteredRecords.forEach { farm ->
                AdminFarmRecordCard(
                    farm = farm,
                    onEditClick = { onEditFarmClick(farm) },
                    onDeleteClick = { onDeleteFarmClick(farm) }
                )
            }
        }
    }
}

@Composable
private fun DatabaseMetricCard(
    title: String,
    value: String,
    unit: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = unit,
                fontSize = 9.sp,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
fun AdminFarmRecordCard(
    farm: FarmRecord,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_farm_row_${farm.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row Header: ID Badge, Name, Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "#${farm.id}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = farm.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = farm.dateFormatted,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats grid: Area, Perimeter, Crop, GPS accuracy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("SUKAT NG LUPA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(
                        "${farm.areaHectares} ha",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF15803D)
                    )
                }

                Column {
                    Text("PERIMETER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(
                        "${farm.perimeterMeters.toInt()} m",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1)
                    )
                }

                Column {
                    Text("GPS ACCURACY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(
                        farm.gpsAccuracy,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Grass,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = farm.cropType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                Text(
                    text = "Boundaries: ${farm.boundaryPointsCount} pts",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF1F5F9)
            )

            // Database Actions: Edit & Delete Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A)),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 38.dp)
                        .testTag("btn_edit_farm_${farm.id}"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Row", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 38.dp)
                        .testTag("btn_delete_farm_${farm.id}"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete Row", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOGS FOR FARM RECORD CRUD (Room SQLite DB)
// -------------------------------------------------------------

@Composable
fun EditFarmRecordDialog(
    farm: FarmRecord,
    onDismiss: () -> Unit,
    onSave: (FarmRecord) -> Unit
) {
    var name by remember { mutableStateOf(farm.name) }
    var areaText by remember { mutableStateOf(farm.areaHectares.toString()) }
    var perimeterText by remember { mutableStateOf(farm.perimeterMeters.toString()) }
    var cropType by remember { mutableStateOf(farm.cropType) }
    var gpsAccuracy by remember { mutableStateOf(farm.gpsAccuracy) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Farm Record #${farm.id}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Farm Name / Paddy Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = areaText,
                        onValueChange = { areaText = it },
                        label = { Text("Area (ha)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = perimeterText,
                        onValueChange = { perimeterText = it },
                        label = { Text("Perimeter (m)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Variety") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gpsAccuracy,
                    onValueChange = { gpsAccuracy = it },
                    label = { Text("GPS Accuracy Grade") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val area = areaText.toDoubleOrNull() ?: farm.areaHectares
                    val perimeter = perimeterText.toDoubleOrNull() ?: farm.perimeterMeters
                    val updated = farm.copy(
                        name = name.ifBlank { farm.name },
                        areaHectares = area,
                        perimeterMeters = perimeter,
                        cropType = cropType.ifBlank { farm.cropType },
                        gpsAccuracy = gpsAccuracy.ifBlank { farm.gpsAccuracy }
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                modifier = Modifier.testTag("save_edit_farm_btn")
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun AddFarmRecordDialog(
    onDismiss: () -> Unit,
    onSave: (FarmRecord) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var areaText by remember { mutableStateOf("1.50") }
    var perimeterText by remember { mutableStateOf("480.0") }
    var cropType by remember { mutableStateOf("Lowland Inbred Rice (NSIC Rc 222)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Farm Record to SQLite", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Insert a surveyed paddy record directly into Room database (`farm_records`).",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Farm Name / Barangay Lot") },
                    placeholder = { Text("e.g. Bantug Demo Plot 3") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = areaText,
                        onValueChange = { areaText = it },
                        label = { Text("Area (Hectares)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = perimeterText,
                        onValueChange = { perimeterText = it },
                        label = { Text("Perimeter (Meters)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Variety") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val farmName = name.ifBlank { "New Surveyed Lot" }
                    val area = areaText.toDoubleOrNull() ?: 1.0
                    val perimeter = perimeterText.toDoubleOrNull() ?: 400.0
                    val dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
                    val newRecord = FarmRecord(
                        name = farmName,
                        dateFormatted = dateFormatted,
                        timestamp = System.currentTimeMillis(),
                        areaHectares = area,
                        perimeterMeters = perimeter,
                        cropType = cropType,
                        pointsJson = "[]",
                        walkedMeters = perimeter,
                        gpsAccuracy = "High (Admin Direct Entry)",
                        boundaryPointsCount = 4
                    )
                    onSave(newRecord)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                modifier = Modifier.testTag("confirm_add_farm_btn")
            ) {
                Text("Insert Record", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun DeleteFarmRecordDialog(
    farm: FarmRecord,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Farm Record?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Are you sure you want to permanently delete Farm Record #${farm.id} from the Room SQLite database?",
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("• Name: ${farm.name}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                        Text("• Area: ${farm.areaHectares} ha • Crop: ${farm.cropType}", fontSize = 11.sp, color = Color(0xFFB91C1C))
                        Text("• Date: ${farm.dateFormatted}", fontSize = 11.sp, color = Color(0xFFB91C1C))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This action is logged in the CLSU Administrative Audit Trail and cannot be undone.",
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.testTag("confirm_delete_farm_btn")
            ) {
                Text("Permanently Delete", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun WipeAllFarmsDialog(
    recordCount: Int,
    onDismiss: () -> Unit,
    onConfirmWipe: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Dangerous, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Wipe All Farm Records?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DANGER ZONE: You are about to purge all $recordCount farm records from the SQLite database (`rice_farm_assistant_db`).",
                    fontSize = 13.sp,
                    color = Color(0xFF991B1B),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "All surveyed boundaries, GPS walk logs, area calculations, and farm tags will be permanently deleted from the device.",
                    fontSize = 12.sp,
                    color = Color(0xFF334155)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmWipe,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.testTag("confirm_wipe_all_farms_btn")
            ) {
                Text("PURGE ALL DATA", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

// -------------------------------------------------------------
// DIALOGS FOR FARMER REGISTRY CRUD
// -------------------------------------------------------------

@Composable
fun EditFarmerDialog(
    farmer: FarmerRegistryItem,
    onDismiss: () -> Unit,
    onSave: (FarmerRegistryItem) -> Unit
) {
    var fullName by remember { mutableStateOf(farmer.fullName) }
    var rsbsaId by remember { mutableStateOf(farmer.rsbsaId) }
    var barangay by remember { mutableStateOf(farmer.barangay) }
    var municipality by remember { mutableStateOf(farmer.municipality) }
    var farmSizeText by remember { mutableStateOf(farmer.farmSizeHa.toString()) }
    var cropVariety by remember { mutableStateOf(farmer.cropVariety) }
    var soilType by remember { mutableStateOf(farmer.soilType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonSearch, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Farmer Registry Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rsbsaId,
                    onValueChange = { rsbsaId = it },
                    label = { Text("RSBSA Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = barangay,
                        onValueChange = { barangay = it },
                        label = { Text("Barangay") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = municipality,
                        onValueChange = { municipality = it },
                        label = { Text("Municipality") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = farmSizeText,
                        onValueChange = { farmSizeText = it },
                        label = { Text("Farm Size (ha)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = soilType,
                        onValueChange = { soilType = it },
                        label = { Text("Soil Type") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = cropVariety,
                    onValueChange = { cropVariety = it },
                    label = { Text("Primary Crop Variety") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val size = farmSizeText.toDoubleOrNull() ?: farmer.farmSizeHa
                    val updated = farmer.copy(
                        fullName = fullName.ifBlank { farmer.fullName },
                        rsbsaId = rsbsaId.ifBlank { farmer.rsbsaId },
                        barangay = barangay.ifBlank { farmer.barangay },
                        municipality = municipality.ifBlank { farmer.municipality },
                        farmSizeHa = size,
                        cropVariety = cropVariety.ifBlank { farmer.cropVariety },
                        soilType = soilType.ifBlank { farmer.soilType }
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                modifier = Modifier.testTag("save_edit_farmer_btn")
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun DeleteFarmerDialog(
    farmer: FarmerRegistryItem,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonRemove, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Remove Farmer from Registry?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Are you sure you want to delete ${farmer.fullName} from the CLSU Certified Registry?",
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("• RSBSA: ${farmer.rsbsaId}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                        Text("• Barangay: ${farmer.barangay}, ${farmer.municipality}", fontSize = 11.sp, color = Color(0xFFB91C1C))
                        Text("• Farm Area: ${farmer.farmSizeHa} ha", fontSize = 11.sp, color = Color(0xFFB91C1C))
                        Text("• Subsidy: ${farmer.subsidyStatus}", fontSize = 11.sp, color = Color(0xFFB91C1C))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This will immediately revoke their Department of Soil Science verification status.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.testTag("confirm_delete_farmer_btn")
            ) {
                Text("Delete Farmer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun AddFarmerDialog(
    onDismiss: () -> Unit,
    onSave: (FarmerRegistryItem) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var rsbsaId by remember { mutableStateOf("03-49-19-001-") }
    var barangay by remember { mutableStateOf("Bantug") }
    var municipality by remember { mutableStateOf("Science City of Muñoz") }
    var farmSizeText by remember { mutableStateOf("2.0") }
    var cropVariety by remember { mutableStateOf("NSIC Rc 222 (Tubigan 21)") }
    var soilType by remember { mutableStateOf("Maligaya Clay Loam") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register New Farmer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("e.g. Danilo R. Santos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rsbsaId,
                    onValueChange = { rsbsaId = it },
                    label = { Text("RSBSA ID Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = barangay,
                        onValueChange = { barangay = it },
                        label = { Text("Barangay") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = municipality,
                        onValueChange = { municipality = it },
                        label = { Text("Municipality") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = farmSizeText,
                        onValueChange = { farmSizeText = it },
                        label = { Text("Farm Size (ha)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = soilType,
                        onValueChange = { soilType = it },
                        label = { Text("Soil Type") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = cropVariety,
                    onValueChange = { cropVariety = it },
                    label = { Text("Crop Variety") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val name = fullName.ifBlank { "Magsasaka Demo" }
                    val size = farmSizeText.toDoubleOrNull() ?: 1.5
                    val newFarmer = FarmerRegistryItem(
                        id = "farmer_${UUID.randomUUID().toString().take(6)}",
                        fullName = name,
                        rsbsaId = rsbsaId,
                        barangay = barangay,
                        municipality = municipality,
                        farmSizeHa = size,
                        cropVariety = cropVariety,
                        soilType = soilType,
                        lastActiveDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date()),
                        subsidyStatus = "Approved (CLSU Soil-Certified)"
                    )
                    onSave(newFarmer)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                modifier = Modifier.testTag("confirm_add_farmer_btn")
            ) {
                Text("Add to Registry", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun ResetRegistryDialog(
    onDismiss: () -> Unit,
    onConfirmReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Farmer Registry?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                "Reset all farmer records to the official CLSU Department of Soil Science seed directory (Danilo Santos, Maria Clara Dela Cruz, Roberto Ramos, etc.)?",
                fontSize = 12.5.sp,
                color = Color(0xFF334155)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmReset,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                modifier = Modifier.testTag("confirm_reset_registry_btn")
            ) {
                Text("Reset to Seed Data", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}
