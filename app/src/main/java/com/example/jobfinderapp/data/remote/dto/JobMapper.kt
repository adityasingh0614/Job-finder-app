package com.example.jobfinderapp.data.remote.dto

import com.example.jobfinderapp.domain.model.Job

fun JobDto.toDomain(): Job {
    return Job(
        id = id,
        title = title,
        companyName = companyName,
        companyLogo = companyLogo.ifEmpty { "https://via.placeholder.com/150" },
        location = location,
        salary = salary ?: "Not specified",
        description = description,
        category = category,
        jobType = jobType ?: "Full-time",
        tags = extractTags(),
        postedDate = publicationDate,
        applyUrl = url
    )
}

private fun JobDto.extractTags(): List<String> {
    val tags = mutableListOf<String>()

    if (!jobType.isNullOrEmpty()) {
        tags.add(jobType)
    }

    tags.add(category)

    if (location.contains("Worldwide", ignoreCase = true)) {
        tags.add("🌍 Worldwide")
    }

    if (!salary.isNullOrEmpty()) {
        tags.add("💰 Salary Listed")
    }

    return tags.take(4)
}
