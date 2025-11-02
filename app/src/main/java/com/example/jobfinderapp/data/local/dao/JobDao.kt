package com.example.jobfinderapp.data.local.dao
import androidx.room.*
import com.example.jobfinderapp.data.local.entity.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Query("SELECT * FROM saved_jobs ORDER BY savedAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM saved_jobs WHERE id = :jobId")
    suspend fun getJobById(jobId: Int): JobEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM saved_jobs WHERE id = :jobId)")
    fun isJobSaved(jobId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Delete
    suspend fun deleteJob(job: JobEntity)

    @Query("DELETE FROM saved_jobs WHERE id = :jobId")
    suspend fun deleteJobById(jobId: Int)

    @Query("DELETE FROM saved_jobs")
    suspend fun deleteAllJobs()

    @Query("SELECT COUNT(*) FROM saved_jobs")
    fun getSavedJobsCount(): Flow<Int>
}
