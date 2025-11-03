package com.example.jobfinderapp.data.local.dao


import androidx.room.*
import com.example.jobfinderapp.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)

    @Delete
    suspend fun deleteSearch(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE searchQuery = :query")
    suspend fun deleteSearchByQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearches()
}
