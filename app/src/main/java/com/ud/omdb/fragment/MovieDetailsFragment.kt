package com.ud.omdb.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil

import com.ud.omdb.R
import com.ud.omdb.activity.MainActivity
import com.ud.omdb.databinding.FragmentMovieDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MovieDetailsFragment : Fragment() {

    private lateinit var parentActivity: MainActivity

    private lateinit var binding: FragmentMovieDetailsBinding

    private lateinit var movieTitle: TextView
    private lateinit var movieId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity

        if (arguments != null) {
            movieId = arguments!!.getString("id", "mock")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDataBinding(inflater, container)

        loadMovieDetails()
        return binding.root
    }

    private fun initDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false)
        movieTitle = binding.tvMovieName
    }

    private fun loadMovieDetails() {
        CoroutineScope(Dispatchers.Main).launch {
            val movieDetails = parentActivity.loadMovieDetails(movieId)
            binding.movie = movieDetails
        }
    }

}