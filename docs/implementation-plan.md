# Implementation Plan

## Phase 1: Theme
App colors and Material 3 theme

- [x] AppColors.kt — Color palette with primary, secondary, tertiary, error colors derived from ui_design.seed_color
- [x] AppTheme.kt — Material 3 theme composable wrapping MaterialTheme with light/dark color schemes and configureSystemAppearance expect/actual

## Phase 2: Feature: Assignment Tracker
All layers for Assignment Tracker feature

- [ ] AssignmentEntity.kt — Room @Entity for Assignment with fields: id ( String), title ( String), subject ( String), dueDate ( Instant), description ( String), attachmentUrl ( String). Store Instant as Long (toEpochMilliseconds).
- [ ] AssignmentDao.kt — Room @Dao for AssignmentEntity with getAll(): Flow, getById(id), insert, update, deleteById
- [ ] [modify] AppDatabase.kt — Add AssignmentEntity to @Database entities array, add assignmentDao() abstract fun, increment version
- [x] Assignment.kt — Domain data class for Assignment with fields: id ( String), title ( String), subject ( String), dueDate ( Instant), description ( String), attachmentUrl ( String). Include toEntity() extension and Entity.toDomain() mapper. Use kotlin.time.Instant for date/time fields.
- [x] AssignmentRepository.kt — Repository interface for Assignment with items: StateFlow, isLoading: StateFlow, error: StateFlow, loadAll, getById, insert, update, delete
- [x] AssignmentRepositoryImpl.kt — Repository implementation for Assignment. Extends GoogleSheetsRepository<Assignment> (pre-built). Implements mapRow() to parse sheet rows. Uses AssignmentDao for offline caching.
- [x] GetAssignmentListUseCase.kt — operator fun invoke(): StateFlow<List<Assignment>> from repository. suspend fun load() triggers loadAll. suspend fun refresh() triggers refresh.
- [x] GetAssignmentDetailUseCase.kt — suspend fun load(id: String) selects specific Assignment from repository.
- [ ] SaveAssignmentUseCase.kt — suspend fun save(item: Assignment) calls repository insert/update. Handles both create and edit.
- [x] DeleteAssignmentUseCase.kt — suspend fun execute(id: String) deletes Assignment from repository.
- [x] AssignmentListViewModel.kt — THIN ViewModel extending BaseViewModel. Combines use case StateFlows with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] AssignmentListScreen.kt — Composable wrapping GenericListScreen from core/presentation/screens/. Uses koinViewModel() default parameter.
- [x] AssignmentDetailViewModel.kt — THIN ViewModel extending BaseViewModel. Combines use case StateFlows with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] AssignmentDetailScreen.kt — Composable wrapping GenericDetailScreen from core/presentation/screens/. Uses koinViewModel() default parameter.
- [ ] AssignmentFormViewModel.kt — THIN ViewModel extending BaseViewModel. Combines use case StateFlows with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [ ] AssignmentFormScreen.kt — Composable wrapping GenericFormScreen from core/presentation/screens/. Uses koinViewModel() default parameter.

## Phase 3: Feature: Exam Planner
All layers for Exam Planner feature

- [ ] ExamEntity.kt — Room @Entity for Exam with fields: id ( String), title ( String), subject ( String), examDate ( Instant), topics ( String), description ( String). Store Instant as Long (toEpochMilliseconds).
- [ ] ExamDao.kt — Room @Dao for ExamEntity with getAll(): Flow, getById(id), insert, update, deleteById
- [ ] [modify] AppDatabase.kt — Add ExamEntity to @Database entities array, add examDao() abstract fun, increment version
- [x] Exam.kt — Domain data class for Exam with fields: id ( String), title ( String), subject ( String), examDate ( Instant), topics ( String), description ( String). Include toEntity() extension and Entity.toDomain() mapper. Use kotlin.time.Instant for date/time fields.
- [x] ExamRepository.kt — Repository interface for Exam with items: StateFlow, isLoading: StateFlow, error: StateFlow, loadAll, getById, insert, update, delete
- [x] ExamRepositoryImpl.kt — Repository implementation for Exam. Extends GoogleSheetsRepository<Exam> (pre-built). Implements mapRow() to parse sheet rows. Uses ExamDao for offline caching.
- [x] GetExamListUseCase.kt — operator fun invoke(): StateFlow<List<Exam>> from repository. suspend fun load() triggers loadAll. suspend fun refresh() triggers refresh.
- [x] GetExamDetailUseCase.kt — suspend fun load(id: String) selects specific Exam from repository.
- [x] DeleteExamUseCase.kt — suspend fun execute(id: String) deletes Exam from repository.
- [x] ExamListViewModel.kt — THIN ViewModel extending BaseViewModel. Combines use case StateFlows with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] ExamListScreen.kt — Composable wrapping GenericListScreen from core/presentation/screens/. Uses koinViewModel() default parameter.
- [x] ExamDetailViewModel.kt — THIN ViewModel extending BaseViewModel. Combines use case StateFlows with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] ExamDetailScreen.kt — Composable wrapping GenericDetailScreen from core/presentation/screens/. Uses koinViewModel() default parameter.

