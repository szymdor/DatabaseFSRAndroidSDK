package com.szymdor.fsr

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
//import androidx.room.Update
//import androidx.room.Delete

@Dao
interface SignalDao {
    @Insert
    suspend fun insert(signal: Signal)
    //@Update
    //suspend fun update(signal: Signal)
    //@Delete
    //suspend fun delete(signal: Signal)
    @Query("SELECT value FROM signals ORDER BY signalId DESC LIMIT 1")
    fun getLastValue(): LiveData<Float>
    @Query("SELECT * FROM signals ORDER BY signalId DESC LIMIT 100")
    fun getLastValues(): LiveData<List<Signal>>
    @Query("SELECT * FROM signals WHERE time >= :startTime ORDER BY time ASC")
    fun getLastSeconds(startTime: Long): LiveData<List<Signal>>
    @Query("DELETE FROM signals")
    suspend fun deleteAllSignals()
}