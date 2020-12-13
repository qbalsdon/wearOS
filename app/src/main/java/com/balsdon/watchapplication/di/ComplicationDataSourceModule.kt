package com.balsdon.watchapplication.di

import android.content.Context
import com.balsdon.watchfacerenderer.WatchComplicationDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class)
object ComplicationDataSourceModule {
    @Provides
    fun provideWatchComplicationDataSource(@ApplicationContext context: Context): WatchComplicationDataSource
            = ExampleComplicationDataSource(context)
}