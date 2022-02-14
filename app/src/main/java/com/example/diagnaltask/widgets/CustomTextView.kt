package com.example.diagnaltask.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.diagnaltask.R

class CustomTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (!isInEditMode)
            initWithAttrs(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (!isInEditMode)
            initWithAttrs(context, attrs, defStyleAttr)
    }

   private fun initWithAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomViews, defStyleAttr, 0)
        val customFontIndex = a.getInt(R.styleable.CustomViews_setFonts, 0)
        val fontPath = resources.getStringArray(R.array.FontNames)[customFontIndex]
        setCustomFont(fontPath)
        a.recycle()
    }

    private fun setCustomFont(customFontPath: String) {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/$customFontPath")
        setTypeface(typeface)
        includeFontPadding = false
    }
}

