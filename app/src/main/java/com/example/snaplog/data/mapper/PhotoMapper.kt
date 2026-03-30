package com.example.snaplog.data.mapper

import com.example.snaplog.data.local.PhotoEntity
import com.example.snaplog.domain.model.Photo

fun PhotoEntity.toDomain(): Photo =
    Photo(
        id = id,
        imagePath = imagePath,
        memo = memo,
        tag = tag,
        createdAt = createdAt
    )

fun Photo.toEntity(): PhotoEntity =
    PhotoEntity(
        id = id,
        imagePath = imagePath,
        memo = memo,
        tag = tag,
        createdAt = createdAt
    )