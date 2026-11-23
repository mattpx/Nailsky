package com.nailsky.app.data

import androidx.compose.runtime.Immutable
import com.nailsky.app.data.local.BoardDesignCrossRef
import com.nailsky.app.data.local.BoardEntity
import com.nailsky.app.data.local.NailSkyDao
import com.nailsky.app.data.local.RecentViewEntity
import com.nailsky.app.data.local.SavedDesignEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Immutable
data class BoardWithDesigns(
    val id: Long,
    val name: String,
    val emoji: String,
    val designIds: List<Int>,
)

class NailSkyRepository(private val dao: NailSkyDao) {

    val savedIds: Flow<List<Int>> = dao.observeSavedIds()

    val savedNotes: Flow<Map<Int, String>> = dao.observeSavedDesigns().map { entries ->
        entries.associate { it.designId to it.notes }
    }

    val boards: Flow<List<BoardWithDesigns>> = combine(
        dao.observeBoards(),
        dao.observeAllBoardCrossRefs(),
    ) { boards, refs ->
        val grouped = refs.groupBy { it.boardId }
        boards.map { board ->
            BoardWithDesigns(
                id = board.id,
                name = board.name,
                emoji = board.emoji,
                designIds = grouped[board.id].orEmpty().map { it.designId },
            )
        }
    }

    val recentViewIds: Flow<List<Int>> = dao.observeRecentViewIds()

    suspend fun toggleSave(designId: Int): Boolean {
        return if (dao.isSaved(designId)) {
            dao.deleteSaved(designId)
            dao.removeDesignFromAllBoards(designId)
            false
        } else {
            dao.insertSaved(SavedDesignEntity(designId = designId, savedAt = System.currentTimeMillis()))
            true
        }
    }

    suspend fun updateNote(designId: Int, note: String) {
        if (!dao.isSaved(designId)) {
            dao.insertSaved(
                SavedDesignEntity(
                    designId = designId,
                    savedAt = System.currentTimeMillis(),
                    notes = note.trim(),
                ),
            )
        } else {
            dao.updateNote(designId, note.trim())
        }
    }

    suspend fun createBoard(name: String, emoji: String): Long? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        return dao.insertBoard(
            BoardEntity(
                name = trimmed,
                emoji = emoji.ifBlank { "💅" },
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteBoard(boardId: Long) {
        dao.deleteBoard(boardId)
    }

    suspend fun toggleDesignInBoard(
        boardId: Long,
        designId: Int,
        currentlyInBoard: Boolean,
    ): Boolean {
        return if (currentlyInBoard) {
            dao.removeDesignFromBoard(boardId, designId)
            false
        } else {
            if (!dao.isSaved(designId)) {
                dao.insertSaved(
                    SavedDesignEntity(designId = designId, savedAt = System.currentTimeMillis()),
                )
            }
            dao.addDesignToBoard(
                BoardDesignCrossRef(
                    boardId = boardId,
                    designId = designId,
                    addedAt = System.currentTimeMillis(),
                ),
            )
            true
        }
    }

    suspend fun recordView(designId: Int) {
        dao.upsertRecentView(
            RecentViewEntity(designId = designId, viewedAt = System.currentTimeMillis()),
        )
    }

    suspend fun clearRecentViews() {
        dao.clearRecentViews()
    }
}
