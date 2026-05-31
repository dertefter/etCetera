package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.search.SearchHashtagDto

@Entity(tableName = "trending_hashtags")
data class SearchHashtagEntity(
    @PrimaryKey val id: String,
    val name: String,
    val postsCount: Int
)

fun SearchHashtagEntity.asExternalModel() = SearchHashtagDto(
    id = id,
    name = name,
    postsCount = postsCount
)

fun SearchHashtagDto.asEntity() = SearchHashtagEntity(
    id = id,
    name = name,
    postsCount = postsCount
)
