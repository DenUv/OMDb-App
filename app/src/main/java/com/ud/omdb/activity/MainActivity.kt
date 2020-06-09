package com.ud.omdb.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ud.omdb.BuildConfig
import com.ud.omdb.R
import com.ud.omdb.databinding.ActivityMainBinding
import com.ud.omdb.fragment.MovieDetailsFragment
import com.ud.omdb.fragment.SearchFragment
import com.ud.omdb.listener.OnItemTouchListener
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.model.SearchResult
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnItemTouchListener {

    private lateinit var searchService: SearchService

    private lateinit var binding: ActivityMainBinding

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitle: TextView

    private lateinit var searchFragment: Fragment
    private lateinit var movieDetailsFragment: Fragment


    private val searchFragmentTag: String = "search_frag_tag"
    private val movieDetailsFragmentTag: String = "movie_details_frag_tag"

    //pagination
    var currentPage: Int = 1
    var maxPages: Int = currentPage
    val pageSize: Int = 10
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDataBinding()
        initServices()
        initFragments()
        initToolbar()
        showFragment(searchFragment, searchFragmentTag, R.string.search_frag_toolbar_title)
    }

    private fun initServices() {
        searchService = NetworkClient(this).createService(SearchService::class.java)
    }

    private fun initFragments() {
        searchFragment = SearchFragment()
        movieDetailsFragment = MovieDetailsFragment()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        fragmentContainer = binding.fragmentContainer
        toolbar = binding.toolbar
        toolbarTitle = binding.tvToolbarTitle
    }


    suspend fun loadMoviesList(searchedTitle: String): SearchResult {
        return withContext(Dispatchers.IO) {
            searchService.search(
                BuildConfig.API_KEY, searchedTitle, currentPage
            )
        }
    }

    suspend fun loadMovieDetails(id: String): MovieDetails {
        return withContext(Dispatchers.IO) {
            searchService.loadMovieDetails(BuildConfig.API_KEY, id)
        }
    }

    private fun showFragment(
        fragmentToAdd: Fragment,
        fragmentTag: String,
        toolBarTitleId: Int
    ) {
        for (attachedFragment: Fragment in supportFragmentManager.fragments) {
            if (attachedFragment.isVisible) {
                supportFragmentManager.beginTransaction().hide(attachedFragment).commit()
            }
        }
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (existingFragment != null) {
            supportFragmentManager.beginTransaction()
                .show(existingFragment).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(fragmentContainer.id, fragmentToAdd, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit()
        }
        toolbar.tv_toolbar_title.text = getString(toolBarTitleId)
    }

    override fun onItemTouchListener(id: String) {
        val bundle = Bundle()
        bundle.putString("id", id)

        movieDetailsFragment.arguments = bundle
        showFragment(
            movieDetailsFragment,
            movieDetailsFragmentTag,
            R.string.movie_details_frag_toolbar_title
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish();
        } else {
            showFragment(searchFragment, searchFragmentTag, R.string.search_frag_toolbar_title)
            super.onBackPressed();
        }

    }
}


