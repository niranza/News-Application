package com.niran.newsapplication.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.niran.newsapplication.R
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.databinding.FragmentBreakingNewsBinding
import com.niran.newsapplication.utils.Constants
import com.niran.newsapplication.utils.Constants.Companion.DEFAULT_COUNTRY
import com.niran.newsapplication.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.niran.newsapplication.utils.FragmentUtil.Companion.newsViewModel
import com.niran.newsapplication.utils.FragmentUtil.Companion.showInternetConnectionError
import com.niran.newsapplication.utils.Resource
import com.niran.newsapplication.utils.SharedPrefUtil.Companion.getSharedPrefString
import com.niran.newsapplication.utils.SharedPrefUtil.Companion.setSharedPrefString
import com.niran.newsapplication.utils.adapters.ArticleAdapter
import com.niran.newsapplication.utils.adapters.shouldPaginate
import com.niran.newsapplication.viewmodels.NewsViewModel

class BreakingNewsFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private var currentCountryCode = DEFAULT_COUNTRY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBreakingNewsBinding.inflate(inflater)

        viewModel = newsViewModel()

        setHasOptionsMenu(true)

        loadCountryCode()

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
                            viewModel.getBreakingNews(currentCountryCode)
                            isScrolling = false
                        }
                    }
                })
            }

            viewModel.eventLoadBreakingNews.observe(viewLifecycleOwner) { load ->
                load?.let { if (it) viewModel.getBreakingNews(currentCountryCode) }
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
                            when (errorMessage) {
                                Constants.NO_INTERNET_CONNECTION_ERROR ->
                                    showInternetConnectionError(rvBreakingNews)
                                else -> Log.e(TAG, "Error with response: $errorMessage")
                            }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.breaking_news_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.item_choose_country -> {
            showCountryDialog()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showCountryDialog() = with(AlertDialog.Builder(requireContext())) {
        val countries = resources.getStringArray(R.array.countries)
        setTitle(R.string.choose_country_dialog_title)
        setItems(countries) { _, which ->
            when (countries[which]) {
                "Israel" -> saveCountryCode("il")
                "U.S" -> saveCountryCode("us")
                else -> return@setItems
            }
            viewModel.refreshBreakingNews(currentCountryCode)
        }
        show()
    }

    private fun saveCountryCode(countryCode: String) = requireContext().setSharedPrefString(
        getString(R.string.breaking_news_pref_file_key),
        getString(R.string.country_key),
        countryCode
    ).also { currentCountryCode = countryCode }

    private fun loadCountryCode() = (requireContext().getSharedPrefString(
        getString(R.string.breaking_news_pref_file_key),
        getString(R.string.country_key),
        DEFAULT_COUNTRY
    )).also { currentCountryCode = it }

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