package com.example.michael.rhino

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class WeightSelectorActivity : AppCompatActivity() {

    lateinit var weightButton45: WeightButton
    lateinit var weightButton35: WeightButton
    lateinit var weightButton25: WeightButton
    lateinit var weightButton10: WeightButton
    lateinit var weightButton5: WeightButton
    lateinit var weightButton2: WeightButton
    lateinit var barbellDrawView: BarbellDrawView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_selector)

        weightButton45 = findViewById(R.id.weightButton45)
        weightButton35 = findViewById(R.id.weightButton35)
        weightButton25 = findViewById(R.id.weightButton25)
        weightButton10 = findViewById(R.id.weightButton10)
        weightButton5  = findViewById(R.id.weightButton5)
        weightButton2  = findViewById(R.id.weightButton2)
        barbellDrawView = findViewById(R.id.barbell_view)

        listOf(weightButton45, weightButton35, weightButton25, weightButton10, weightButton5, weightButton2)
            .forEach {it.onChange = ::onChange}

        val barbell = intent.getSerializableExtra(BARBELL_EXTRA) as Barbell
        weightButton45.count = barbell.count45
        weightButton35.count = barbell.count35
        weightButton25.count = barbell.count25
        weightButton10.count = barbell.count10
        weightButton5.count = barbell.count5
        weightButton2.count = barbell.count2
        onChange()
    }

    private fun onChange() {
        barbellDrawView.apply {
            weight45 = weightButton45.count
            weight35 = weightButton35.count
            weight25 = weightButton25.count
            weight10 = weightButton10.count
            weight5  = weightButton5.count
            weight2  = weightButton2.count
        }
        barbellDrawView.invalidate()
    }

    fun done(view: View) {
        val data = Intent()
        val barbell = Barbell(
            weightButton45.count, weightButton35.count, weightButton25.count,
            weightButton10.count, weightButton5.count, weightButton2.count
        )
        data.putExtra(BARBELL_EXTRA, barbell)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}
