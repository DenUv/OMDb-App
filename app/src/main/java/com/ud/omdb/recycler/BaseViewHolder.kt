package com.ud.omdb.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var currentPosition: Int? = null
        private set

    protected abstract fun clear()

    open fun onBind(position: Int) {
        currentPosition = position
        clear()
    }

}