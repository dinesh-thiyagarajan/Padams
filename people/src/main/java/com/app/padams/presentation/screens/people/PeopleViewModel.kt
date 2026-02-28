package com.app.padams.presentation.screens.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.app.padams.domain.model.FaceGroup
import com.app.padams.domain.usecase.face.GetFaceGroupsUseCase
import com.app.padams.ml.FaceScanManager
import com.app.padams.ml.FaceScanWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScanProgress(
    val isScanning: Boolean = false,
    val progress: Int = 0,
    val total: Int = 0
)

@HiltViewModel
class PeopleViewModel @Inject constructor(
    getFaceGroupsUseCase: GetFaceGroupsUseCase,
    private val faceScanManager: FaceScanManager
) : ViewModel() {

    val faceGroups: StateFlow<List<FaceGroup>> = getFaceGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _scanProgress = MutableStateFlow(ScanProgress())
    val scanProgress: StateFlow<ScanProgress> = _scanProgress.asStateFlow()

    init {
        observeScanProgress()
    }

    fun startFaceScan() {
        faceScanManager.startScan()
    }

    private fun observeScanProgress() {
        viewModelScope.launch {
            faceScanManager.observeProgress().collect { workInfo ->
                if (workInfo != null) {
                    val isRunning = workInfo.state == WorkInfo.State.RUNNING
                    val progress = workInfo.progress.getInt(FaceScanWorker.KEY_PROGRESS, 0)
                    val total = workInfo.progress.getInt(FaceScanWorker.KEY_TOTAL, 0)
                    _scanProgress.value = ScanProgress(
                        isScanning = isRunning,
                        progress = progress,
                        total = total
                    )
                }
            }
        }
    }
}
