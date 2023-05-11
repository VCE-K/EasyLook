package cn.vce.easylook.feature_music.di

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import cn.vce.easylook.feature_music.data.remote.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)//表示在服务组件当中运行
object ServiceModule {

    /*@ServiceScoped//成为跨服务实例共享的单例
    @Provides
    fun provideMusicDatabase() = MusicDatabase()
*/
    //该实例定义音频属性，如内容类型和用途，以便 ExoPlayer 可以根据这些属性来处理播放和控制媒体。
    @ServiceScoped
    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)//表示内容用于音乐播放
        .setUsage(C.USAGE_MEDIA)//表示音频用于媒体播放
        .build()

    //提供 SimpleExoPlayer 实例，它是一个轻量级的 ExoPlayer 实现，
    // 可用于播放本地或远程音频、视频文件和流。预先设置了音频属性、处理耳机断开、处理焦点变化并启用播放器内部自动管理缓存的实例。
    @ServiceScoped
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ) = SimpleExoPlayer.Builder(context).build().apply {
        setAudioAttributes(audioAttributes, true)
        setHandleAudioBecomingNoisy(true)
    }

    @ServiceScoped
    @Provides
    fun provideDataSourceFactory(
        @ApplicationContext context: Context
    ) = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Spotify App"))
}












