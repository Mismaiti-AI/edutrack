package com.edutrack.preview

// ============================================================
// MOCK MEDIA APP - Delete this entire 'preview' package when done
// Demonstrates: Camera, Media Picker, Video Player, Audio Player,
//               Map components, GenericMediaPlayerScreen, GenericMapScreen
// ============================================================

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edutrack.core.data.media.LatLng
import com.edutrack.core.data.media.MapMarker
import com.edutrack.core.data.media.MediaPickerType
import com.edutrack.core.data.media.MediaResult
import com.edutrack.core.presentation.components.AudioPlayerBar
import com.edutrack.core.presentation.components.CaptureButton
import com.edutrack.core.presentation.components.DetailCard
import com.edutrack.core.presentation.components.DetailRow
import com.edutrack.core.presentation.components.InfoRow
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.MapCard
import com.edutrack.core.presentation.components.MediaPreview
import com.edutrack.core.presentation.components.PickMediaButton
import com.edutrack.core.presentation.components.SectionHeader
import com.edutrack.core.presentation.components.StatusBadge
import com.edutrack.core.presentation.components.VideoPlayerView
import com.edutrack.core.presentation.media.CameraCaptureMode
import com.edutrack.core.presentation.navigation.AppOrchestrator
import com.edutrack.core.presentation.navigation.AppState
import com.edutrack.core.presentation.navigation.NavigationTab
import com.edutrack.core.presentation.screens.DashboardStat
import com.edutrack.core.presentation.screens.GenericDashboardScreen
import com.edutrack.core.presentation.screens.GenericDetailScreen
import com.edutrack.core.presentation.screens.GenericListScreen
import com.edutrack.core.presentation.screens.GenericMapScreen
import com.edutrack.core.presentation.screens.GenericMediaPlayerScreen
import com.edutrack.core.presentation.screens.GenericSplashScreen
import com.edutrack.core.presentation.screens.MapScreenConfig
import com.edutrack.core.presentation.screens.MediaPlayerItem
import com.edutrack.core.presentation.screens.QuickAction
import kotlinx.serialization.Serializable

// ============================================================
// Routes
// ============================================================

object MediaRoutes {
    @Serializable object Dashboard
    @Serializable object Lessons
    @Serializable object Explore
    @Serializable object Profile
    @Serializable data class LessonPlayer(val lessonId: String)
    @Serializable data class StoreDetail(val storeId: String)
    @Serializable object StoreMap
    @Serializable object Settings
}

// ============================================================
// Mock Data — Lessons (Video/Audio)
// ============================================================

data class MockLesson(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String?,
    val duration: String,
    val isAudio: Boolean,
    val instructor: String,
    val category: String
)

val mockLessons = listOf(
    MockLesson(
        id = "l1",
        title = "Introduction to KMP",
        description = "Learn the fundamentals of KMP and how to share code between Android and iOS.",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbnailUrl = null,
        duration = "12:45",
        isAudio = false,
        instructor = "Alex Chen",
        category = "Mobile Dev"
    ),
    MockLesson(
        id = "l2",
        title = "Compose Multiplatform",
        description = "Deep dive into building shared UIs with Compose across platforms.",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        thumbnailUrl = null,
        duration = "18:30",
        isAudio = false,
        instructor = "Sarah Park",
        category = "UI/UX"
    ),
    MockLesson(
        id = "l3",
        title = "Architecture Best Practices",
        description = "Repository pattern, dependency injection, and clean architecture in KMP.",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        thumbnailUrl = null,
        duration = "22:10",
        isAudio = false,
        instructor = "Mike Torres",
        category = "Architecture"
    ),
    MockLesson(
        id = "l4",
        title = "Weekly Roundup #42",
        description = "This week's news in Kotlin Multiplatform development.",
        videoUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        thumbnailUrl = null,
        duration = "35:20",
        isAudio = true,
        instructor = "Dev Team",
        category = "Podcast"
    ),
    MockLesson(
        id = "l5",
        title = "Navigation Deep Dive",
        description = "Type-safe navigation patterns in Compose Multiplatform apps.",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        thumbnailUrl = null,
        duration = "15:55",
        isAudio = false,
        instructor = "Alex Chen",
        category = "Mobile Dev"
    ),
)

