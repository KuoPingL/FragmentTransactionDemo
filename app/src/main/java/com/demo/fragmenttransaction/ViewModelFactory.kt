package com.demo.fragmenttransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.fragmenttransaction.frags.FragViewModel

@Suppress("UNCHECKED_CAST")
val ViewModelFactory = object: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return with(modelClass) {
            when {
                isAssignableFrom(FragViewModel::class.java) -> {
                    FragViewModel()
                }
                isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel()
                }
                else -> {
                    throw IllegalArgumentException("")
                }
            }
        } as T
    }
}