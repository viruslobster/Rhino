package com.example.michael.rhino

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.max

class WeightButton(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    var count: Int = 0
    var onChange: () -> Unit = {}

    init {
        inflate(context, R.layout.weight_button, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.WeightButton)
        val text = attributes.getString(R.styleable.WeightButton_weightText)
        val textView = findViewById<TextView>(R.id.textView3)
        val incrementButton = findViewById<Button>(R.id.button5)
        incrementButton.setOnClickListener { incrementCount(it) }

        val decrementButton = findViewById<Button>(R.id.button3)
        decrementButton.setOnClickListener { decrementCount(it) }
        textView.text = text
        attributes.recycle()
    }

    private fun incrementCount(view: View) {
        count++
        onChange()
    }

    private fun decrementCount(view: View) {
        count = max(0, count-1)
        onChange()
    }

}