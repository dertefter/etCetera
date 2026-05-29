package com.dertefter.data.repository

import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import java.io.File

interface NewPostRepository {

    suspend fun upload(file: File): Result<AttachmentUploadResponseDto>

    suspend fun newPost(newPostRequestDto: NewPostRequestDto): Result<PostDto>

}