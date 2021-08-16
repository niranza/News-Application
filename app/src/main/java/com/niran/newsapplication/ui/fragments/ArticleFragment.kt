package com.niran.newsapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.niran.newsapplication.R
import com.niran.newsapplication.databinding.FragmentArticleBinding
import com.niran.newsapplication.utils.newsViewModel
import com.niran.newsapplication.viewmodels.NewsViewModel


class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel

    private val args: ArticleFragmentArgs by navArgs()

    private val article get() = args.article

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleBinding.inflate(inflater)

        viewModel = newsViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            webView.apply {
                webViewClient = WebViewClient()
                loadUrl(article.url)
            }

            fabSave.setOnClickListener { viewModel.insertArticle(article); showSnackBar() }
        }
    }

    private fun showSnackBar() =
        Snackbar.make(binding.root, getString(R.string.article_saved), Snackbar.LENGTH_SHORT)
            .show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}