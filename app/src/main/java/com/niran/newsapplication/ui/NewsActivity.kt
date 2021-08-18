package com.niran.newsapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.niran.newsapplication.NewsApplication
import com.niran.newsapplication.R
import com.niran.newsapplication.databinding.ActivityMainBinding
import com.niran.newsapplication.viewmodels.NewsViewModel
import com.niran.newsapplication.viewmodels.NewsViewModelFactory

class NewsActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel

    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = (application as NewsApplication).newsRepository
        val factory = NewsViewModelFactory(newsRepository, application)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        controller = navHostFragment.navController

        setupActionBarWithNavController(controller)

        binding.apply {
            bottomNavigationView.setupWithNavController(controller)
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        controller.navigateUp() || super.onSupportNavigateUp()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}