// ============================================================
// Mock Data — Stores (Map locations)
// ============================================================

data class MockStore(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val rating: String,
    val hours: String
)

val mockStores = listOf(
    MockStore("s1", "Downtown Cafe", "123 Main St", 37.7749, -122.4194, "Coffee", "4.5", "7am - 9pm"),
    MockStore("s2", "Tech Hub Store", "456 Market St", 37.7851, -122.4094, "Electronics", "4.2", "9am - 8pm"),
    MockStore("s3", "Green Park Deli", "789 Park Ave", 37.7694, -122.4262, "Food", "4.7", "6am - 10pm"),
    MockStore("s4", "Sunset Books", "321 Ocean Blvd", 37.7599, -122.5107, "Books", "4.8", "10am - 7pm"),
    MockStore("s5", "Bay Fitness", "555 Embarcadero", 37.7946, -122.3999, "Gym", "4.3", "5am - 11pm"),
)

// ============================================================
// Entry Point
// ============================================================

@Composable
fun MockMediaApp() {
    var appState by remember { mutableStateOf(AppState.Splash) }

    AppOrchestrator(
        appState = appState,

        splashContent = {
            GenericSplashScreen(
                appName = "MediaHub",
                tagline = "Learn, explore, discover",
                icon = Icons.Default.PlayCircle,
                durationMillis = 1500,
                onFinished = { appState = AppState.Home }
            )
        },

        tabs = listOf(
            NavigationTab(MediaRoutes.Dashboard, "Home", Icons.Default.Home),
            NavigationTab(MediaRoutes.Lessons, "Lessons", Icons.Default.VideoLibrary),
            NavigationTab(MediaRoutes.Explore, "Explore", Icons.Default.Explore),
            NavigationTab(MediaRoutes.Profile, "Profile", Icons.Default.Person),
        ),
        homeStartDestination = MediaRoutes.Dashboard,
        showTopBar = false,
        homeBuilder = { nav ->
            // Tab screens
            composable<MediaRoutes.Dashboard> { MediaDashboardTab(nav) }
            composable<MediaRoutes.Lessons> { MediaLessonsTab(nav) }
            composable<MediaRoutes.Explore> { MediaExploreTab(nav) }
            composable<MediaRoutes.Profile> { MediaProfileTab(nav) }

            // Detail routes
            composable<MediaRoutes.LessonPlayer> { entry ->
                val route = entry.toRoute<MediaRoutes.LessonPlayer>()
                LessonPlayerScreen(route.lessonId, nav)
            }
            composable<MediaRoutes.StoreDetail> { entry ->
                val route = entry.toRoute<MediaRoutes.StoreDetail>()
                StoreDetailScreen(route.storeId, nav)
            }
            composable<MediaRoutes.StoreMap> { StoreMapScreen(nav) }
        }
    )
}

// ============================================================
// Dashboard — Media stats + quick actions + recent lessons
// ============================================================

