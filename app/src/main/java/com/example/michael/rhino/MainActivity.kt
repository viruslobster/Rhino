package com.example.michael.rhino

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.michael.rhino.api.Exercise
import com.example.michael.rhino.api.RhinoApi
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch



private const val REQUEST_AUTHORIZATION = 1001
const val EXERCISE_NAME = "exercise_name"
class MainActivity : AppCompatActivity() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val exerciseSets = mutableListOf<List<Any>>()

    private lateinit var api: RhinoApi
    private lateinit var exerciseNameEditText: EditText
    private lateinit var repsEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        api = RhinoApi.instance

        exerciseNameEditText = findViewById(R.id.exerciseNameEditText)
        repsEditText = findViewById(R.id.repsEditText)
        weightEditText = findViewById(R.id.weightEditText)
        textView = findViewById(R.id.textView)

        exerciseNameEditText.setText(intent.getStringExtra(EXERCISE_NAME) ?: "")
    }

    fun addSet(view: View) {
        val reps = repsEditText.text.toString().toIntOrNull()
        val weight = weightEditText.text.toString().toDoubleOrNull()
        if (reps == null || weight == null) {
            Toast.makeText(this, "Please specify reps and weight", Toast.LENGTH_SHORT).show()

        } else {
            exerciseSets.add(listOf(reps, weight))
            updateTextView()
        }
    }

    fun removeSet(view: View) {
        if (exerciseSets.size > 0) {
            exerciseSets.removeAt(exerciseSets.size - 1)
            updateTextView()
        }
    }

    fun finish(view: View) {
        if (exerciseSets.size == 0) {
            Toast.makeText(this, "Add at least one set", Toast.LENGTH_SHORT).show()
        } else {
            getResultsFromApi()
        }
    }

    fun updateTextView() {
        val text = exerciseSets.map { "${it[0]} x ${it[1]}" }.joinToString("\n")
        textView.text = text
    }

    private fun getResultsFromApi() {
        scope.launch {
            val volume = exerciseSets
                .map { (it[0] as Int) * (it[1] as Double) }
                .sum()

            val name = exerciseNameEditText.text.toString().trim()

            val exercise = Exercise(name, volume)
            try {
                api.writeSet(exercise)
                exerciseSets.clear()
                updateTextView()
            } catch (e: UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, REQUEST_AUTHORIZATION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_AUTHORIZATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    getResultsFromApi()
                }
            }
        }
    }
}
