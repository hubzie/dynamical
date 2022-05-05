package com.example.dynamical.routelist

import android.content.Context
import android.util.TypedValue
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import com.example.dynamical.R

class RouteDetailsItemFactory(private val context: Context) {
    fun produce(value: String): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size))
            text = value
        }
    }
}