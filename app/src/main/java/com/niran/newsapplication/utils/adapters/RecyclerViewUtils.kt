package com.niran.newsapplication.utils.adapters

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.shouldPaginate(isLoading: Boolean, isLastPage: Boolean, isScrolling: Boolean) =
    run {
        val layoutManager = layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount

        val isAtLastItem =
            firstVisibleItemPosition + visibleItemCount >= totalItemCount
        val isNotAtBegging = firstVisibleItemPosition >= 0

        !isLoading && !isLastPage && isAtLastItem && isNotAtBegging && isScrolling
    }