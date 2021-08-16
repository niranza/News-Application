package com.niran.newsapplication.data.database.Daos

import androidx.room.*
import com.niran.newsapplication.data.models.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article): Long

    @Query("SELECT * FROM article_table")
    fun getAllArticlesWithFlow(): Flow<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}