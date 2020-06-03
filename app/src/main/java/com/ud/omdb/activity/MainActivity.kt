package com.ud.omdb.activity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ud.omdb.BuildConfig
import com.ud.omdb.R
import com.ud.omdb.databinding.ActivityMainBinding
import com.ud.omdb.fragment.SearchFragment
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.model.SearchResult
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var searchService: SearchService

    private lateinit var binding: ActivityMainBinding

    private lateinit var fragmentContainer: FrameLayout

    private lateinit var searchFragment: Fragment

    private val searchFragmentTag: String = "search_frag_tag"
    private val detailsFragmentTag: String = "movie_details_frag_tag"

    //pagination. mb to move somewhere ?
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

        showFragment(searchFragment, searchFragmentTag)
    }

    private fun initServices() {
        searchService = NetworkClient(this).createService(SearchService::class.java)
    }

    private fun initFragments() {
        searchFragment = SearchFragment()
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        fragmentContainer = binding.fragmentContainer
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
        fragmentTag: String
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
                .add(fragmentContainer.id, fragmentToAdd, fragmentTag).commit()
        }
    }
}


