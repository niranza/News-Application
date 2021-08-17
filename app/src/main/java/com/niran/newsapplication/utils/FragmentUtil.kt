package com.niran.newsapplication.utils

import androidx.fragment.app.Fragment
import com.niran.newsapplication.ui.NewsActivity
import com.niran.newsapplication.viewmodels.NewsViewModel

class FragmentUtil {
    companion object {
        //Will throw an error if this can't cast to NewsActivity
        fun Fragment.newsViewModel(): NewsViewModel = (activity as NewsActivity).viewModel
    }
}
