package com.example.michael.rhino.api

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class RhinoApi private constructor(credential: GoogleAccountCredential) : ViewModel() {
    private val APPLICATION_NAME = "Rhino"
    private var service: Sheets
    private val spreadsheetID = "1woasUopUN6iIw2SG880LjKl4a79tMXPia4rDzOZkNuk"
    private val TAG = "RHINOAPI"
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    companion object {
        lateinit var instance: RhinoApi
            private set

        fun setCredentials(credential: GoogleAccountCredential) {
            instance = RhinoApi(credential)
        }
    }


    init {
        val jsonFactory = JacksonFactory.getDefaultInstance()
        val httpTransport = com.google.api.client.http.javanet.NetHttpTransport()

        service = Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    suspend fun getExercises(): List<String> {
        lateinit var exercises: List<String>
        withContext(Dispatchers.IO) {
            val result = service.spreadsheets().get(spreadsheetID).execute()
            result.sheets.forEach { Log.d("TAG", it.toPrettyString()) }
            exercises = result.sheets.map { it.properties["title"].toString() }
        }
        return exercises.sorted()
    }

    suspend fun writeSet(exercise: Exercise) {
        withContext(Dispatchers.IO) {
            // check if exercise.excercise sheet exists
            Log.d(TAG, "Checking if sheet ${exercise.name} exists...")
            val response1 = service.Spreadsheets().get(spreadsheetID).execute()
            val sheetExists = response1.sheets.any { it.properties["title"] == exercise.name}

            // if not create it
            if (!sheetExists) {
                Log.d(TAG, "Sheet ${exercise.name} does not exist. Creating...")
                val props = SheetProperties().set("title", exercise.name)
                val request = Request().setAddSheet(AddSheetRequest().setProperties(props))
                val batchUpdate = BatchUpdateSpreadsheetRequest().setRequests(listOf(request))
                val response2 = service.Spreadsheets().batchUpdate(spreadsheetID, batchUpdate).execute()

                Log.d(TAG, response2.toString())

            }

            // write sheet
            Log.d(TAG, "Writting to sheet ${exercise.name}")
            val range = "${exercise.name}!A:B"
            val time = simpleDateFormat.format(Date())
            val data = (if (!sheetExists) listOf(listOf("Date", "Volume")) else listOf<List<Any>>()) + listOf(listOf(time, exercise.volume))


            val valueRange = ValueRange().setValues(data)

            val response3 = service.Spreadsheets().Values()
                .append(spreadsheetID, range, valueRange)
                .setValueInputOption("USER_ENTERED")
                .execute()
        }



    }

    suspend fun test() {
        withContext(Dispatchers.IO) {
            val response = service.Spreadsheets().values()
                .get(spreadsheetID, "Sheet1!A:B")
                .execute()

            Log.e("TAG", response.getValues().toString())
        }
        /*
        doAsync {
            while(service == null) {

            }

            val spreadsheetID = "1woasUopUN6iIw2SG880LjKl4a79tMXPia4rDzOZkNuk"
            val response: ValueRange? = service?.spreadsheets()?.values()
                .get(spreadsheetID, "Class Data!A2:B2")
                .execute()
            Log.e("TAG", response.toString())
        }
        */
    }
}