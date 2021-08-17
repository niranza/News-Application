package com.niran.newsapplication.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.data.models.NewsResponse
import com.niran.newsapplication.repositories.NewsRepository
import com.niran.newsapplication.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _breakingNews = MutableLiveData<Resource<NewsResponse>>()
    val breakingNews: LiveData<Resource<NewsResponse>> get() = _breakingNews
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    private val _searchNews = MutableLiveData<Resource<NewsResponse>>()
    val searchNews: LiveData<Resource<NewsResponse>> get() = _searchNews
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    private val _eventLoadBreakingNews = MutableLiveData(false)
    val eventLoadBreakingNews: LiveData<Boolean> get() = _eventLoadBreakingNews

    init {
        _eventLoadBreakingNews.value = true
    }

    fun refreshBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNewsPage = 1
        getBreakingNews(countryCode)
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        _breakingNews.postValue(Resource.Loading())
        val response = repository.getBreakingNews(countryCode, breakingNewsPage)
        _breakingNews.postValue(handleBreakingNewsResponse(response))
        _eventLoadBreakingNews.value = false
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        _searchNews.postValue(Resource.Loading())
        val response = repository.searchNews(searchQuery, searchNewsPage)
        _searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(
        response: Response<NewsResponse>
    ): Resource<NewsResponse> = with(response) {
        if (isSuccessful) body()?.let { result ->
            Log.d("TAG", "")
            breakingNewsPage++
            if (breakingNewsResponse == null) breakingNewsResponse = result
            else breakingNewsResponse?.articles?.addAll(result.articles)
            return Resource.Success(breakingNewsResponse ?: result)
        }
        Resource.Error(message())
    }

    private fun handleSearchNewsResponse(
        response: Response<NewsResponse>
    ): Resource<NewsResponse> = with(response) {
        if (isSuccessful) body()?.let { result ->
            searchNewsPage++
            if (searchNewsResponse == null) searchNewsResponse = result
            else searchNewsResponse?.articles?.addAll(result.articles)
            return Resource.Success(searchNewsResponse ?: result)
        }
        Resource.Error(message())
    }

    fun insertArticle(article: Article) =
        viewModelScope.launch { repository.insertArticle(article) }

    fun deleteArticle(article: Article) =
        viewModelScope.launch { repository.deleteArticle(article) }

    val savedArticlesAsLiveData = repository.savedArticlesWithFlow.asLiveData()
}

class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}