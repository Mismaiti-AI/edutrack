package com.edutrack.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.edutrack.core.data.charts.ChartDataPoint
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.PieChart
import com.edutrack.core.presentation.components.ChartCard
import com.edutrack.core.presentation.components.StatusBadge
import com.edutrack.core.presentation.screens.DashboardStat
import com.edutrack.core.presentation.screens.GenericDashboardScreen
import com.edutrack.core.presentation.screens.QuickAction

private data class Transaction(
    val merchant: String,
    val category: String,
    val amount: String,
    val date: String,
    val isExpense: Boolean
)

private val spendingCategories = listOf(
    ChartDataPoint("Housing", 1800f, Color(0xFF2196F3)),
    ChartDataPoint("Food", 620f, Color(0xFF4CAF50)),
    ChartDataPoint("Transport", 340f, Color(0xFFFF9800)),
    ChartDataPoint("Entertainment", 250f, Color(0xFF9C27B0)),
    ChartDataPoint("Utilities", 180f, Color(0xFF00BCD4)),
    ChartDataPoint("Other", 310f, Color(0xFF607D8B))
)

private val recentTransactions = listOf(
    Transaction("Whole Foods Market", "Food", "-$84.32", "Today", true),
    Transaction("Salary Deposit", "Income", "+$4,200.00", "Mar 1", false),
    Transaction("Netflix", "Entertainment", "-$15.99", "Mar 1", true),
    Transaction("Shell Gas Station", "Transport", "-$52.40", "Feb 28", true),
    Transaction("Electric Company", "Utilities", "-$94.50", "Feb 27", true)
)

@Composable
fun FinanceMockup() {
    GenericDashboardScreen(
        title = "BudgetWise",
        greeting = "Good morning, Riley",
        stats = listOf(
            DashboardStat(label = "Balance", value = "$12,450", icon = Icons.Default.AccountBalance),
            DashboardStat(label = "Income", value = "$4,200", icon = Icons.Default.TrendingUp, iconTint = Color(0xFF4CAF50)),
            DashboardStat(label = "Expenses", value = "$3,500", icon = Icons.Default.ShoppingCart, iconTint = Color(0xFFF44336)),
        ),
        quickActions = listOf(
            QuickAction(label = "Add Expense", icon = Icons.Default.ShoppingCart, onClick = {}),
            QuickAction(label = "Set Budget", icon = Icons.Default.Savings, onClick = {})
        ),
        recentItems = recentTransactions,
        recentTitle = "Recent Transactions",
        onSeeAllClick = {},
        onSettingsClick = {},
        recentItemContent = { tx ->
            ListItemCard(
                title = tx.merchant,
                subtitle = tx.category,
                caption = tx.date,
                leadingIcon = if (tx.isExpense) Icons.Default.ShoppingCart else Icons.Default.TrendingUp,
                leadingIconTint = if (tx.isExpense) Color(0xFFF44336) else Color(0xFF4CAF50),
                trailingContent = {
                    StatusBadge(
                        text = tx.amount,
                        containerColor = if (tx.isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        contentColor = if (tx.isExpense) Color(0xFFC62828) else Color(0xFF2E7D32)
                    )
                }
            )
        },
        extraContent = {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                ChartCard(
                    title = "Spending Breakdown",
                    subtitle = "This month"
                ) {
                    PieChart(
                        data = spendingCategories,
                        isDonut = true,
                        showLegend = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    )
}
