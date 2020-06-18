package com.ud.omdb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.ud.omdb.R
import com.ud.omdb.databinding.FragmentMovieDetailsBinding
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.viewmodel.MovieDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MovieDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsBinding

    private lateinit var viewModel: MovieDetailsViewModel

    private lateinit var movieTitle: TextView
    private lateinit var poster: ImageView
    private lateinit var errorMessage: TextView

    private lateinit var movieId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            movieId = arguments!!.getString("id", "mock")
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViewModel()
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
        movieTitle = binding.tvMovieTitle
        poster = binding.ivPoster
        errorMessage = binding.tvErrorMessage
    }

    private fun initViewModel() {
        viewModel = MovieDetailsViewModel(requireActivity().application)
    }

    private fun loadMovieDetails() {
        viewModel.viewModelScope.launch {
            hideErrorMessage()
            var movieDetails: MovieDetails? = null
            try {
                movieDetails = viewModel.loadMovieDetails(movieId)
            } catch (exp: Exception) {
                showErrorMessage(exp.localizedMessage)
                return@launch
            }
            binding.movie = movieDetails
            if (movieDetails.poster != "N/A") {
                viewModel.loadPoster(movieDetails.poster, poster)
            }
        }
    }

    private fun showErrorMessage(error: String?) {
        errorMessage.visibility = View.VISIBLE
        errorMessage.text = error
    }

    private fun hideErrorMessage() {
        errorMessage.text = null
        errorMessage.visibility = View.GONE
    }

}
