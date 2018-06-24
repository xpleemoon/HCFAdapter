package com.xpleemoon.view.rv.sample.hcfadapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xpleemoon.view.rv.HCFAdapter
import com.xpleemoon.view.rv.ViewHolder

/**
 * @author xpleemoon
 */
class SampleAdapter : HCFAdapter<ViewHolder>() {
    private val headers = MutableList(2) { "header$it" }
    private val footers = MutableList(2) { "footer$it" }
    private val contents = MutableList(20) { "content$it" }

    override fun getHeaderItemCount(): Int = headers?.size

    override fun getFooterItemCount(): Int = footers?.size

    override fun getContentItemCount(): Int = contents?.size

    override fun onCreateHeaderItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return HeaderViewHolder(itemView)
    }

    override fun onCreateFooterItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return FooterViewHolder(itemView)
    }

    override fun onCreateContentItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ContentViewHolder(itemView)
    }

    override fun onBindHeaderItemViewHolder(holder: ViewHolder, position: Int) {
        holder.init(position)
    }

    override fun onBindFooterItemViewHolder(holder: ViewHolder, position: Int) {
        holder.init(position)
    }

    override fun onBindContentItemViewHolder(holder: ViewHolder, position: Int) {
        holder.init(position)
    }

    private inner class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {

        override fun init(position: Int) {
            itemView.takeIf { it is TextView }?.let {
                it as TextView
                it.text = headers[position]
                it.setBackgroundColor(Color.DKGRAY)
            }
        }
    }

    private inner class FooterViewHolder(itemView: View) : ViewHolder(itemView) {

        override fun init(position: Int) {
            itemView.takeIf { it is TextView }?.let {
                it as TextView
                it.text = footers[position]
                it.setBackgroundColor(Color.GRAY)
            }
        }
    }

    private inner class ContentViewHolder(itemView: View) : ViewHolder(itemView) {

        override fun init(position: Int) {
            itemView.takeIf { it is TextView }?.let {
                it as TextView
                it.text = contents[position]
            }
        }
    }
}