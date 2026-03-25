package com.edutrack.preview

// ============================================================
// MOCK SOCIAL APP - Delete this entire 'preview' package when done
// ============================================================

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edutrack.core.presentation.components.ChipGroup
import com.edutrack.core.presentation.components.ImageCard
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.navigation.AppOrchestrator
import com.edutrack.core.presentation.navigation.AppState
import com.edutrack.core.presentation.navigation.NavigationTab
import com.edutrack.core.presentation.screens.FormField
import com.edutrack.core.presentation.screens.GenericAuthScreen
import com.edutrack.core.presentation.screens.GenericDetailScreen
import com.edutrack.core.presentation.screens.GenericFormScreen
import com.edutrack.core.presentation.screens.GenericListScreen
import com.edutrack.core.presentation.screens.GenericOnboardingScreen
import com.edutrack.core.presentation.screens.GenericProfileScreen
import com.edutrack.core.presentation.screens.GenericSearchScreen
import com.edutrack.core.presentation.screens.GenericSettingsScreen
import com.edutrack.core.presentation.screens.GenericSplashScreen
import com.edutrack.core.presentation.screens.OnboardingPage
import com.edutrack.core.presentation.screens.ProfileMenuSection
import com.edutrack.core.presentation.screens.ProfileMenuItem
import com.edutrack.core.presentation.screens.ProfileStat
import com.edutrack.core.presentation.screens.SearchFilterChip
import com.edutrack.core.presentation.screens.SettingsSection
import com.edutrack.core.presentation.screens.SocialButton
import kotlinx.serialization.Serializable

// ============================================================
// Social Routes
// ============================================================

object SocialRoutes {
    @Serializable object Feed
    @Serializable object Explore
    @Serializable object Profile
    @Serializable data class PostDetail(val postId: String)
    @Serializable data class UserProfile(val userId: String)
    @Serializable object CreatePost
    @Serializable object Settings
    @Serializable object Login
    @Serializable object Signup
}

// ============================================================
// Mock Data
// ============================================================

data class MockPost(
    val id: String,
    val author: String,
    val authorHandle: String,
    val content: String,
    val imageUrl: String?,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val timeAgo: String,
    val tags: List<String> = emptyList()
)

data class MockUser(
    val id: String,
    val name: String,
    val handle: String,
    val bio: String,
    val followers: Int,
    val following: Int,
    val posts: Int
)

val mockPosts = listOf(
    MockPost(
        "p1", "Sarah Chen", "@sarahc",
        "Just launched our new community feature! Check it out and let me know what you think.",
        "https://picsum.photos/seed/post1/800/400",
        142, 28, 12, "2h",
        listOf("launch", "product", "community")
    ),
    MockPost(
        "p2", "Alex Rivera", "@alexr",
        "Beautiful sunset at the beach today. Nature never disappoints.",
        "https://picsum.photos/seed/post2/800/400",
        89, 15, 5, "4h",
        listOf("nature", "photography")
    ),
    MockPost(
        "p3", "Dev Community", "@devcom",
        "Kotlin Multiplatform is the future of cross-platform development. Who agrees?",
        null,
        256, 64, 32, "6h",
        listOf("kotlin", "development", "tech")
    ),
    MockPost(
        "p4", "Maria Lopez", "@marial",
        "Recipe of the day: homemade pasta with truffle sauce. So good!",
        "https://picsum.photos/seed/post4/800/400",
        198, 42, 18, "8h",
        listOf("food", "recipe", "cooking")
    ),
    MockPost(
        "p5", "Tech News", "@technews",
        "Breaking: New AI model achieves state-of-the-art results on multiple benchmarks.",
        "https://picsum.photos/seed/post5/800/400",
        534, 128, 96, "12h",
        listOf("ai", "tech", "news")
    ),
    MockPost(
        "p6", "John Park", "@johnp",
        "Morning coffee and code. The best combo for productivity.",
        null,
        67, 8, 3, "1d",
        listOf("coding", "lifestyle")
    ),
)

