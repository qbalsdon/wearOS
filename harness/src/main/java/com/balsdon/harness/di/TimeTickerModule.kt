package com.balsdon.harness.di

import android.content.Context
import com.balsdon.harness.ui.activity.CoroutineTimerTicker
import com.balsdon.harness.ui.activity.TimeTicker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.MainScope


@Module
@InstallIn(ActivityComponent::class)
object TimeTickerModule {
    @Provides
    fun provideTimeTicker(): TimeTicker
            = CoroutineTimerTicker(MainScope())
}