package dev.sandesh.kharcha.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sandesh.kharcha.core.model.UserPreferences
import dev.sandesh.kharcha.core.model.UserPreferencesRepository
import dev.sandesh.kharcha.feature.parser.rules.ParserEngine
import dev.sandesh.kharcha.feature.parser.rules.SbiParser
import dev.sandesh.kharcha.feature.parser.rules.BobParser
import dev.sandesh.kharcha.feature.parser.rules.LicParser
import dev.sandesh.kharcha.feature.parser.rules.GenericFallbackParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideParserEngine(): ParserEngine {
        return ParserEngine(
            parsers = listOf(
                SbiParser(),
                BobParser(),
                LicParser(),
                GenericFallbackParser()
            )
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return object : UserPreferencesRepository {
            private val dataStore = context.dataStore

            private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
            private val CLOUD_LLM_ENABLED = booleanPreferencesKey("cloud_llm_enabled")
            private val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")

            override val preferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
                UserPreferences(
                    isDarkMode = preferences[IS_DARK_MODE] ?: false,
                    cloudLlmEnabled = preferences[CLOUD_LLM_ENABLED] ?: false,
                    isOnboardingComplete = preferences[IS_ONBOARDING_COMPLETE] ?: false
                )
            }

            override suspend fun setDarkMode(isDark: Boolean) {
                dataStore.edit { preferences ->
                    preferences[IS_DARK_MODE] = isDark
                }
            }

            override suspend fun setCloudLlmEnabled(enabled: Boolean) {
                dataStore.edit { preferences ->
                    preferences[CLOUD_LLM_ENABLED] = enabled
                }
            }

            override suspend fun setOnboardingCompleted(completed: Boolean) {
                dataStore.edit { preferences ->
                    preferences[IS_ONBOARDING_COMPLETE] = completed
                }
            }
        }
    }
}
