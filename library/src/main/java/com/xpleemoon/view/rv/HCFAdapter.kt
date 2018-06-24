package com.xpleemoon.view.rv

import android.annotation.SuppressLint
import android.support.annotation.IntRange
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.ViewGroup

private const val VIEW_TYPE_OFFSET = 1000L
private const val VIEW_TYPE_HEADER_START = 0
private const val VIEW_TYPE_FOOTER_START = 1000
private const val VIEW_TYPE_CONTENT_START = 2000

/**
 * 可设置header、footer、content的adapter
 * H:Header
 * C:Content
 * F:Footer
 * @author xpleemoon
 */
abstract class HCFAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private var oldHeaderItemCount: Int = 0
    private var oldContentItemCount: Int = 0
    private var oldFooterItemCount: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, @IntRange(from = 0L, to = VIEW_TYPE_OFFSET * 3 - 1) viewType: Int): VH =
            when (viewType) {
                in VIEW_TYPE_HEADER_START until (VIEW_TYPE_HEADER_START + VIEW_TYPE_OFFSET) ->
                    this.onCreateHeaderItemViewHolder(parent, viewType - VIEW_TYPE_HEADER_START)
                in VIEW_TYPE_FOOTER_START until (VIEW_TYPE_FOOTER_START + VIEW_TYPE_FOOTER_START) ->
                    this.onCreateFooterItemViewHolder(parent, viewType - VIEW_TYPE_FOOTER_START)
                in VIEW_TYPE_CONTENT_START until (VIEW_TYPE_CONTENT_START + VIEW_TYPE_OFFSET) ->
                    this.onCreateContentItemViewHolder(parent, viewType - VIEW_TYPE_CONTENT_START)
                else -> throw IllegalStateException("viewType $viewType is invalid")
            }

    override fun onBindViewHolder(holder: VH, position: Int) =
            when {
                isHeader(position) ->
                    this.onBindHeaderItemViewHolder(holder, position)
                isContent(position) ->
                    this.onBindContentItemViewHolder(holder, position - getHeaderItemCount())
                isFooter(position) ->
                    this.onBindFooterItemViewHolder(holder, position - getHeaderItemCount() - getContentItemCount())
                else -> throw IllegalStateException("position $position is invalid")
            }

    override fun getItemCount(): Int {
        oldHeaderItemCount = getHeaderItemCount()
        oldContentItemCount = getContentItemCount()
        oldFooterItemCount = getFooterItemCount()
        return oldHeaderItemCount + oldContentItemCount + oldFooterItemCount
    }

    @SuppressLint("Range")
    @IntRange(from = 0L, to = VIEW_TYPE_OFFSET * 3 - 1)
    override fun getItemViewType(position: Int): Int =
            when {
                isHeader(position) ->
                    this.validateViewType(this.getHeaderItemViewType(position)) + VIEW_TYPE_HEADER_START
                isContent(position) ->
                    this.validateViewType(this.getContentItemViewType(position - getHeaderItemCount())) + VIEW_TYPE_CONTENT_START
                isFooter(position) ->
                    this.validateViewType(this.getFooterItemViewType(position - getHeaderItemCount() - getContentItemCount())) + VIEW_TYPE_FOOTER_START
                else -> throw IllegalStateException("position $position is invalid")
            }


    fun isHeader(@IntRange(from = 0L) position: Int): Boolean =
            position >= 0 && position < getHeaderItemCount()

    fun isContent(@IntRange(from = 0L) position: Int): Boolean =
            position >= 0 && position in getHeaderItemCount() until getHeaderItemCount() + getContentItemCount()

    fun isFooter(@IntRange(from = 0L) position: Int): Boolean =
            position >= 0 && position >= getHeaderItemCount() + getContentItemCount()

    private fun validateViewType(@IntRange(from = 0L, to = VIEW_TYPE_OFFSET - 1) viewType: Int): Int =
            if (viewType in 0 until VIEW_TYPE_OFFSET) {
                viewType
            } else {
                throw IllegalStateException("viewType must be between 0 and ${VIEW_TYPE_OFFSET - 1}")
            }

    fun notifyHeaderItemInserted(position: Int) {
        val newHeaderItemCount = this.getHeaderItemCount()
        if (position in 0 until newHeaderItemCount) {
            this.notifyItemInserted(position)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for header items [0 - ${newHeaderItemCount - 1}].")
        }
    }

    fun notifyHeaderItemRangeInserted(positionStart: Int, itemCount: Int) {
        val newHeaderItemCount = this.getHeaderItemCount()
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= newHeaderItemCount) {
            this.notifyItemRangeInserted(positionStart, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for header items [0 - ${newHeaderItemCount - 1}].")
        }
    }

    fun notifyHeaderItemChanged(position: Int) {
        if (position in 0 until oldHeaderItemCount) {
            this.notifyItemChanged(position)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for header items [0 - ${oldHeaderItemCount - 1}].")
        }
    }

    fun notifyHeaderItemRangeChanged(positionStart: Int, itemCount: Int) {
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount < oldHeaderItemCount) {
            this.notifyItemRangeChanged(positionStart, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart  - ${positionStart + itemCount - 1}] is not within the position bounds for header items [0 - ${oldHeaderItemCount - 1}].")
        }
    }

    fun notifyHeaderItemMoved(fromPosition: Int, toPosition: Int) {
        if (toPosition >= fromPosition && fromPosition in 0 until oldHeaderItemCount && toPosition in 0 until oldHeaderItemCount) {
            this.notifyItemMoved(fromPosition, toPosition)
        } else {
            throw IndexOutOfBoundsException("The given fromPosition $fromPosition or toPosition $toPosition is not within the position bounds for header items [0 - ${oldHeaderItemCount - 1}].")
        }
    }

    fun notifyHeaderItemRemoved(position: Int) {
        if (position in 0 until oldHeaderItemCount) {
            this.notifyItemRemoved(position)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for header items [0 - ${oldHeaderItemCount - 1}].")
        }
    }

    fun notifyHeaderItemRangeRemoved(positionStart: Int, itemCount: Int) {
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= oldHeaderItemCount) {
            this.notifyItemRangeRemoved(positionStart, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for header items [0 - ${oldHeaderItemCount - 1}].")
        }
    }

    fun notifyContentItemInserted(position: Int) {
        val newHeaderItemCount = this.getHeaderItemCount()
        val newContentItemCount = this.getContentItemCount()
        if (position in 0 until newContentItemCount) {
            this.notifyItemInserted(position + newHeaderItemCount)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for content items [0 - ${newContentItemCount - 1}].")
        }
    }

    fun notifyContentItemRangeInserted(positionStart: Int, itemCount: Int) {
        val newHeaderItemCount = this.getHeaderItemCount()
        val newContentItemCount = this.getContentItemCount()
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= newContentItemCount) {
            this.notifyItemRangeInserted(positionStart + newHeaderItemCount, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for content items [0 - ${newContentItemCount - 1}].")
        }
    }

    fun notifyContentItemChanged(position: Int) {
        if (position in 0 until oldContentItemCount) {
            this.notifyItemChanged(position + oldHeaderItemCount)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for content items [0 - ${oldContentItemCount - 1}].")
        }
    }

    fun notifyContentItemRangeChanged(positionStart: Int, itemCount: Int) {
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= oldContentItemCount) {
            this.notifyItemRangeChanged(positionStart + oldHeaderItemCount, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for content items [0 - ${oldContentItemCount - 1}].")
        }
    }

    fun notifyContentItemMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition in 0 until oldContentItemCount && toPosition in 0 until oldContentItemCount) {
            this.notifyItemMoved(fromPosition + oldHeaderItemCount, toPosition + oldHeaderItemCount)
        } else {
            throw IndexOutOfBoundsException("The given fromPosition $fromPosition  or toPosition $toPosition is not within the position bounds for content items [0 - ${oldContentItemCount - 1}].")
        }
    }

    fun notifyContentItemRemoved(position: Int) {
        if (position in 0 until oldContentItemCount) {
            this.notifyItemRemoved(position + oldHeaderItemCount)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for content items [0 - ${oldContentItemCount - 1}].")
        }
    }

    fun notifyContentItemRangeRemoved(positionStart: Int, itemCount: Int) {
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= oldContentItemCount) {
            this.notifyItemRangeRemoved(positionStart + oldHeaderItemCount, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for content items [0 - ${oldContentItemCount - 1}].")
        }
    }

    fun notifyFooterItemInserted(position: Int) {
        val newHeaderItemCount = this.getHeaderItemCount()
        val newContentItemCount = this.getContentItemCount()
        val newFooterItemCount = this.getFooterItemCount()
        if (position in 0 until newFooterItemCount) {
            this.notifyItemInserted(position + newHeaderItemCount + newContentItemCount)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for footer items [0 - ${newFooterItemCount - 1}].")
        }
    }

    fun notifyFooterItemRangeInserted(positionStart: Int, itemCount: Int) {
        val newHeaderItemCount = this.getHeaderItemCount()
        val newContentItemCount = this.getContentItemCount()
        val newFooterItemCount = this.getFooterItemCount()
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= newFooterItemCount) {
            this.notifyItemRangeInserted(positionStart + newHeaderItemCount + newContentItemCount, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for footer items [0 - ${newFooterItemCount - 1}].")
        }
    }

    fun notifyFooterItemChanged(position: Int) {
        if (position in 0 until oldFooterItemCount) {
            this.notifyItemChanged(position + oldHeaderItemCount + oldContentItemCount)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for footer items [0 - ${oldFooterItemCount - 1}].")
        }
    }

    fun notifyFooterItemRangeChanged(positionStart: Int, itemCount: Int) {
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= oldFooterItemCount) {
            this.notifyItemRangeChanged(positionStart + oldHeaderItemCount + oldContentItemCount, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for footer items [0 - ${oldFooterItemCount - 1}].")
        }
    }

    fun notifyFooterItemMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition in 0 until oldFooterItemCount && toPosition in 0 until oldFooterItemCount) {
            this.notifyItemMoved(fromPosition + oldHeaderItemCount + oldContentItemCount, toPosition + oldHeaderItemCount + oldContentItemCount)
        } else {
            throw IndexOutOfBoundsException("The given fromPosition $fromPosition or toPosition $toPosition is not within the position bounds for footer items [0 - ${oldFooterItemCount - 1}].")
        }
    }

    fun notifyFooterItemRemoved(position: Int) {
        if (position in 0 until oldFooterItemCount) {
            this.notifyItemRemoved(position + oldHeaderItemCount + oldContentItemCount)
        } else {
            throw IndexOutOfBoundsException("The given position $position is not within the position bounds for footer items [0 - ${oldFooterItemCount - 1}].")
        }
    }

    fun notifyFooterItemRangeRemoved(positionStart: Int, itemCount: Int) {
        if (positionStart >= 0 && itemCount >= 0 && positionStart + itemCount <= oldFooterItemCount) {
            this.notifyItemRangeRemoved(positionStart + oldHeaderItemCount + oldContentItemCount, itemCount)
        } else {
            throw IndexOutOfBoundsException("The given range [$positionStart - ${positionStart + itemCount - 1}] is not within the position bounds for footer items [0 - ${oldFooterItemCount - 1}].")
        }
    }

    @IntRange(from = 0L, to = VIEW_TYPE_OFFSET - 1)
    open fun getHeaderItemViewType(position: Int): Int = 0

    @IntRange(from = 0L, to = VIEW_TYPE_OFFSET - 1)
    open fun getFooterItemViewType(position: Int): Int = 0

    @IntRange(from = 0L, to = VIEW_TYPE_OFFSET - 1)
    open fun getContentItemViewType(position: Int): Int = 0

    abstract fun getHeaderItemCount(): Int

    abstract fun getFooterItemCount(): Int

    abstract fun getContentItemCount(): Int

    abstract fun onCreateHeaderItemViewHolder(parent: ViewGroup, @IntRange(from = 0L, to = VIEW_TYPE_OFFSET - 1) viewType: Int): VH

    abstract fun onCreateFooterItemViewHolder(parent: ViewGroup, @IntRange(from = 0L, to = VIEW_TYPE_OFFSET - 1) viewType: Int): VH

    abstract fun onCreateContentItemViewHolder(parent: ViewGroup, @IntRange(from = 0L, to = VIEW_TYPE_OFFSET - 1) viewType: Int): VH

    abstract fun onBindHeaderItemViewHolder(holder: VH, position: Int)

    abstract fun onBindFooterItemViewHolder(holder: VH, position: Int)

    abstract fun onBindContentItemViewHolder(holder: VH, position: Int)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val oldSpanSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                        if (isHeader(position) || isFooter(position)) {
                            layoutManager.spanCount
                        } else {
                            oldSpanSizeLookup?.getSpanSize(position) ?: 1
                        }
            }
            layoutManager.spanCount = layoutManager.spanCount
        }
    }

    override fun onViewAttachedToWindow(holder: VH) {
        val position = holder.layoutPosition
        if (isHeader(position) || isFooter(position)) {
            val lp = holder.itemView.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
                lp.isFullSpan = true
            }
        }
    }
}