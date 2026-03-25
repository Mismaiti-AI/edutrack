package com.edutrack.preview

// ============================================================
// MOCK APP - Delete this entire 'preview' package when done
// ============================================================

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edutrack.core.presentation.components.DetailCard
import com.edutrack.core.presentation.components.DetailRow
import com.edutrack.core.presentation.components.InfoRow
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.StatusBadge
import com.edutrack.core.presentation.navigation.AppState
import com.edutrack.core.presentation.navigation.NavigationTab
import com.edutrack.core.presentation.navigation.Routes
import com.edutrack.core.presentation.screens.DashboardStat
import com.edutrack.core.presentation.screens.FieldType
import com.edutrack.core.presentation.screens.FormField
import com.edutrack.core.presentation.screens.GenericAuthScreen
import com.edutrack.core.presentation.screens.GenericDashboardScreen
import com.edutrack.core.presentation.screens.GenericDetailScreen
import com.edutrack.core.presentation.screens.GenericFormScreen
import com.edutrack.core.presentation.screens.GenericOnboardingScreen
import com.edutrack.core.presentation.screens.GenericProfileScreen
import com.edutrack.core.presentation.screens.GenericSearchScreen
import com.edutrack.core.presentation.screens.GenericSettingsScreen
import com.edutrack.core.presentation.screens.GenericSplashScreen
import com.edutrack.core.presentation.screens.OnboardingPage
import com.edutrack.core.presentation.screens.ProfileMenuSection
import com.edutrack.core.presentation.screens.ProfileMenuItem
import com.edutrack.core.presentation.screens.ProfileStat
import com.edutrack.core.presentation.screens.QuickAction
import com.edutrack.core.presentation.screens.SearchFilterChip
import com.edutrack.core.presentation.screens.SettingsItem
import com.edutrack.core.presentation.screens.SettingsSection
import com.edutrack.core.presentation.screens.SocialButton

// ============================================================
// Mock Data
// ============================================================

data class MockOrder(
    val id: String,
    val customer: String,
    val total: String,
    val status: String,
    val date: String,
    val items: Int
)

val mockOrders = listOf(
    MockOrder("1001", "John Doe", "$125.00", "Shipped", "Feb 14, 2026", 3),
    MockOrder("1002", "Jane Smith", "$89.50", "Pending", "Feb 13, 2026", 2),
    MockOrder("1003", "Bob Wilson", "$245.00", "Delivered", "Feb 12, 2026", 5),
    MockOrder("1004", "Alice Brown", "$67.00", "Processing", "Feb 11, 2026", 1),
    MockOrder("1005", "Charlie Davis", "$198.00", "Shipped", "Feb 10, 2026", 4),
)

// ============================================================
// Mock App Entry Point
// ============================================================

@Composable
fun MockApp() {
    var appState by remember { mutableStateOf(AppState.Splash) }

    com.mismaiti.core.presentation.navigation.AppOrchestrator(
        appState = appState,

        // --- Splash ---
        splashContent = {
            GenericSplashScreen(
                appName = "Mock Store",
                tagline = "Your favorite shopping app",
                icon = Icons.Default.ShoppingCart,
                durationMillis = 2000,
                onFinished = {
                    appState =
                        AppState.Onboarding
                }
            )
        },

        // --- Onboarding ---
        onboardingContent = {
            GenericOnboardingScreen(
                pages = listOf(
                    OnboardingPage(
                        title = "Browse Products",
                        description = "Explore thousands of products from top brands",
                        icon = Icons.Default.Search
                    ),
                    OnboardingPage(
                        title = "Track Orders",
                        description = "Real-time tracking for all your orders",
                        icon = Icons.Default.Checklist
                    ),
                    OnboardingPage(
                        title = "Get Started",
                        description = "Create your account and start shopping",
                        icon = Icons.Default.Star
                    ),
                ),
                onFinish = {
                    appState =
                        AppState.Auth
                },
                onSkip = {
                    appState =
                        AppState.Auth
                }
            )
        },

        // --- Auth ---
        authStartDestination = Routes.Login,
        authBuilder = { nav ->
            composable<Routes.Login> {
                MockAuthScreen(
                    onLoginSuccess = {
                        appState =
                            AppState.Home
                    },
                    onNavigateSignup = { nav.navigate(Routes.Signup) }
                )
            }
            composable<Routes.Signup> {
                MockAuthScreen(
                    isLogin = false,
                    onLoginSuccess = {
                        appState =
                            AppState.Home
                    },
                    onNavigateSignup = { nav.popBackStack() }
                )
            }
        },

        // --- Home (bottom tabs) ---
        tabs = listOf(
            NavigationTab(
                Routes.Home,
                "Home",
                Icons.Default.Home
            ),
            NavigationTab(
                Routes.Search,
                "Search",
                Icons.Default.Search
            ),
            NavigationTab(
                Routes.Profile,
                "Profile",
                Icons.Default.Person
            ),
        ),
        homeStartDestination = Routes.Home,
        showTopBar = false,  // each screen has its own TopAppBar with actions
        homeBuilder = { nav ->
            // Tab screens
            composable<Routes.Home> {
                MockDashboardTab(nav)
            }
            composable<Routes.Search> {
                MockSearchTab()
            }
            composable<Routes.Profile> {
                MockProfileTab()
            }

            // Detail routes (bottom bar auto-hides)
            composable<Routes.Detail> { entry ->
                val detail =
                    entry.toRoute<Routes.Detail>()
                MockOrderDetail(detail.itemId, nav)
            }
            composable<Routes.Edit> { entry ->
                val edit = entry.toRoute<Routes.Edit>()
                MockOrderForm(edit.itemId, nav)
            }
            composable<Routes.Create> {
                MockOrderForm(null, nav)
            }
            composable<Routes.Settings> {
                MockSettingsScreen(nav)
            }
        }
    )
}

