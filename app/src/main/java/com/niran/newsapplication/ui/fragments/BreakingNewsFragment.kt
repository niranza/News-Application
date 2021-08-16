package com.niran.newsapplication.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.databinding.FragmentBreakingNewsBinding
import com.niran.newsapplication.utils.Resource
import com.niran.newsapplication.utils.adapters.ArticleAdapter
import com.niran.newsapplication.utils.newsViewModel
import com.niran.newsapplication.viewmodels.NewsViewModel

class BreakingNewsFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel

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

            rvBreakingNews.adapter = articleAdapter

            viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let { newsResponse ->
                            articleAdapter.submitList(newsResponse.articles)
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

    private fun hideProgressBar() {
        binding.pbPagination.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.pbPagination.visibility = View.VISIBLE
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