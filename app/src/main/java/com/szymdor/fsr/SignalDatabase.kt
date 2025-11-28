package com.szymdor.fsr

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [Signal::class], version = 1, exportSchema = false)
abstract class SignalDatabase: RoomDatabase() {
    abstract val signalDao: SignalDao

    companion object {
        @Volatile
        private var INSTANCE: SignalDatabase? = null

        fun getInstance(context: Context): SignalDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, SignalDatabase::class.java, "tasks_database").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}