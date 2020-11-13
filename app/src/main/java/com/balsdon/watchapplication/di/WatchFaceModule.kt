package com.balsdon.watchapplication.di

import android.content.Context
import com.balsdon.watchapplication.ExampleWatchRenderer
import com.balsdon.watchapplication.watchfacerenderer.WatchFaceRenderer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object WatchFaceModule {
    @Provides
    fun provideWatchFaceRenderer(@ApplicationContext context: Context): WatchFaceRenderer
            = ExampleWatchRenderer(context.resources)
}