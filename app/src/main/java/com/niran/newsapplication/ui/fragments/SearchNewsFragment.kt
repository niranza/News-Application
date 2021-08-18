package com.niran.newsapplication.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.niran.newsapplication.R
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.databinding.FragmentSearchNewsBinding
import com.niran.newsapplication.utils.Constants
import com.niran.newsapplication.utils.FragmentUtil.Companion.newsViewModel
import com.niran.newsapplication.utils.FragmentUtil.Companion.showInternetConnectionError
import com.niran.newsapplication.utils.Resource
import com.niran.newsapplication.utils.adapters.ArticleAdapter
import com.niran.newsapplication.utils.adapters.shouldPaginate
import com.niran.newsapplication.viewmodels.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private var currentSearchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchNewsBinding.inflate(inflater)

        viewModel = newsViewModel()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            val articleAdapter = ArticleAdapter(object : ArticleAdapter.ArticleClickHandler {
                override fun onItemClick(article: Article) {
                    navigateToArticleFragment(article)
                }
            })

            rvSearchNews.apply {
                adapter = articleAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                            isScrolling = true
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        if (recyclerView.shouldPaginate(isLoading, isLastPage, isScrolling)) {
                            viewModel.getSearchNews(currentSearchQuery)
                            isScrolling = false
                        }
                    }
                })
            }

            viewModel.searchNews.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let { newsResponse ->
                            articleAdapter.submitList(newsResponse.articles.toList())
                            val totalPages =
                                newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                            isLastPage = totalPages == viewModel.searchNewsPage
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { errorMessage ->
                            when (errorMessage) {
                                Constants.NO_INTERNET_CONNECTION_ERROR ->
                                    showInternetConnectionError(rvSearchNews)
                                else -> Log.e(TAG, "Error with response: $errorMessage")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.pbPagination.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.pbPagination.visibility = View.VISIBLE
        isLoading = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_news_menu, menu)

        (menu.findItem(R.id.item_search).actionView as SearchView).apply {
            queryHint = getString(R.string.search_hint)
            isSubmitButtonEnabled = true

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    this@apply.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    currentSearchQuery = newText ?: ""
                    searchWord(newText)
                    return true
                }
            })
        }
    }

    private var job: Job? = null
    private fun searchWord(searchQuery: String?) {
        job?.cancel()
        job = MainScope().launch {
            delay(Constants.SEARCH_NEWS_TIME_DELAY)
            searchQuery?.let { if (it.isNotBlank()) viewModel.getSearchNews(it) }
        }
    }

    private fun navigateToArticleFragment(article: Article) = view?.findNavController()
        ?.navigate(SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(article))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "SearchNewsFragment"
    }
}