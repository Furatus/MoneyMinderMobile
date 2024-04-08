package com.example.moneymindermobile.data

import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient

class MainViewModel(
    private val httpClient: HttpClient,
//    private val repository: Repository
) : ViewModel()