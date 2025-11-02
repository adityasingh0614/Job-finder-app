package com.example.jobfinderapp.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jobfinderapp.domain.model.Job

@Entity(tableName = "saved_jobs")
data class JobEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val companyName: String,
    val companyLogo: String,
    val location: String,
    val salary: String?,
    val description: String,
    val category: String,
    val jobType: String,
    val tags: String, // Stored as comma-separated string
    val postedDate: String,
    val applyUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)

// Mapper functions
fun JobEntity.toJob(): Job {
    return Job(
        id = id,
        title = title,
        companyName = companyName,
        companyLogo = companyLogo,
        location = location,
        salary = salary,
        description = description,
        category = category,
        jobType = jobType,
        tags = tags.split(",").filter { it.isNotBlank() },
        postedDate = postedDate,
        applyUrl = applyUrl
    )
}

fun Job.toEntity(): JobEntity {
    return JobEntity(
        id = id,
        title = title,
        companyName = companyName,
        companyLogo = companyLogo,
        location = location,
        salary = salary,
        description = description,
        category = category,
        jobType = jobType,
        tags = tags.joinToString(","),
        postedDate = postedDate,
        applyUrl = applyUrl
    )
}
