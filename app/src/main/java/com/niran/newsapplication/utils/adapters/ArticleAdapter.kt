package com.niran.newsapplication.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.databinding.ArticleItemBinding

class ArticleAdapter(
    private val handler: ArticleClickHandler
) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleCallBack) {

    class ArticleViewHolder private constructor(
        private val binding: ArticleItemBinding,
        private val handler: ArticleClickHandler
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) = binding.apply {
            Glide.with(root).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt

            itemView.setOnClickListener { handler.onItemClick(article) }
        }

        companion object {
            fun create(parent: ViewGroup, handler: ArticleClickHandler): ArticleViewHolder {
                val binding = ArticleItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ArticleViewHolder(binding, handler)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.create(parent, handler)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object ArticleCallBack : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return newItem.articleId == oldItem.articleId
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return newItem == oldItem
        }
    }

    interface ArticleClickHandler {
        fun onItemClick(article: Article)
    }
}