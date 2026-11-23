package com.nailsky.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NailSkyDao {

    @Query("SELECT designId FROM saved_designs ORDER BY savedAt DESC")
    fun observeSavedIds(): Flow<List<Int>>

    @Query("SELECT * FROM saved_designs ORDER BY savedAt DESC")
    fun observeSavedDesigns(): Flow<List<SavedDesignEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_designs WHERE designId = :designId)")
    suspend fun isSaved(designId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSaved(entity: SavedDesignEntity)

    @Query("DELETE FROM saved_designs WHERE designId = :designId")
    suspend fun deleteSaved(designId: Int)

    @Query("UPDATE saved_designs SET notes = :notes WHERE designId = :designId")
    suspend fun updateNote(designId: Int, notes: String)

    @Query("SELECT * FROM boards ORDER BY createdAt DESC")
    fun observeBoards(): Flow<List<BoardEntity>>

    @Query("SELECT * FROM board_designs ORDER BY addedAt DESC")
    fun observeAllBoardCrossRefs(): Flow<List<BoardDesignCrossRef>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity): Long

    @Query("DELETE FROM boards WHERE id = :boardId")
    suspend fun deleteBoard(boardId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDesignToBoard(ref: BoardDesignCrossRef)

    @Query("DELETE FROM board_designs WHERE boardId = :boardId AND designId = :designId")
    suspend fun removeDesignFromBoard(boardId: Long, designId: Int)

    @Query("DELETE FROM board_designs WHERE designId = :designId")
    suspend fun removeDesignFromAllBoards(designId: Int)

    @Query("SELECT designId FROM recent_views ORDER BY viewedAt DESC LIMIT :limit")
    fun observeRecentViewIds(limit: Int = 12): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecentView(entity: RecentViewEntity)

    @Query("DELETE FROM recent_views")
    suspend fun clearRecentViews()
}
