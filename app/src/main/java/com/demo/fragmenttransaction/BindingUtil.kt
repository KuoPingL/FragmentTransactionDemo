package com.demo.fragmenttransaction

import android.widget.TextView

import androidx.databinding.BindingAdapter


@BindingAdapter("app:addSpaceToText")
fun setTextWithSpace(view: TextView, text: CharSequence?) {
    // Some temp code related conditional checks is removed

    val finalText = text?.let {
        var formattedString = ""

        it.forEach {
            formattedString += it
            formattedString += "\n"
        }
        formattedString.dropLast(1)

    } ?: text

    view.text = finalText
}

@BindingAdapter("app:intToString")
fun setIntToText(view: TextView, value: Int) {
    view.text = "$value"
}