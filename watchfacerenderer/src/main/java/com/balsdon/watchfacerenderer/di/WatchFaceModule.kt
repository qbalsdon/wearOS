package com.balsdon.watchfacerenderer.di

import android.content.Context
import com.balsdon.watchfacerenderer.WatchFaceRenderer
import com.balsdon.watchfacerenderer.example.ExampleWatchRenderer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class, ViewComponent::class)
object WatchFaceModule {
    @Provides
    fun provideWatchFaceRenderer(@ApplicationContext context: Context): WatchFaceRenderer
            = ExampleWatchRenderer(context.resources)
}