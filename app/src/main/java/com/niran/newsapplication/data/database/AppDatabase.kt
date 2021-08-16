package com.niran.newsapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.niran.newsapplication.data.database.Daos.ArticleDao
import com.niran.newsapplication.data.database.converters.SourceConverter
import com.niran.newsapplication.data.models.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(SourceConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val articleDao: ArticleDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build().also { INSTANCE = it }
            }
    }
}