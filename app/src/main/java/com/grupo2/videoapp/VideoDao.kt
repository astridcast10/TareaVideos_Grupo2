package com.grupo2.videoapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VideoDao {
    @Insert
    suspend fun insertVideo(video: VideoEntity)

    @Query("SELECT * FROM videos ORDER BY timestamp DESC")
    suspend fun getAllVideos(): List<VideoEntity>
}
