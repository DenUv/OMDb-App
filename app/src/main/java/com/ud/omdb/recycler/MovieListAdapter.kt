package com.ud.omdb.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ud.omdb.R
import com.ud.omdb.activity.MainActivity
import com.ud.omdb.databinding.MovieListItemBinding
import com.ud.omdb.model.MovieDetails

class MovieListAdapter(
    private val parentActivity:
    Activity
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1

    private val movieList: MutableList<MovieDetails> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.progress_bar, parent, false)
                ProgressBarViewHolder(itemView)
            }
            else -> {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.movie_list_item, parent, false)
                MovieViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if ((parentActivity as MainActivity).isLoading) {
            if (position == movieList.size - 1) {
                VIEW_TYPE_LOADING
            } else {
                VIEW_TYPE_NORMAL
            }
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    fun showLoader() {
        movieList.add(
            MovieDetails(
                "mock",
                "mock",
                "mock",
                "mock",
                "mock",
                "mock",
                true,
                "mock",
                "mock"
            )
        )
        notifyItemInserted(movieList.size - 1)
    }

    fun hideLoader() {
        val position: Int = movieList.size - 1
        movieList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        movieList.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun addItems(data: List<MovieDetails>) {
        movieList.addAll(data)
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val binding: MovieListItemBinding = DataBindingUtil.bind(itemView)!!

        override fun onBind(position: Int) {
            super.onBind(position)
            binding.movie = movieList[position]
        }

        override fun clear() {}
    }

    inner class ProgressBarViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun clear() {
        }
    }
}