// ============================================================
// Mock Screens (wired to generic screens)
// ============================================================

@Composable
private fun MockAuthScreen(
    isLogin: Boolean = true,
    onLoginSuccess: () -> Unit,
    onNavigateSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    GenericAuthScreen(
        isLogin = isLogin,
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSubmit = onLoginSuccess,
        onToggleMode = onNavigateSignup,
        onForgotPassword = { },
        showName = !isLogin,
        name = name,
        onNameChange = { name = it },
        socialButtons = listOf(
            SocialButton(
                label = "Continue with Google",
                icon = Icons.Default.Email,
                onClick = onLoginSuccess
            )
        )
    )
}

@Composable
private fun MockDashboardTab(nav: NavHostController) {
    GenericDashboardScreen(
        title = "Mock Store",
        greeting = "Welcome back, John",
        stats = listOf(
            DashboardStat(
                "Orders",
                "24",
                Icons.Default.ShoppingCart
            ),
            DashboardStat(
                "Revenue",
                "$4.2K",
                Icons.Default.AttachMoney
            ),
            DashboardStat(
                "Customers",
                "128",
                Icons.Default.People
            ),
            DashboardStat(
                "Analytics",
                "12%",
                Icons.Default.Analytics
            ),
        ),
        quickActions = listOf(
            QuickAction(
                "New Order",
                Icons.Default.Add
            ) { nav.navigate(Routes.Create) },
            QuickAction(
                "Search",
                Icons.Default.Search
            ) { nav.navigate(Routes.Search) },
        ),
        recentItems = mockOrders.take(3),
        recentTitle = "Recent Orders",
        onRecentItemClick = {
            nav.navigate(
                Routes.Detail(
                    it.id
                )
            )
        },
        recentItemContent = { order ->
            ListItemCard(
                title = order.customer,
                subtitle = "Order #${order.id} · ${order.total}",
                caption = order.date,
                leadingIcon = Icons.Default.ShoppingCart,
                showChevron = true,
                trailingContent = {
                    StatusBadge(text = order.status)
                }
            )
        },
        onSettingsClick = { nav.navigate(Routes.Settings) }
    )
}

@Composable
private fun MockSearchTab() {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }

    val filters = listOf(
        SearchFilterChip(
            "all",
            "All",
            selectedFilter == "all"
        ),
        SearchFilterChip(
            "shipped",
            "Shipped",
            selectedFilter == "shipped"
        ),
        SearchFilterChip(
            "pending",
            "Pending",
            selectedFilter == "pending"
        ),
        SearchFilterChip(
            "delivered",
            "Delivered",
            selectedFilter == "delivered"
        ),
    )

    val filtered = mockOrders.filter { order ->
        val matchesQuery = query.isBlank() ||
                order.customer.contains(query, ignoreCase = true) ||
                order.id.contains(query)
        val matchesFilter = selectedFilter == "all" ||
                order.status.equals(selectedFilter, ignoreCase = true)
        matchesQuery && matchesFilter
    }

    GenericSearchScreen(
        query = query,
        onQueryChange = { query = it },
        results = filtered,
        filterChips = filters,
        onFilterClick = { selectedFilter = it.key },
        recentSearches = listOf("John Doe", "Order 1001", "Shipped"),
        onRecentClick = { query = it },
        onClearRecent = { },
        emptyMessage = "No orders match your search"
    ) { order ->
        ListItemCard(
            title = order.customer,
            subtitle = "Order #${order.id} · ${order.total}",
            caption = order.date,
            showChevron = true,
            trailingContent = {
                StatusBadge(
                    text = order.status
                )
            }
        )
    }
}

