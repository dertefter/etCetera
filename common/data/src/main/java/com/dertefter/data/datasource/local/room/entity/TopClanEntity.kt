package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.search.TopClanDto

@Entity(tableName = "top_clans")
data class TopClanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val avatar: String,
    val postsCount: Int
)

fun TopClanEntity.asExternalModel() = TopClanDto(
    avatar = avatar,
    postsCount = postsCount
)

fun TopClanDto.asEntity() = TopClanEntity(
    avatar = avatar,
    postsCount = postsCount
)
