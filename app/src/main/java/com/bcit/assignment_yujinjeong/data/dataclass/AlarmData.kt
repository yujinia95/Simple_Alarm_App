package com.bcit.assignment_yujinjeong.data.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek


//@Entity marks a class as a database Entity class
@Entity(tableName = "alarms")
data class Alarm(
    //Marking field as PK
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean = true,
    //Defining days when the alarm should repeat
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val label: String = ""
)