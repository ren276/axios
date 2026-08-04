package dev.sandesh.kharcha.feature.llm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sandesh.kharcha.feature.llm.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LlmModule {

    @Provides
    @Singleton
    fun provideLlmProvider(
        onDeviceProvider: OnDeviceGemmaProvider,
        // In a real implementation, read user's cloud preference from DataStore
        // and return GeminiFlashProvider or GrokProvider if enabled
    ): LlmProvider = onDeviceProvider // Default: on-device
}
