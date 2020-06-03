package com.ud.omdb.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private val pageSize = 10

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemsCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

        if ((visibleItemsCount + firstVisiblePosition) >= totalItemCount
            && firstVisiblePosition >= 0
            && totalItemCount >= pageSize
        ) {
            loadNextPage()
        }
    }

    abstract fun loadNextPage()

}