val mockUsers = listOf(
    MockUser("u1", "Sarah Chen", "@sarahc", "Product designer & maker", 1240, 340, 89),
    MockUser("u2", "Alex Rivera", "@alexr", "Photographer & traveler", 3400, 120, 256),
    MockUser("u3", "Dev Community", "@devcom", "Developer community hub", 12500, 50, 430),
    MockUser("u4", "Maria Lopez", "@marial", "Food blogger & chef", 8900, 210, 178),
)

// ============================================================
// Mock Social App Entry Point
// ============================================================

@Composable
fun MockSocialApp() {
    var appState by remember { mutableStateOf(AppState.Splash) }

    AppOrchestrator(
        appState = appState,

        // --- Splash ---
        splashContent = {
            GenericSplashScreen(
                appName = "Connect",
                tagline = "Share your world",
                icon = Icons.Default.People,
                durationMillis = 1500,
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
                        title = "Share Moments",
                        description = "Post photos, thoughts, and updates with your community",
                        icon = Icons.Default.Image
                    ),
                    OnboardingPage(
                        title = "Discover People",
                        description = "Find and follow people who share your interests",
                        icon = Icons.Default.Explore
                    ),
                    OnboardingPage(
                        title = "Join the Conversation",
                        description = "Comment, like, and share posts you love",
                        icon = Icons.AutoMirrored.Filled.Chat
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
        authStartDestination = SocialRoutes.Login,
        authBuilder = { nav ->
            composable<SocialRoutes.Login> {
                SocialAuthScreen(
                    onLoginSuccess = {
                        appState =
                            AppState.Home
                    },
                    onNavigateSignup = { nav.navigate(SocialRoutes.Signup) }
                )
            }
            composable<SocialRoutes.Signup> {
                SocialAuthScreen(
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
                SocialRoutes.Feed,
                "Feed",
                Icons.Default.Home
            ),
            NavigationTab(
                SocialRoutes.Explore,
                "Explore",
                Icons.Default.Search
            ),
            NavigationTab(
                SocialRoutes.Profile,
                "Profile",
                Icons.Default.Person
            ),
        ),
        homeStartDestination = SocialRoutes.Feed,
        showTopBar = false,
        homeBuilder = { nav ->
            // Tab screens
            composable<SocialRoutes.Feed> {
                SocialFeedTab(nav)
            }
            composable<SocialRoutes.Explore> {
                SocialExploreTab(nav)
            }
            composable<SocialRoutes.Profile> {
                SocialProfileTab(nav)
            }

            // Detail routes
            composable<SocialRoutes.PostDetail> { entry ->
                val detail = entry.toRoute<SocialRoutes.PostDetail>()
                SocialPostDetail(detail.postId, nav)
            }
            composable<SocialRoutes.UserProfile> { entry ->
                val user = entry.toRoute<SocialRoutes.UserProfile>()
                SocialUserProfile(user.userId, nav)
            }
            composable<SocialRoutes.CreatePost> {
                SocialCreatePost(nav)
            }
            composable<SocialRoutes.Settings> {
                SocialSettingsScreen(nav)
            }
        }
    )
}

// ============================================================
// Social Screens
// ============================================================

@Composable
private fun SocialAuthScreen(
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
        showName = !isLogin,
        name = name,
        onNameChange = { name = it },
        socialButtons = listOf(
            SocialButton(
                "Continue with Google",
                onClick = onLoginSuccess
            ),
            SocialButton(
                "Continue with Apple",
                onClick = onLoginSuccess
            ),
        )
    )
}

@Composable
private fun SocialFeedTab(nav: NavHostController) {
    GenericListScreen(
        title = "Feed",
        items = mockPosts,
        onItemClick = { nav.navigate(SocialRoutes.PostDetail(it.id)) },
        onAddClick = { nav.navigate(SocialRoutes.CreatePost) },
    ) { post ->
        ImageCard(
            imageUrl = post.imageUrl ?: "https://picsum.photos/seed/${post.id}/800/400",
            title = post.author,
            subtitle = post.content,
            footerContent = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "${post.likes}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Comment",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "${post.comments}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "${post.shares}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Bookmark,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun SocialExploreTab(nav: NavHostController) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("posts") }

    val filters = listOf(
        SearchFilterChip(
            "posts",
            "Posts",
            selectedFilter == "posts"
        ),
        SearchFilterChip(
            "people",
            "People",
            selectedFilter == "people"
        ),
        SearchFilterChip(
            "tags",
            "Tags",
            selectedFilter == "tags"
        ),
    )

    if (selectedFilter == "people") {
        val filteredUsers = if (query.isBlank()) mockUsers
        else mockUsers.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.handle.contains(query, ignoreCase = true)
        }

        GenericSearchScreen(
            query = query,
            onQueryChange = { query = it },
            results = filteredUsers,
            filterChips = filters,
            onFilterClick = { selectedFilter = it.key },
            onResultClick = { nav.navigate(SocialRoutes.UserProfile(it.id)) },
            recentSearches = listOf("Sarah Chen", "Dev Community"),
            onRecentClick = { query = it },
            onClearRecent = { },
            emptyMessage = "No people found"
        ) { user ->
            ListItemCard(
                title = user.name,
                subtitle = user.handle,
                caption = "${user.followers} followers",
                leadingIcon = Icons.Default.Person,
                showChevron = true,
                trailingContent = {
                    TextButton(onClick = {}) {
                        Text("Follow")
                    }
                }
            )
        }
    } else {
        val filteredPosts = if (query.isBlank()) mockPosts
        else mockPosts.filter {
            it.content.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }

        GenericSearchScreen(
            query = query,
            onQueryChange = { query = it },
            results = filteredPosts,
            filterChips = filters,
            onFilterClick = { selectedFilter = it.key },
            onResultClick = { nav.navigate(SocialRoutes.PostDetail(it.id)) },
            recentSearches = listOf("kotlin", "photography", "recipe"),
            onRecentClick = { query = it },
            onClearRecent = { },
            emptyMessage = "No posts found"
        ) { post ->
            ListItemCard(
                title = post.author,
                subtitle = post.content.take(80) + if (post.content.length > 80) "..." else "",
                caption = "${post.timeAgo} · ${post.likes} likes",
                leadingIcon = if (post.imageUrl != null) Icons.Default.Image else Icons.AutoMirrored.Filled.Chat,
                showChevron = true,
            )
        }
    }
}

@Composable
private fun SocialProfileTab(nav: NavHostController) {
    GenericProfileScreen(
        name = "John Doe",
        subtitle = "@johnd · Creator & developer",
        showBack = false,
        onEditClick = { },
        stats = listOf(
            ProfileStat("Posts", "48"),
            ProfileStat("Followers", "1.2K"),
            ProfileStat("Following", "340"),
        ),
        headerExtraContent = {
            // Tags/interests
            val interests = setOf("tech", "design", "kotlin", "photography")
            ChipGroup(
                chips = interests.toList(),
                selectedChips = interests,
                onChipClick = { }
            )
        },
        menuSections = listOf(
            ProfileMenuSection(
                title = "Content",
                items = listOf(
                    ProfileMenuItem(
                        title = "My Posts",
                        subtitle = "48 posts",
                        icon = Icons.Default.Edit,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Saved",
                        subtitle = "12 saved posts",
                        icon = Icons.Default.Bookmark,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Liked",
                        subtitle = "256 liked posts",
                        icon = Icons.Default.Favorite,
                        onClick = { }
                    ),
                )
            ),
            ProfileMenuSection(
                title = "Account",
                items = listOf(
                    ProfileMenuItem(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        onClick = { nav.navigate(SocialRoutes.Settings) }
                    ),
                )
            ),
        )
    )
}

@Composable
private fun SocialPostDetail(postId: String, nav: NavHostController) {
    val post = mockPosts.find { it.id == postId }

    GenericDetailScreen(
        title = post?.author ?: "Post",
        item = post,
        onBackClick = { nav.popBackStack() },
    ) { p ->
        Column(modifier = Modifier.padding(16.dp)) {
            // Author info
            ListItemCard(
                title = p.author,
                subtitle = p.authorHandle,
                caption = p.timeAgo,
                leadingIcon = Icons.Default.Person,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                text = p.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tags
            if (p.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                ChipGroup(
                    chips = p.tags,
                    selectedChips = p.tags.toSet(),
                    onChipClick = { }
                )
            }

            // Image
            if (p.imageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ImageCard(
                    imageUrl = p.imageUrl,
                    title = "",
                    subtitle = null
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Engagement stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${p.likes}", style = MaterialTheme.typography.titleMedium)
                    Text("Likes", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${p.comments}", style = MaterialTheme.typography.titleMedium)
                    Text("Comments", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${p.shares}", style = MaterialTheme.typography.titleMedium)
                    Text("Shares", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun SocialUserProfile(userId: String, nav: NavHostController) {
    val user = mockUsers.find { it.id == userId }

    if (user != null) {
        GenericProfileScreen(
            name = user.name,
            subtitle = "${user.handle} · ${user.bio}",
            onBackClick = { nav.popBackStack() },
            stats = listOf(
                ProfileStat(
                    "Posts",
                    "${user.posts}"
                ),
                ProfileStat(
                    "Followers",
                    "${user.followers}"
                ),
                ProfileStat(
                    "Following",
                    "${user.following}"
                ),
            ),
            headerExtraContent = {
                TextButton(onClick = {}) {
                    Icon(Icons.Default.PersonAdd, null)
                    Text("  Follow")
                }
            },
            menuSections = listOf(
                ProfileMenuSection(
                    items = listOf(
                        ProfileMenuItem(
                            title = "View Posts",
                            subtitle = "${user.posts} posts",
                            icon = Icons.Default.Edit,
                            onClick = { }
                        ),
                    )
                ),
            )
        )
    }
}

@Composable
private fun SocialCreatePost(nav: NavHostController) {
    val fieldValues = remember {
        mutableStateMapOf(
            "content" to "",
            "tags" to "",
        )
    }

    GenericFormScreen(
        title = "New Post",
        fields = listOf(
            FormField(
                key = "content",
                label = "What's on your mind?",
                value = fieldValues["content"] ?: "",
                type = com.mismaiti.core.presentation.screens.FieldType.MultiLine,
                required = true,
                placeholder = "Share something with your community..."
            ),
            FormField(
                key = "tags",
                label = "Tags",
                value = fieldValues["tags"] ?: "",
                placeholder = "tech, photography, food (comma separated)"
            ),
        ),
        onFieldChange = { key, value -> fieldValues[key] = value },
        onSubmit = { nav.popBackStack() },
        onBackClick = { nav.popBackStack() },
        submitText = "Post"
    )
}

@Composable
private fun SocialSettingsScreen(nav: NavHostController) {
    var notifications by remember { mutableStateOf(true) }
    var privateAccount by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(false) }

    GenericSettingsScreen(
        title = "Settings",
        onBackClick = { nav.popBackStack() },
        sections = listOf(
            SettingsSection(
                title = "Privacy",
                items = listOf(
                    com.mismaiti.core.presentation.screens.SettingsItem.Toggle(
                        title = "Private Account",
                        subtitle = "Only followers can see your posts",
                        icon = Icons.Default.Lock,
                        checked = privateAccount,
                        onCheckedChange = { privateAccount = it }
                    ),
                )
            ),
            SettingsSection(
                title = "Notifications",
                items = listOf(
                    com.mismaiti.core.presentation.screens.SettingsItem.Toggle(
                        title = "Push Notifications",
                        subtitle = "Likes, comments, and follows",
                        icon = Icons.Default.Notifications,
                        checked = notifications,
                        onCheckedChange = { notifications = it }
                    ),
                )
            ),
            SettingsSection(
                title = "Appearance",
                items = listOf(
                    com.mismaiti.core.presentation.screens.SettingsItem.Toggle(
                        title = "Dark Mode",
                        icon = Icons.Default.DarkMode,
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    ),
                    com.mismaiti.core.presentation.screens.SettingsItem.Navigation(
                        title = "Language",
                        subtitle = "English",
                        icon = Icons.Default.Language,
                        onClick = { }
                    ),
                )
            ),
            SettingsSection(
                title = "About",
                items = listOf(
                    com.mismaiti.core.presentation.screens.SettingsItem.Info(
                        title = "Version",
                        value = "3.2.0"
                    ),
                )
            ),
        )
    )
}
