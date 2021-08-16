package com.niran.newsapplication.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.databinding.FragmentBreakingNewsBinding
import com.niran.newsapplication.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.niran.newsapplication.utils.Resource
import com.niran.newsapplication.utils.adapters.ArticleAdapter
import com.niran.newsapplication.utils.adapters.shouldPaginate
import com.niran.newsapplication.utils.newsViewModel
import com.niran.newsapplication.viewmodels.NewsViewModel

class BreakingNewsFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBreakingNewsBinding.inflate(inflater)

        viewModel = newsViewModel()

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

            rvBreakingNews.apply {
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
                            viewModel.getBreakingNews("us")
                            isScrolling = false
                        }
                    }
                })
            }

            viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let { newsResponse ->
                            articleAdapter.submitList(newsResponse.articles.toList())
                            val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                            isLastPage = totalPages == viewModel.breakingNewsPage
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { errorMessage ->
                            Log.e(TAG, "Error with response: $errorMessage")
                        }
                    }
                }
            }
        }
    }

    private fun hideProgressBar() = binding.apply {
        pbPagination.visibility = View.GONE
        isLoading = false

    }

    private fun showProgressBar() = binding.apply {
        pbPagination.visibility = View.VISIBLE
        isLoading = true
    }

    private fun navigateToArticleFragment(article: Article) = view?.findNavController()?.navigate(
        BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "BreakingNewsFragment"
    }
}