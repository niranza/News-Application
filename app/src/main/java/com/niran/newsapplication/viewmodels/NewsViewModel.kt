package com.niran.newsapplication.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.niran.newsapplication.NewsApplication
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.data.models.NewsResponse
import com.niran.newsapplication.repositories.NewsRepository
import com.niran.newsapplication.utils.Constants.Companion.CONVERSION_ERROR
import com.niran.newsapplication.utils.Constants.Companion.NETWORK_FAILURE
import com.niran.newsapplication.utils.Constants.Companion.NO_INTERNET_CONNECTION_ERROR
import com.niran.newsapplication.utils.Constants.Companion.RESPONSE_BODY_ERROR
import com.niran.newsapplication.utils.InternetUtil.Companion.hasInternetConnection
import com.niran.newsapplication.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    private val repository: NewsRepository,
    app: Application
) : AndroidViewModel(app) {

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
        breakingNewsResponse = null
        getBreakingNews(countryCode)
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
        _eventLoadBreakingNews.value = false
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        _breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                _breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                _breakingNews.postValue(Resource.Error(NO_INTERNET_CONNECTION_ERROR))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _breakingNews.postValue(Resource.Error(NETWORK_FAILURE))
                else -> _breakingNews.postValue(Resource.Error(CONVERSION_ERROR))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        _searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.searchNews(searchQuery, searchNewsPage)
                _searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                _searchNews.postValue(Resource.Error(NO_INTERNET_CONNECTION_ERROR))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchNews.postValue(Resource.Error(NETWORK_FAILURE))
                else -> _searchNews.postValue(Resource.Error(CONVERSION_ERROR))
            }
        }
    }

    private fun handleBreakingNewsResponse(
        response: Response<NewsResponse>
    ): Resource<NewsResponse> = with(response) {
        if (isSuccessful) body()?.let { result ->
            breakingNewsPage++
            if (breakingNewsResponse == null) breakingNewsResponse = result
            else breakingNewsResponse?.articles?.addAll(result.articles)
            return Resource.Success(breakingNewsResponse ?: result)
        }
        val errorMessage = if (message().isEmpty()) RESPONSE_BODY_ERROR else message()
        Resource.Error(errorMessage)
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
        val errorMessage = if (message().isEmpty()) RESPONSE_BODY_ERROR else message()
        Resource.Error(errorMessage)
    }

    fun insertArticle(article: Article) =
        viewModelScope.launch { repository.insertArticle(article) }

    fun deleteArticle(article: Article) =
        viewModelScope.launch { repository.deleteArticle(article) }

    val savedArticlesAsLiveData = repository.savedArticlesWithFlow.asLiveData()

    private fun hasInternetConnection() = getApplication<NewsApplication>().hasInternetConnection()
}

class NewsViewModelFactory(
    private val repository: NewsRepository,
    private val app: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}