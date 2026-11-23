package com.nailsky.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "saved_designs")
data class SavedDesignEntity(
    @PrimaryKey val designId: Int,
    val savedAt: Long,
    val notes: String = "",
)

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String = "💅",
    val createdAt: Long,
)

@Entity(
    tableName = "board_designs",
    primaryKeys = ["boardId", "designId"],
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["id"],
            childColumns = ["boardId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("boardId"),
        Index("designId"),
    ],
)
data class BoardDesignCrossRef(
    val boardId: Long,
    val designId: Int,
    val addedAt: Long,
)

@Entity(tableName = "recent_views")
data class RecentViewEntity(
    @PrimaryKey val designId: Int,
    val viewedAt: Long,
)
