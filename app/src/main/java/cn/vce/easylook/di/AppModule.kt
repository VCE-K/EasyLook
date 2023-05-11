package cn.vce.easylook.di

import android.content.Context
import cn.vce.easylook.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.data.remote.MusicDatabase
import cn.vce.easylook.feature_music.exoplayer.MusicServiceConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@InstallIn 注解可以与 Hilt 的 @Module 注解一起使用，用于声明要提供的依赖项，并指定模块要安装到的注入器组件.安装在@HiltAndroidApp上面
@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton//成为跨服务实例共享的单例
    @Provides
    fun provideMusicDatabase() = MusicDatabase()


    //Music
    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ) = MusicServiceConnection(context)

    @Singleton
    @Provides
    fun provideSwipeSongAdapter() = SwipeSongAdapter()

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )
}