package com.hasan.youtubedownloader.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import java.sql.Time
import java.util.Timer
import java.util.TimerTask
import java.util.regex.Matcher
import java.util.regex.Pattern

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun String.isEmailValid(): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}