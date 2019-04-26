package com.example.michael.rhino

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.michael.rhino.api.RhinoApi
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val REQUEST_AUTHORIZATION = 1000

class ExercisePickerActivity : AppCompatActivity() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var api: RhinoApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val excercises = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_picker)

        api = RhinoApi.instance

        viewManager = LinearLayoutManager(this)
        viewAdapter = ExercisePickerAdapter(excercises)

        recyclerView = findViewById<RecyclerView>(R.id.exercise_picker_recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        fetchExercises()
    }

    override fun onResume() {
        super.onResume()
        //fetchExercises()
    }


    private fun fetchExercises() {
        scope.launch {
            try {
                excercises.clear()
                excercises.addAll(api.getExercises())
                viewAdapter.notifyDataSetChanged()
            } catch (e: UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, REQUEST_AUTHORIZATION)
            }
        }
    }

}
