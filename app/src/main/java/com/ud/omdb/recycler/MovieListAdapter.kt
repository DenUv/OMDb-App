package com.ud.omdb.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ud.omdb.R
import com.ud.omdb.databinding.MovieListItemBinding
import com.ud.omdb.listener.OnItemTouchListener
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.viewmodel.SearchViewModel

class MovieListAdapter(
    private val viewModel: SearchViewModel,
    private val onTouchListener: OnItemTouchListener
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
        return if (viewModel.isLoading) {
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
    }

    fun hideLoader() {
        val item = movieList.find { it.id == "mock" }
        if (item != null) {
            val position = movieList.indexOf(item)
            movieList.removeAt(position)
            notifyItemRemoved(position)
        }
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
        notifyItemRangeChanged(movieList.size - 1, data.size)
    }

    inner class MovieViewHolder(itemView: View) : BaseViewHolder(itemView), View.OnClickListener {

        private val binding: MovieListItemBinding = DataBindingUtil.bind(itemView)!!

        init {
            itemView.setOnClickListener(this)
        }

        override fun onBind(position: Int) {
            super.onBind(position)
            binding.movie = movieList[position]
        }

        override fun clear() {}

        override fun onClick(v: View?) {
            val item = movieList[adapterPosition]
            onTouchListener.onItemTouchListener(item.id)
        }
    }

    inner class ProgressBarViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun clear() {
        }
    }
}