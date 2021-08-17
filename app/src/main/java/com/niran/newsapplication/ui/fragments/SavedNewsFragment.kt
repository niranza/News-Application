package com.niran.newsapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.niran.newsapplication.R
import com.niran.newsapplication.data.models.Article
import com.niran.newsapplication.databinding.FragmentSavedNewsBinding
import com.niran.newsapplication.utils.adapters.ArticleAdapter
import com.niran.newsapplication.utils.newsViewModel
import com.niran.newsapplication.viewmodels.NewsViewModel


class SavedNewsFragment : Fragment() {

    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSavedNewsBinding.inflate(inflater)

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

            rvSavedNews.adapter = articleAdapter

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    articleAdapter.currentList[viewHolder.adapterPosition].also { article ->
                        viewModel.deleteArticle(article)
                        showSnackBar(article)
                    }
                }
            }).apply { attachToRecyclerView(rvSavedNews) }

            viewModel.savedArticlesAsLiveData.observe(viewLifecycleOwner) { articles ->
                articles?.let {
                    articleAdapter.submitList(it)
                    if (it.isEmpty()) showNoSavedNews() else hideNoSavedNews()
                }
            }
        }
    }

    private fun showNoSavedNews() = binding.apply { tvNoSavedNews.visibility = View.VISIBLE }

    private fun hideNoSavedNews() = binding.apply { tvNoSavedNews.visibility = View.GONE }

    private fun showSnackBar(article: Article) =
        Snackbar.make(binding.root, getString(R.string.article_deleted), Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { viewModel.insertArticle(article) }
            .show()

    private fun navigateToArticleFragment(article: Article) = view?.findNavController()
        ?.navigate(SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(article))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}