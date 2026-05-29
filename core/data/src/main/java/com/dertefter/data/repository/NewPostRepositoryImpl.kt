package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import java.io.File
import javax.inject.Inject

class NewPostRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSourceImpl: LocalDataSource,
) : NewPostRepository {

    override suspend fun upload(file: File): Result<AttachmentUploadResponseDto> {
        return remoteDataSource.uploadMyFile(file)
    }

    override suspend fun newPost(newPostRequestDto: NewPostRequestDto): Result<PostDto> {
        return remoteDataSource.newPost(newPostRequestDto)
    }




}
