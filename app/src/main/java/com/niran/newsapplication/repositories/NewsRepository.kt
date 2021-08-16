package com.niran.newsapplication.repositories

import com.niran.newsapplication.data.api.NewsApi
import com.niran.newsapplication.data.database.Daos.ArticleDao
import com.niran.newsapplication.data.models.Article

class NewsRepository(private val articleDao: ArticleDao, private val newsApi: NewsApi) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        newsApi.searchForNews(searchQuery, pageNumber)

    val savedArticlesWithFlow = articleDao.getAllArticlesWithFlow()

    suspend fun insertArticle(article: Article) = articleDao.insertArticle(article)

    suspend fun deleteArticle(article: Article) = articleDao.deleteArticle(article)
}