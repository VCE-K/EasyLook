package cn.vce.easylook.di

import cn.vce.easylook.feature_chatroom.MyWebSocketClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    /*@Singleton
    @Provides
    fun provideWebSocketClient() = MyWebSocketClient.getInstance()*/
}












