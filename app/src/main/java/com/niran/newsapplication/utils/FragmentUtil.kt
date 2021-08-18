package com.niran.newsapplication.utils

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.niran.newsapplication.R
import com.niran.newsapplication.ui.NewsActivity
import com.niran.newsapplication.viewmodels.NewsViewModel

class FragmentUtil {
    companion object {
        //Will throw an error if this can't cast to NewsActivity
        fun Fragment.newsViewModel(): NewsViewModel = (activity as NewsActivity).viewModel

        fun Fragment.showInternetConnectionError(vararg views: View) = view?.apply {
            try {
                findViewById<ImageView>(R.id.iv_no_internet).visibility = View.VISIBLE
                views.forEach { view -> view.visibility = View.GONE }
            } catch (e: Exception) {
                throw IllegalArgumentException("Fragment ${this::class.java.name} doesn't have iv_no_internet")
            }
        }
    }
}
