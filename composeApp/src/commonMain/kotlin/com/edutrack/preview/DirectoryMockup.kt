package com.edutrack.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edutrack.core.presentation.components.DetailCard
import com.edutrack.core.presentation.components.DetailRow
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.RatingBar
import com.edutrack.core.presentation.components.SectionHeader
import com.edutrack.core.presentation.components.StatusBadge
import com.edutrack.core.presentation.screens.GenericListScreen

private data class Business(
    val name: String,
    val category: String,
    val rating: Float,
    val distance: String,
    val address: String,
    val phone: String,
    val hours: String,
    val priceLevel: String
)

private val mockBusinesses = listOf(
    Business("The Rustic Table", "Restaurant", 4.7f, "0.3 mi", "142 Main St", "(555) 234-5678", "11 AM - 10 PM", "$$"),
    Business("Bloom & Brew Coffee", "Coffee Shop", 4.5f, "0.5 mi", "88 Oak Ave", "(555) 345-6789", "6 AM - 8 PM", "$"),
    Business("Urban Fit Gym", "Fitness", 4.3f, "0.8 mi", "320 Elm Blvd", "(555) 456-7890", "5 AM - 11 PM", "$$"),
    Business("Page Turner Books", "Bookstore", 4.8f, "1.1 mi", "56 Maple Dr", "(555) 567-8901", "9 AM - 9 PM", "$"),
    Business("Precision Auto Care", "Auto Service", 4.2f, "1.4 mi", "710 Industrial Pkwy", "(555) 678-9012", "8 AM - 6 PM", "$$"),
    Business("Fresh & Green Market", "Grocery", 4.4f, "0.6 mi", "205 Cedar Ln", "(555) 789-0123", "7 AM - 10 PM", "$"),
    Business("Serenity Spa & Wellness", "Spa", 4.6f, "1.7 mi", "430 Pine St", "(555) 890-1234", "10 AM - 8 PM", "$$$")
)

@Composable
fun DirectoryMockup() {
    GenericListScreen(
        title = "Local Directory",
        items = mockBusinesses,
        searchEnabled = true,
        filterEnabled = true,
        showFab = false,
        emptyMessage = "No businesses found"
    ) { business ->
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(text = business.category)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = business.priceLevel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    RatingBar(rating = business.rating, starSize = 16.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = business.distance,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            DetailCard(
                title = "Details",
                icon = Icons.Default.Store,
                rows = listOf(
                    DetailRow(label = "Address", value = business.address),
                    DetailRow(label = "Phone", value = business.phone),
                    DetailRow(label = "Hours", value = business.hours)
                ),
                actionText = "Directions",
                onActionClick = {}
            )
        }
    }
}
