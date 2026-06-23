package com.within.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.within.app.R
import com.within.app.data.model.Category
import com.within.app.ui.components.WithinTimePickerDialog
import com.within.app.ui.util.labelRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FrequencySection(
                count = uiState.messagesPerDay,
                onCountChange = viewModel::setMessagesPerDay
            )
            HorizontalDivider()
            NotificationTimesSection(
                times = uiState.notificationTimes,
                onTimeChange = viewModel::setTimeAt
            )
            HorizontalDivider()
            CategoriesSection(
                enabled = uiState.enabledCategories,
                onToggle = viewModel::toggleCategory
            )
        }
    }
}

@Composable
private fun FrequencySection(count: Int, onCountChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.settings_frequency), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { n ->
                FilterChip(
                    selected = n == count,
                    onClick = { onCountChange(n) },
                    label = { Text("$n") }
                )
            }
        }
    }
}

@Composable
private fun NotificationTimesSection(times: List<String>, onTimeChange: (Int, String) -> Unit) {
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.settings_notification_times), style = MaterialTheme.typography.titleMedium)
        times.forEachIndexed { index, time ->
            ListItem(
                headlineContent = { Text(stringResource(R.string.onboarding_time_slot, index + 1)) },
                trailingContent = {
                    Text(time, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall)
                },
                modifier = Modifier.clickable { editingIndex = index }
            )
        }
    }

    editingIndex?.let { idx ->
        WithinTimePickerDialog(
            initialTime = times[idx],
            onConfirm = { time -> onTimeChange(idx, time); editingIndex = null },
            onDismiss = { editingIndex = null }
        )
    }
}

@Composable
private fun CategoriesSection(enabled: Set<Category>, onToggle: (Category) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.settings_categories), style = MaterialTheme.typography.titleMedium)
        Category.entries.forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(category.labelRes))
                Switch(
                    checked = category in enabled,
                    onCheckedChange = { onToggle(category) },
                    enabled = !(category in enabled && enabled.size == 1)
                )
            }
        }
    }
}
