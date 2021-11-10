package com.kedaireka.monitoring_biomassa.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE dd-MMM-yyyy")
        .format(systemTime).toString()
}