@Composable
private fun MockProfileTab() {
    GenericProfileScreen(
        name = "John Doe",
        subtitle = "john@example.com",
        showBack = false,
        stats = listOf(
            ProfileStat("Orders", "24"),
            ProfileStat("Wishlist", "8"),
            ProfileStat("Reviews", "12"),
        ),
        menuSections = listOf(
            ProfileMenuSection(
                title = "Account",
                items = listOf(
                    ProfileMenuItem(
                        title = "Edit Profile",
                        subtitle = "Name, email, phone",
                        icon = Icons.Default.Edit,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Change Password",
                        icon = Icons.Default.Lock,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Notifications",
                        subtitle = "Push, email, SMS",
                        icon = Icons.Default.Notifications,
                        onClick = { }
                    ),
                )
            ),
            ProfileMenuSection(
                title = "Support",
                items = listOf(
                    ProfileMenuItem(
                        title = "About",
                        subtitle = "Version 1.0.0",
                        icon = Icons.Default.Info,
                        onClick = { }
                    ),
                )
            ),
        ),
        bottomContent = {
            Button(
                onClick = { },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Log Out")
            }
        }
    )
}

@Composable
private fun MockOrderDetail(orderId: String, nav: NavHostController) {
    val order = mockOrders.find { it.id == orderId }

    GenericDetailScreen(
        title = "Order #$orderId",
        item = order,
        onBackClick = { nav.popBackStack() },
        onEditClick = {
            nav.navigate(
                Routes.Edit(
                    orderId
                )
            )
        },
        onDeleteClick = { nav.popBackStack() },
    ) { o ->
        DetailCard(
            title = "Order Summary",
            icon = Icons.Default.ShoppingCart,
            rows = listOf(
                DetailRow(
                    "Customer",
                    o.customer
                ),
                DetailRow("Status") {
                    StatusBadge(text = o.status)
                },
                DetailRow(
                    "Total",
                    o.total
                ),
                DetailRow(
                    "Items",
                    "${o.items} items"
                ),
                DetailRow(
                    "Date",
                    o.date
                ),
            )
        )

        InfoRow(
            label = "Order ID",
            value = o.id,
            icon = Icons.Default.Info
        )
        InfoRow(
            label = "Customer",
            value = o.customer,
            icon = Icons.Default.Person
        )
        InfoRow(
            label = "Email",
            value = "customer@example.com",
            icon = Icons.Default.Email
        )
    }
}

@Composable
private fun MockOrderForm(orderId: String?, nav: NavHostController) {
    val isEdit = orderId != null
    val order = orderId?.let { id -> mockOrders.find { it.id == id } }

    val fieldValues = remember {
        mutableStateMapOf(
            "customer" to (order?.customer ?: ""),
            "total" to (order?.total?.removePrefix("$") ?: ""),
            "items" to (order?.items?.toString() ?: ""),
            "status" to (order?.status ?: "Pending"),
        )
    }

    GenericFormScreen(
        title = if (isEdit) "Edit Order #$orderId" else "New Order",
        fields = listOf(
            FormField(
                key = "customer",
                label = "Customer Name",
                value = fieldValues["customer"] ?: "",
                required = true,
                placeholder = "Enter customer name"
            ),
            FormField(
                key = "total",
                label = "Total Amount",
                value = fieldValues["total"] ?: "",
                type = FieldType.Number,
                required = true,
                placeholder = "0.00"
            ),
            FormField(
                key = "items",
                label = "Number of Items",
                value = fieldValues["items"] ?: "",
                type = FieldType.Number,
                placeholder = "0"
            ),
            FormField(
                key = "status",
                label = "Status",
                value = fieldValues["status"] ?: "",
                type = FieldType.Dropdown,
                options = listOf("Pending", "Processing", "Shipped", "Delivered")
            ),
        ),
        onFieldChange = { key, value -> fieldValues[key] = value },
        onSubmit = { nav.popBackStack() },
        onBackClick = { nav.popBackStack() },
        submitText = if (isEdit) "Update" else "Create"
    )
}

@Composable
private fun MockSettingsScreen(nav: NavHostController) {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }

    GenericSettingsScreen(
        title = "Settings",
        onBackClick = { nav.popBackStack() },
        sections = listOf(
            SettingsSection(
                title = "Appearance",
                items = listOf(
                    SettingsItem.Toggle(
                        title = "Dark Mode",
                        subtitle = "Use dark theme",
                        icon = Icons.Default.DarkMode,
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    ),
                    SettingsItem.Navigation(
                        title = "Language",
                        subtitle = "English",
                        icon = Icons.Default.Language,
                        onClick = { }
                    ),
                )
            ),
            SettingsSection(
                title = "Notifications",
                items = listOf(
                    SettingsItem.Toggle(
                        title = "Push Notifications",
                        subtitle = "Receive order updates",
                        icon = Icons.Default.Notifications,
                        checked = notifications,
                        onCheckedChange = { notifications = it }
                    ),
                )
            ),
            SettingsSection(
                title = "About",
                items = listOf(
                    SettingsItem.Info(
                        title = "Version",
                        value = "1.0.0"
                    ),
                    SettingsItem.Info(
                        title = "Build",
                        value = "2026.02.16"
                    ),
                    SettingsItem.Navigation(
                        title = "Log Out",
                        icon = Icons.AutoMirrored.Filled.Logout,
                        onClick = { }
                    ),
                )
            ),
        )
    )
}
