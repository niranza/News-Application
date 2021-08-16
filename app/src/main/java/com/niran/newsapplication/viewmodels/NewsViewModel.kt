package com.niran.newsapplication.viewmodels

import androidx.lifecycle.*
import com.niran.newsapplication.data.models.NewsResponse
import com.niran.newsapplication.repositories.NewsRepository
import com.niran.newsapplication.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _breakingNews = MutableLiveData<Resource<NewsResponse>>()
    val breakingNews: LiveData<Resource<NewsResponse>> get() = _breakingNews
    private val breakingNewsPage = 1

    init {
        getBreakingNews("il")
    }

    private fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        _breakingNews.postValue(Resource.Loading())
        val response = repository.getBreakingNews(countryCode, breakingNewsPage)
        _breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(
        response: Response<NewsResponse>
    ): Resource<NewsResponse> = with(response) {
        if (isSuccessful) {
            body()?.let { result -> return Resource.Success(result) }
        }
        Resource.Error(message())
    }

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