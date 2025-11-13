package com.example.csci_6221_kotlin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform