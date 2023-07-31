package cn.vce.easylook.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import cn.vce.easylook.R
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.db.MusicDatabase
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_1_2
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_2_3
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_3_4
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_4_5
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_5_6
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_6_7
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_7_8
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_8_9
import cn.vce.easylook.feature_music.db.MusicDatabase.Companion.MIGRATION_9_10
import cn.vce.easylook.feature_music.exoplayer.MusicServiceConnection
import cn.vce.easylook.feature_music.exoplayer.MusicSource
import cn.vce.easylook.feature_music.repository.MusicRepository
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@InstallIn 注解可以与 Hilt 的 @Module 注解一起使用，用于声明要提供的依赖项，并指定模块要安装到的注入器组件.安装在@HiltAndroidApp上面
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(
        @ApplicationContext context: Context
    ) = context


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

    //Music所用
    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context,
        musicRepository: MusicRepository,
        musicSource: MusicSource
    ) = MusicServiceConnection(context, musicRepository, musicSource)

    @Singleton
    @Provides
    fun provideSwipeSongAdapter() = SwipeSongAdapter()


    @Provides//@Provides 注解来声明提供对象的方法
    @Singleton//@Singleton 注解来定义用于提供单例对象的方法。
    fun provideMusicDatabase(app: Application): MusicDatabase {
        return Room.databaseBuilder(
            app,
            MusicDatabase::class.java,
            MusicDatabase.DATABASE_NAME
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6,
            MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
            .createFromAsset("database/MUSICS_DB.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: MusicDatabase): MusicRepository {
        return MusicRepository(db)
    }

    @Provides
    @Singleton
    fun provideMusicSource(repository: MusicRepository): MusicSource {
        return MusicSource(repository)
    }


}