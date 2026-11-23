package com.nailsky.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedDesignEntity::class,
        BoardEntity::class,
        BoardDesignCrossRef::class,
        RecentViewEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class NailSkyDatabase : RoomDatabase() {

    abstract fun dao(): NailSkyDao

    companion object {
        @Volatile
        private var instance: NailSkyDatabase? = null

        fun getInstance(context: Context): NailSkyDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NailSkyDatabase::class.java,
                    "nailsky.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
