package com.example.diagnaltask.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.diagnaltask.R

class Utils {
    companion object {

        fun getMoviesPictures(pictureKey: String?): Int {
            val moviePoster = when (pictureKey) {
                "poster1.jpg" -> R.drawable.poster1
                "poster2.jpg" -> R.drawable.poster2
                "poster3.jpg" -> R.drawable.poster3
                "poster4.jpg" -> R.drawable.poster4
                "poster5.jpg" -> R.drawable.poster5
                "poster6.jpg" -> R.drawable.poster6
                "poster7.jpg" -> R.drawable.poster7
                "poster8.jpg" -> R.drawable.poster8
                "poster9.jpg" -> R.drawable.poster9
                "posterthatismissing.jpg" -> R.drawable.placeholder_for_missing_posters
                else -> R.drawable.placeholder_for_missing_posters
            }
            return moviePoster
        }

        fun hideKeyboard(activity: Activity) {
            val view = activity.findViewById<View>(android.R.id.content)
            if (view != null) {
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

    }
}