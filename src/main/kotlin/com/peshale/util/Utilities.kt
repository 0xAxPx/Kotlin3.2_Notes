package com.peshale.util
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.RandomAccess
import kotlin.random.Random

class Utilities {

    companion object {
        fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            return format.format(date)
        }

        fun randomInt(): Int {
            return Random.nextInt(1, 100)
        }

    }
}