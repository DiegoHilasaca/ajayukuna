package com.ajayu.newproyect.ui.apoyanos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DonacionesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Apoyanos a seguir difundiendo el arte y la cultura"
    }
    val text: LiveData<String> = _text
}