## Phase 4: Feature: Project Manager
All layers for Project Manager feature

- [ ] ProjectEntity.kt — Room @Entity for Project with fields: id ( String), title ( String), subject ( String), startDate ( Instant), dueDate ( Instant), description ( String), milestones ( String), attachmentUrl ( String). Store Instant as Long (toEpochMilliseconds).
- [ ] ProjectDao.kt — Room @Dao for ProjectEntity with getAll(): Flow, getById(id), insert, update, deleteById
- [ ] [modify] AppDatabase.kt — Add ProjectEntity to @Database entities array, add projectDao() abstract fun, increment version
- [x] Project.kt — Domain data class for Project with fields: id ( String), title ( String), subject ( String), startDate ( Instant), dueDate ( Instant), description ( String), milestones ( String), attachmentUrl ( String). Include toEntity() extension and Entity.toDomain() mapper. Use kotlin.time.Instant for date/time fields.
- [x] ProjectRepository.kt — Repository interface for Project with items: StateFlow, isLoading: StateFlow, error: StateFlow, loadAll, getById, insert, update, delete
- [x] ProjectRepositoryImpl.kt — Repository implementation for Project. Extends GoogleSheetsRepository<Project> (pre-built). Implements mapRow() to parse sheet rows. Uses ProjectDao for offline caching.
- [x] GetProjectListUseCase.kt — operator fun invoke(): StateFlow<List<Project>> from repository. suspend fun load() triggers loadAll. suspend fun refresh() triggers refresh.
- [x] GetProjectDetailUseCase.kt — suspend fun load(id: String) selects specific Project from repository.
- [x] DeleteProjectUseCase.kt — suspend fun execute(id: String) deletes Project from repository.
- [x] ProjectListViewModel.kt — THIN ViewModel extending BaseViewModel. Combines use case StateFlows with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] ProjectListScreen.kt — Composable wrapping GenericListScreen from core/presentation/screens/. Uses koinViewModel() default parameter.
- [x] ProjectDetailViewModel.kt — THIN ViewModel extending BaseViewModel. Combines use case StateFlows with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] ProjectDetailScreen.kt — Composable wrapping GenericDetailScreen from core/presentation/screens/. Uses koinViewModel() default parameter.

## Phase 5: Feature: Dashboard
Dashboard/Home screen — aggregates data from other features

- [x] GetDashboardOverviewUseCase.kt — Dashboard use case — injects AssignmentRepository, ExamRepository, ProjectRepository, exposes combined StateFlow with DashboardOverview(stats, quickActions, recentItems). operator fun invoke() returns StateFlow. IMPORTANT: Define the DashboardOverview data class IN THIS FILE (it does not exist elsewhere). Do NOT create a Dashboard domain model, repository, or entity — Dashboard is an aggregation view, not a stored entity.
- [x] DashboardViewModel.kt — THIN ViewModel extending BaseViewModel. Combines GetDashboardOverviewUseCase() flow with isLoading + error via combine().stateIn(). UiState sealed interface in same file. IMPORT DashboardOverview from the use case file — do NOT redefine it here.
- [x] DashboardScreen.kt — Composable wrapping GenericDashboardScreen from core/presentation/screens/. Maps DashboardOverview to stats (2-3 StatCards), quickActions (2 max), and recentItems list.

## Phase 6: App Wiring
Create routes, register DI, wire AppOrchestrator in App.kt

- [x] AppRoutes.kt — @Serializable route objects/data classes for all screens. Use @Serializable object for tab destinations, @Serializable data class for detail screens with parameters.
- [x] [modify] AppModule.kt — Register all project-specific DAOs (single { get<AppDatabase>().xxxDao() }), repositories (singleOf), use cases (factoryOf), and ViewModels (viewModelOf). Do NOT re-register pre-built services from coreModule().
- [ ] [modify] App.kt — Wire AppOrchestrator inside AppTheme: define NavigationTab list, set homeStartDestination, and register ALL screen composables inside homeBuilder lambda. Without this, app shows blank screen.

## Cleanup: Unused Template Files
Files/directories to delete before code generation:

- composeApp/src/commonMain/kotlin/com/edutrack/preview/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/auth/
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/auth/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/chat/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/deeplink/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/notifications/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/firestore/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/media/MapModels.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/media/PlayerSource.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/media/MediaResult.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/media/MediaPickerType.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/media/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/calendar/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/charts/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/messaging/
- composeApp/src/commonMain/kotlin/com/edutrack/core/data/payment/
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericPaywallScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericConversationListScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericAuthScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericCalendarScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericMediaPlayerScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericChartScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericGalleryScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericChatScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericSubscriptionScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericTabScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericSearchScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericNotificationScreen.kt
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericMapScreen.kt
