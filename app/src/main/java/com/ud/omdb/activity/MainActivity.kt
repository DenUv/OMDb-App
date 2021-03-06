package com.ud.omdb.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ud.omdb.R
import com.ud.omdb.databinding.ActivityMainBinding
import com.ud.omdb.fragment.MovieDetailsFragment
import com.ud.omdb.fragment.SearchFragment
import com.ud.omdb.listener.OnItemTouchListener
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(), OnItemTouchListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitle: TextView

    private lateinit var searchFragment: Fragment
    private lateinit var movieDetailsFragment: Fragment

    private val searchFragmentTag: String = "search_frag_tag"
    private val movieDetailsFragmentTag: String = "movie_details_frag_tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDataBinding()
        initFragments()
        initToolbar()
        showFragment(searchFragment, searchFragmentTag, R.string.search_frag_toolbar_title)
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


