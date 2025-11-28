package com.szymdor.fsr

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "signals")
data class Signal(
    @PrimaryKey(autoGenerate = true)
    var signalId: Long = 0L,
    @ColumnInfo (name = "time")
    var timeStamp: Long = 0L,
    @ColumnInfo (name = "value")
    var signalValue: Float = 0F
)