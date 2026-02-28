package com.app.padams.di

import android.content.Context
import com.app.padams.ml.FaceClusterEngine
import com.app.padams.ml.FaceDetectorWrapper
import com.app.padams.ml.FaceEmbeddingExtractor
import com.app.padams.ml.FaceScanManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MlModule {

    @Provides
    @Singleton
    fun provideFaceDetectorWrapper(): FaceDetectorWrapper = FaceDetectorWrapper()

    @Provides
    @Singleton
    fun provideFaceEmbeddingExtractor(
        @ApplicationContext context: Context
    ): FaceEmbeddingExtractor = FaceEmbeddingExtractor(context)

    @Provides
    @Singleton
    fun provideFaceClusterEngine(): FaceClusterEngine = FaceClusterEngine()

    @Provides
    @Singleton
    fun provideFaceScanManager(
        @ApplicationContext context: Context
    ): FaceScanManager = FaceScanManager(context)
}