@Composable
private fun MediaDashboardTab(nav: NavHostController) {
    GenericDashboardScreen(
        title = "MediaHub",
        greeting = "Welcome back, learner!",
        stats = listOf(
            DashboardStat("Lessons", "${mockLessons.size}", Icons.Default.PlayCircle),
            DashboardStat("Hours", "8.5", Icons.Default.MusicNote),
            DashboardStat("Stores", "${mockStores.size}", Icons.Default.LocationOn),
        ),
        quickActions = listOf(
            QuickAction("Browse Lessons", Icons.Default.VideoLibrary) {
                nav.navigate(MediaRoutes.Lessons)
            },
            QuickAction("Store Map", Icons.Default.Map) {
                nav.navigate(MediaRoutes.StoreMap)
            },
        ),
        recentItems = mockLessons.take(3),
        recentTitle = "Continue Learning",
        onRecentItemClick = { nav.navigate(MediaRoutes.LessonPlayer(it.id)) },
        recentItemContent = { lesson ->
            ListItemCard(
                title = lesson.title,
                subtitle = "${lesson.instructor} · ${lesson.duration}",
                caption = lesson.category,
                leadingIcon = if (lesson.isAudio) Icons.Default.MusicNote else Icons.Default.PlayCircle,
                showChevron = true,
                trailingContent = {
                    StatusBadge(text = lesson.category)
                }
            )
        },
        extraContent = {
            SectionHeader(
                title = "Nearby Stores",
                actionText = "View Map",
                onActionClick = { nav.navigate(MediaRoutes.StoreMap) }
            )
            // MapCard embedded in dashboard — shows all store markers
            MapCard(
                center = LatLng(37.7749, -122.4194),
                markers = mockStores.map {
                    MapMarker(LatLng(it.latitude, it.longitude), it.name)
                },
                zoom = 12f,
                height = 180.dp,
                onClick = { nav.navigate(MediaRoutes.StoreMap) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    )
}

// ============================================================
// Lessons Tab — List of video/audio lessons
// ============================================================

@Composable
private fun MediaLessonsTab(nav: NavHostController) {
    GenericListScreen(
        title = "Lessons",
        items = mockLessons,
        onItemClick = { nav.navigate(MediaRoutes.LessonPlayer(it.id)) },
        showFab = false,
        emptyMessage = "No lessons available"
    ) { lesson ->
        ListItemCard(
            title = lesson.title,
            subtitle = "${lesson.instructor} · ${lesson.duration}",
            caption = lesson.description,
            leadingIcon = if (lesson.isAudio) Icons.Default.AudioFile else Icons.Default.PlayCircle,
            showChevron = true,
            trailingContent = {
                StatusBadge(text = if (lesson.isAudio) "Audio" else "Video")
            }
        )
    }
}

// ============================================================
// Explore Tab — Map view of nearby stores
// ============================================================

@Composable
private fun MediaExploreTab(nav: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }

    GenericMapScreen(
        title = "Explore",
        config = MapScreenConfig(
            initialCenter = LatLng(37.7749, -122.4194),
            initialZoom = 12f
        ),
        markers = mockStores.map {
            MapMarker(
                position = LatLng(it.latitude, it.longitude),
                title = it.name,
                snippet = it.category
            )
        },
        showBack = false,
        onMarkerClick = { marker ->
            val store = mockStores.find { it.name == marker.title }
            store?.let { nav.navigate(MediaRoutes.StoreDetail(it.id)) }
        },
        showSearchBar = true,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onSearchSubmit = { /* filter markers */ }
    )
}

// ============================================================
// Profile Tab — Camera + file picker demo
// ============================================================

@Composable
private fun MediaProfileTab(nav: NavHostController) {
    var capturedPhoto by remember { mutableStateOf<MediaResult?>(null) }
    var pickedImage by remember { mutableStateOf<MediaResult?>(null) }
    var pickedFile by remember { mutableStateOf<MediaResult?>(null) }

    GenericDetailScreen(
        title = "My Profile",
        item = Unit,
        showBack = false
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // -- Camera Section --
            SectionHeader(title = "Camera Capture")
            CaptureButton(
                mode = CameraCaptureMode.VIDEO,
                onCaptured = { capturedPhoto = it },
                label = "Take Profile Photo",
                icon = Icons.Default.PhotoCamera,
                modifier = Modifier.fillMaxWidth()
            )
            capturedPhoto?.let {
                MediaPreview(
                    result = it,
                    onRemove = { capturedPhoto = null }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // -- Image Picker Section --
            SectionHeader(title = "Media Picker")
            PickMediaButton(
                onPicked = { pickedImage = it },
                type = MediaPickerType.IMAGE_AND_VIDEO,
                label = "Pick Image",
                icon = Icons.Default.Image,
                modifier = Modifier.fillMaxWidth()
            )
            pickedImage?.let {
                MediaPreview(
                    result = it,
                    onRemove = { pickedImage = null }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // -- File Picker Section --
            SectionHeader(title = "File Picker")
            PickMediaButton(
                onPicked = { pickedFile = it },
                type = MediaPickerType.FILE,
                label = "Pick File",
                icon = Icons.Default.Upload,
                modifier = Modifier.fillMaxWidth()
            )
            pickedFile?.let {
                MediaPreview(
                    result = it,
                    onRemove = { pickedFile = null }
                )
            }
        }
    }
}

// ============================================================
// Lesson Player Screen — GenericMediaPlayerScreen demo
// ============================================================

@Composable
private fun LessonPlayerScreen(lessonId: String, nav: NavHostController) {
    val lesson = mockLessons.find { it.id == lessonId }

    GenericMediaPlayerScreen(
        title = lesson?.title ?: "Lesson",
        item = lesson?.let {
            MediaPlayerItem(
                title = it.title,
                url = it.videoUrl,
                thumbnailUrl = it.thumbnailUrl,
                subtitle = it.description,
                duration = it.duration,
                isAudio = it.isAudio
            )
        },
        onBackClick = { nav.popBackStack() },
        relatedItems = mockLessons
            .filter { it.id != lessonId }
            .take(3)
            .map {
                MediaPlayerItem(
                    title = it.title,
                    url = it.videoUrl,
                    thumbnailUrl = it.thumbnailUrl,
                    subtitle = "${it.instructor} · ${it.duration}",
                    duration = it.duration,
                    isAudio = it.isAudio
                )
            },
        onRelatedItemClick = { relatedItem ->
            val related = mockLessons.find { it.videoUrl == relatedItem.url }
            related?.let { nav.navigate(MediaRoutes.LessonPlayer(it.id)) }
        },
        detailContent = { item ->
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (lesson != null) {
                    InfoRow(label = "Instructor", value = lesson.instructor)
                    InfoRow(label = "Category", value = lesson.category)
                    InfoRow(label = "Duration", value = lesson.duration)
                }
            }
        }
    )
}

// ============================================================
// Store Detail Screen — Map + detail info
// ============================================================

@Composable
private fun StoreDetailScreen(storeId: String, nav: NavHostController) {
    val store = mockStores.find { it.id == storeId }

    GenericDetailScreen(
        title = store?.name ?: "Store",
        item = store,
        onBackClick = { nav.popBackStack() }
    ) { s ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Map preview card showing store location
            MapCard(
                center = LatLng(s.latitude, s.longitude),
                markers = listOf(
                    MapMarker(
                        position = LatLng(s.latitude, s.longitude),
                        title = s.name,
                        snippet = s.address
                    )
                ),
                zoom = 15f,
                height = 200.dp,
                onClick = { nav.navigate(MediaRoutes.StoreMap) }
            )

            DetailCard(
                title = "Store Info",
                icon = Icons.Default.LocationOn,
                rows = listOf(
                    DetailRow("Address", s.address),
                    DetailRow("Category", s.category),
                    DetailRow("Rating", s.rating),
                    DetailRow("Hours", s.hours),
                )
            )

            // Inline audio player for store ambiance/podcast
            SectionHeader(title = "Store Podcast")
            AudioPlayerBar(
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                title = "About ${s.name}",
                subtitle = "Store spotlight episode"
            )

            // Inline video player for store tour
            SectionHeader(title = "Virtual Tour")
            VideoPlayerView(
                url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                showControls = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

// ============================================================
// Full Store Map Screen — GenericMapScreen with bottom sheet
// ============================================================

@Composable
private fun StoreMapScreen(nav: NavHostController) {
    var selectedStore by remember { mutableStateOf<MockStore?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    GenericMapScreen(
        title = "Store Locations",
        config = MapScreenConfig(
            initialCenter = LatLng(37.7749, -122.4194),
            initialZoom = 12f
        ),
        markers = mockStores.map {
            MapMarker(
                position = LatLng(it.latitude, it.longitude),
                title = it.name,
                snippet = "${it.category} · ${it.rating}"
            )
        },
        onBackClick = { nav.popBackStack() },
        onMarkerClick = { marker ->
            selectedStore = mockStores.find { it.name == marker.title }
        },
        showSearchBar = true,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        bottomSheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedStore != null) {
                    val s = selectedStore!!
                    Text(
                        text = s.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = s.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailCard(
                        title = "Details",
                        rows = listOf(
                            DetailRow("Category", s.category),
                            DetailRow("Rating", s.rating),
                            DetailRow("Hours", s.hours),
                        )
                    )
                } else {
                    Text(
                        text = "Tap a marker to see details",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${mockStores.size} stores nearby",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}
