package com.example.michael.rhino

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BarbellDrawView(context: Context, attrs: AttributeSet) : View(context, attrs){
    var weight45: Int = 0
    var weight35: Int = 0
    var weight25: Int = 0
    var weight10: Int = 0
    var weight5:  Int = 0
    var weight2:  Int = 0
    val barWidth: Float = 500f
    val barInnerWidth: Float = barWidth * 0.8f
    val barHeight: Float = 15f
    var textHeight = 40f;

    private val piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = textHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            val centerx = width / 2f
            val centery = height / 2f
            drawRect(centerx - barWidth/2, centery - barHeight/2, centerx + barWidth/2, centery + barHeight/2, piePaint)

            var x = centerx - barInnerWidth/2
            val counts = listOf(weight45, weight35, weight25, weight10, weight5, weight2)
            val heights = listOf(150f, 140f, 120f, 100f, 60f, 50f)
            val widths = listOf(20f, 20f, 20f, 15f, 10f, 10f)
            for (i in 0 until counts.size) {
                val count = counts[i]
                val plateHeight = heights[i]
                val plateWidth = widths[i]
                for (i in 1 .. count) {
                    drawRect(x - plateWidth, centery - plateHeight/2, x, centery + plateHeight/2, piePaint)
                    val reflectx = (x - centerx) * -1 + centerx
                    drawRect(reflectx, centery - plateHeight/2, reflectx + plateWidth, centery + plateHeight/2, piePaint)
                    x -= plateWidth
                }
            }
            val weight = 45 + (45 * weight45 + 35 * weight35 + 25 * weight25 + 10 * weight10 + 5 * weight5 + 2.5 * weight2) * 2
            val weightStr = weight.toInt().toString() + " lb"
            drawText(weightStr, centerx - piePaint.measureText(weightStr)/2, centery + 100, piePaint)

        }
    }
}