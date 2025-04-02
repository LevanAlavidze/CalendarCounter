package com.example.testforcalendarcounter.data.memorycard

data class MemoryCard(
    val id: Int,
    val content: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false,
)
