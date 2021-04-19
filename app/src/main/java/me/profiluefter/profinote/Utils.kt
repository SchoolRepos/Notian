package me.profiluefter.profinote

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("drawableEndCompat")
fun setDrawableEndCompat(view: TextView, drawable: Drawable?) {
    val (start, top, _, bottom) = view.compoundDrawables
    view.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, drawable, bottom)
}

@BindingAdapter("errorText")
fun setErrorText(view: TextInputLayout, text: String?) {
    view.error = text
}

@BindingAdapter("fabHidden")
fun setFABHidden(fab: FloatingActionButton, hidden: Boolean) {
    if(hidden) {
        fab.hide()
    } else {
        fab.show()
    }
}

@ColorInt
fun Context.themeColor(@AttrRes themeAttrId: Int): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}
