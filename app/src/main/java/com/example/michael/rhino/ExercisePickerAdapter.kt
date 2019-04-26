package com.example.michael.rhino

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExercisePickerAdapter(private val exercises: MutableList<String>): RecyclerView.Adapter<ExercisePickerAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context).apply {
            textSize = 20f
            isClickable = true
            isFocusable = true
            setOnClickListener {exerciseSelected(it)}
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = exercises[position]
    }

    override fun getItemCount() = exercises.size

    private fun exerciseSelected(view: View) {
        val textView = view as TextView
        val intent = Intent(view.context, MainActivity::class.java)
        intent.putExtra(EXERCISE_NAME, textView.text.toString())
        view.context.startActivity(intent)
    }



}