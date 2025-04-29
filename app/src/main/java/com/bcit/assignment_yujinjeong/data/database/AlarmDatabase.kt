package com.bcit.assignment_yujinjeong.data.database

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bcit.assignment_yujinjeong.data.dataclass.Alarm

/**
 * This class is responsible for defining the database structure.
 */
@Database(entities = [Alarm::class], version = 1, exportSchema = false)
@TypeConverters(DayOfWeekConvert::class)
abstract class AlarmDatabase : RoomDatabase() {

    //Accessing to Data Access Object
    abstract fun alarmDao(): AlarmDao

    //Companion object is like a static member in java.
    companion object {
        const val DATABASE_NAME = "alarm_db"

        //Volatile ensures that multiple threads immediately see changes made to this variable,
        // preventing race conditions when initializing the singleton instance.
        @Volatile
        private var INSTANCE: AlarmDatabase? = null


        /**
         * This static method ensures to follow the singleton pattern.
         */
        @JvmStatic
        fun getInstance(context: Context): AlarmDatabase {

            //If INSTANCE is not null, return existing db instance, else execute synchronized block
            //synchronized prevents race conditions.
            return INSTANCE ?: synchronized(this) {

                //Creating new db instance.
                val instance = Room.databaseBuilder(

                    //applicationContext making sure db doesn't get destroyed if Activity is closed.
                    context.applicationContext,
                    //Specifying which db class Room to build.
                    AlarmDatabase::class.java,
                    DATABASE_NAME
                ).build()

                //Storing newly create db instance in INSTANCE.
                INSTANCE = instance
                //Return
                instance
            }
        }
    }
}
