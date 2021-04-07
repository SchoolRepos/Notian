package me.profiluefter.profinote

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("drawableEndCompat")
fun setDrawableEndCompat(view: TextView, drawable: Drawable?) {
    val (start, top, _, bottom) = view.compoundDrawables
    view.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, drawable, bottom)
}

@BindingAdapter("app:errorText")
fun setErrorText(view: TextInputLayout, text: String?) {
    view.error = text
}