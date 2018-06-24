package com.xpleemoon.view.rv

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun init(position: Int)
}