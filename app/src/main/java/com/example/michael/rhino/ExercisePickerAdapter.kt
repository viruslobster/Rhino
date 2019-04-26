package com.example.michael.rhino

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExercisePickerAdapter(private val exercises: MutableList<String>): RecyclerView.Adapter<ExercisePickerAdapter.ViewHolder>() {
    class ViewHolder(val layout: LinearLayout): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_element_layout, parent, false)
            as LinearLayout
        item.setOnClickListener { exerciseSelected(it) }
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.layout.findViewById<TextView>(R.id.textView2)
        textView.text = exercises[position]
    }

    override fun getItemCount() = exercises.size

    private fun exerciseSelected(view: View) {
        val textView = view.findViewById<TextView>(R.id.textView2)
        val intent = Intent(view.context, MainActivity::class.java)
        intent.putExtra(EXERCISE_NAME, textView.text.toString())
        view.context.startActivity(intent)
    }



}