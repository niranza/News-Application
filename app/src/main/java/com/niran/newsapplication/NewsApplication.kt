package com.niran.newsapplication

import android.app.Application
import com.niran.newsapplication.data.api.RetrofitInstance
import com.niran.newsapplication.data.database.AppDatabase
import com.niran.newsapplication.repositories.NewsRepository

class NewsApplication : Application() {

    private val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    val newsRepository by lazy { NewsRepository(database.articleDao, RetrofitInstance.newsApi) }
}