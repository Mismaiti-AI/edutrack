package com.edutrack.di

import com.edutrack.core.data.local.AppDatabase
import com.edutrack.core.di.coreModule
import com.edutrack.core.di.platformModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import com.edutrack.data.repository.assignment.AssignmentRepository
import com.edutrack.data.repository.assignment.AssignmentRepositoryImpl
import com.edutrack.domain.usecase.GetAssignmentListUseCase
import com.edutrack.domain.usecase.GetAssignmentDetailUseCase
import com.edutrack.domain.usecase.DeleteAssignmentUseCase
import com.edutrack.presentation.assignmenttracking.AssignmentListViewModel
import com.edutrack.presentation.assignmenttracking.AssignmentDetailViewModel
import com.edutrack.data.repository.exam.ExamRepository
import com.edutrack.data.repository.exam.ExamRepositoryImpl
import com.edutrack.domain.usecase.GetExamListUseCase
import com.edutrack.domain.usecase.GetExamDetailUseCase
import com.edutrack.domain.usecase.DeleteExamUseCase
import com.edutrack.presentation.examtracking.ExamListViewModel
import com.edutrack.presentation.examtracking.ExamDetailViewModel
import com.edutrack.data.repository.project.ProjectRepository
import com.edutrack.data.repository.project.ProjectRepositoryImpl
import com.edutrack.domain.usecase.GetProjectListUseCase
import com.edutrack.domain.usecase.GetProjectDetailUseCase
import com.edutrack.domain.usecase.DeleteProjectUseCase
import com.edutrack.presentation.projecttracking.ProjectListViewModel
import com.edutrack.presentation.projecttracking.ProjectDetailViewModel
import com.edutrack.domain.usecase.GetDashboardOverviewUseCase
import com.edutrack.presentation.dashboard.DashboardViewModel

fun moduleList(): List<Module> = listOf(
    platformModule(),
    coreModule(),
    appModule()
)

fun appModule() = module {
    // ── Repositories ──
    singleOf(::AssignmentRepositoryImpl) { bind<AssignmentRepository>() }
    singleOf(::ExamRepositoryImpl) { bind<ExamRepository>() }
    singleOf(::ProjectRepositoryImpl) { bind<ProjectRepository>() }

    // ── Use Cases (CRUD) ──
    singleOf(::GetAssignmentListUseCase)
    singleOf(::GetAssignmentDetailUseCase)
    singleOf(::DeleteAssignmentUseCase)
    singleOf(::GetExamListUseCase)
    singleOf(::GetExamDetailUseCase)
    singleOf(::DeleteExamUseCase)
    singleOf(::GetProjectListUseCase)
    singleOf(::GetProjectDetailUseCase)
    singleOf(::DeleteProjectUseCase)
    singleOf(::GetDashboardOverviewUseCase)

    // ── ViewModels ──
    viewModelOf(::AssignmentListViewModel)
    viewModel { params ->
        AssignmentDetailViewModel(
            itemId = params.get<String>(),
            getDetailUseCase = get(),
            deleteUseCase = get()
        )
    }
    viewModelOf(::ExamListViewModel)
    viewModel { params ->
        ExamDetailViewModel(
            itemId = params.get<String>(),
            getDetailUseCase = get(),
            deleteUseCase = get()
        )
    }
    viewModelOf(::ProjectListViewModel)
    viewModel { params ->
        ProjectDetailViewModel(
            itemId = params.get<String>(),
            getDetailUseCase = get(),
            deleteUseCase = get()
        )
    }
    viewModelOf(::DashboardViewModel)
}
