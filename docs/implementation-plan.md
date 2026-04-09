# Implementation Plan

## Phase 1: Theme
App colors and Material 3 theme

- [ ] AppColors.kt — Color palette with primary, secondary, tertiary, error colors derived from ui_design.seed_color
- [x] AppTheme.kt — Material 3 theme composable wrapping MaterialTheme with light/dark color schemes and configureSystemAppearance expect/actual

## Phase 2: Feature: Assignment Tracking
Data layer for Assignment Tracking feature (model, repository, use cases, ViewModels)

- [ ] AssignmentEntity.kt — Room @Entity for Assignment with fields: id ( String), title ( String), subject ( String), dueDate ( Instant), instructions ( String). Store Instant as Long (toEpochMilliseconds).
- [ ] AssignmentDao.kt — Room @Dao for AssignmentEntity with getAll(): Flow, getById(id), insert, update, deleteById
- [ ] [modify] AppDatabase.kt — Add AssignmentEntity to @Database entities array, add assignmentDao() abstract fun, increment version
- [x] Assignment.kt — Domain data class for Assignment with fields: id ( String), title ( String), subject ( String), dueDate ( Instant), instructions ( String). Include toEntity() extension and Entity.toDomain() mapper. Use kotlin.time.Instant for date/time fields.
- [x] AssignmentRepository.kt — Repository interface for Assignment with items: StateFlow, isLoading: StateFlow, error: StateFlow, loadAll, getById, insert, update, delete
- [x] AssignmentRepositoryImpl.kt — Repository implementation for Assignment. Extends GoogleSheetsRepository<Assignment> (pre-built). Implements mapRow() to parse sheet rows. Uses AssignmentDao for offline caching.
- [x] GetAssignmentListUseCase.kt — operator fun invoke(): StateFlow<List<Assignment>> from repository. suspend fun load() triggers loadAll. suspend fun refresh() triggers refresh.
- [x] GetAssignmentDetailUseCase.kt — suspend fun load(id: String) selects specific Assignment from repository.
- [ ] SaveAssignmentUseCase.kt — suspend fun save(item: Assignment) calls repository insert/update. Handles both create and edit.
- [x] DeleteAssignmentUseCase.kt — suspend fun execute(id: String) calls repository delete.
- [x] AssignmentListViewModel.kt — THIN ViewModel extending BaseViewModel. Combines GetAssignmentListUseCase() with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] AssignmentDetailViewModel.kt — THIN ViewModel extending BaseViewModel for Assignment detail. Uses GetAssignmentDetailUseCase.
- [ ] AssignmentFormViewModel.kt — THIN ViewModel extending BaseViewModel for Assignment create/edit form. Uses SaveAssignmentUseCase.

## Phase 3: Feature: Exam Tracking
Data layer for Exam Tracking feature (model, repository, use cases, ViewModels)

- [ ] ExamEntity.kt — Room @Entity for Exam with fields: id ( String), name ( String), subject ( String), dateTime ( Instant), description ( String), studyReference ( String). Store Instant as Long (toEpochMilliseconds).
- [ ] ExamDao.kt — Room @Dao for ExamEntity with getAll(): Flow, getById(id), insert, update, deleteById
- [ ] [modify] AppDatabase.kt — Add ExamEntity to @Database entities array, add examDao() abstract fun, increment version
- [x] Exam.kt — Domain data class for Exam with fields: id ( String), name ( String), subject ( String), dateTime ( Instant), description ( String), studyReference ( String). Include toEntity() extension and Entity.toDomain() mapper. Use kotlin.time.Instant for date/time fields.
- [x] ExamRepository.kt — Repository interface for Exam with items: StateFlow, isLoading: StateFlow, error: StateFlow, loadAll, getById, insert, update, delete
- [x] ExamRepositoryImpl.kt — Repository implementation for Exam. Extends GoogleSheetsRepository<Exam> (pre-built). Implements mapRow() to parse sheet rows. Uses ExamDao for offline caching.
- [x] GetExamListUseCase.kt — operator fun invoke(): StateFlow<List<Exam>> from repository. suspend fun load() triggers loadAll. suspend fun refresh() triggers refresh.
- [x] GetExamDetailUseCase.kt — suspend fun load(id: String) selects specific Exam from repository.
- [x] DeleteExamUseCase.kt — suspend fun execute(id: String) calls repository delete.
- [x] ExamListViewModel.kt — THIN ViewModel extending BaseViewModel. Combines GetExamListUseCase() with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] ExamDetailViewModel.kt — THIN ViewModel extending BaseViewModel for Exam detail. Uses GetExamDetailUseCase.

## Phase 4: Feature: Project Tracking
Data layer for Project Tracking feature (model, repository, use cases, ViewModels)

- [ ] ProjectEntity.kt — Room @Entity for Project with fields: id ( String), title ( String), subject ( String), dueDate ( Instant), description ( String), teamMembers ( String). Store Instant as Long (toEpochMilliseconds).
- [ ] ProjectDao.kt — Room @Dao for ProjectEntity with getAll(): Flow, getById(id), insert, update, deleteById
- [ ] [modify] AppDatabase.kt — Add ProjectEntity to @Database entities array, add projectDao() abstract fun, increment version
- [x] Project.kt — Domain data class for Project with fields: id ( String), title ( String), subject ( String), dueDate ( Instant), description ( String), teamMembers ( String). Include toEntity() extension and Entity.toDomain() mapper. Use kotlin.time.Instant for date/time fields.
- [x] ProjectRepository.kt — Repository interface for Project with items: StateFlow, isLoading: StateFlow, error: StateFlow, loadAll, getById, insert, update, delete
- [x] ProjectRepositoryImpl.kt — Repository implementation for Project. Extends GoogleSheetsRepository<Project> (pre-built). Implements mapRow() to parse sheet rows. Uses ProjectDao for offline caching.
- [x] GetProjectListUseCase.kt — operator fun invoke(): StateFlow<List<Project>> from repository. suspend fun load() triggers loadAll. suspend fun refresh() triggers refresh.
- [x] GetProjectDetailUseCase.kt — suspend fun load(id: String) selects specific Project from repository.
- [x] DeleteProjectUseCase.kt — suspend fun execute(id: String) calls repository delete.
- [x] ProjectListViewModel.kt — THIN ViewModel extending BaseViewModel. Combines GetProjectListUseCase() with isLoading + error via combine().stateIn(). UiState sealed interface in same file.
- [x] ProjectDetailViewModel.kt — THIN ViewModel extending BaseViewModel for Project detail. Uses GetProjectDetailUseCase.

## Phase 5: UI Rendering
Render all screens from ScreenConfig templates. Home screen type: dashboard. Screen templates generate complete Compose UI from slot configurations.

- [x] [render] Unknown — Generate atoms, hero blocks, and all screen composables from ScreenConfig slot-based Jinja2 templates

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
- composeApp/src/commonMain/kotlin/com/edutrack/core/presentation/screens/GenericAuthScreen.kt
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
