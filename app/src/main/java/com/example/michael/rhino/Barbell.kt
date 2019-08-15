package com.example.michael.rhino

import java.io.Serializable

data class Barbell(val count45: Int, val count35: Int, val count25: Int, val count10: Int,
                   val count5: Int, val count2: Int): Serializable {

    fun weight(): Int {
        val result = 45 + (count45 * 45 + count35 * 35 + count25 * 25 + count10 * 10 + count5 * 5 + count2 * 2.5) * 2
        return result.toInt()
    }
}