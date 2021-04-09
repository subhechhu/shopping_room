package com.subhechhu.demodb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Similar to @Entity in Model class
//@Database creates the database on the basis
//of the model class. Model class also has the
//table created already
@Database(entities = {MainData.class}, version = 2, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    private static RoomDB database; //Static variable so that it can be accessed directly without creating the object of its class

    public abstract MainDao mainDao();

    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) { //check is added so that there won't be more than 1 instance of the database
            String DATABASE_NAME = "database"; //Database name
            database = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class,
                    DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() //Prevents the crash by replacing older db by new db during migration
                    .build();

        }
        return database;
    }
}