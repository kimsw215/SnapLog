package com.example.snaplog.domain.model

data class Photo(
    val id: Long,
    val imagePath: String,
    val memo: String,
    val tag: String,
    val createdAt: Long
)
