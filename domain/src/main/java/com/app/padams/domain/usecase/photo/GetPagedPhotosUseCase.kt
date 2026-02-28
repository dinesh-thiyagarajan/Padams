package com.app.padams.domain.usecase.photo

import androidx.paging.PagingData
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagedPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(): Flow<PagingData<Photo>> {
        return photoRepository.getPagedPhotos()
    }
}
