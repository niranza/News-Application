package com.niran.newsapplication.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "article_table")
data class Article(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "article_id")
    val articleId: Long = 0L,
    val author: String = "",
    val content: String = "",
    val description: String = "",
    @ColumnInfo(name = "published_at")
    val publishedAt: String = "",
    val source: Source = Source(),
    val title: String = "",
    val url: String = "",
    @ColumnInfo(name = "url_to_image")
    val urlToImage: String = ""
) : Serializable