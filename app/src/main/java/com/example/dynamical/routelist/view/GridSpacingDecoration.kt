package com.example.dynamical.routelist.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingDecoration(private val columnOffset: Int, private val rowOffset: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        with(outRect) {
            left = columnOffset / 2
            right = columnOffset / 2
            top = rowOffset / 2
            bottom = rowOffset / 2
        }
    }
}