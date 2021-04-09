package com.subhechhu.demodb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


//DAO is interface of Room Library
//It stands for data access object
//It makes the DB query easier compared to
//the traditional way of the coding
@Dao
public interface MainDao {

    //Inserting new element to database
    @Insert(onConflict = REPLACE)
    // if the ID is same, replace the older data with the newer data
    void insert(MainData mainData);

    //Deleting the element from the database
    @Delete
    void delete(MainData mainData);

    //Delete all the element from the database
    @Delete
    void reset(List<MainData> mainData);

    //DAO also supports the oldschool apporach with the regular sql queries
    @Query("UPDATE table_name SET text = :sText, quantity = :sQuantity, type = :sType WHERE ID = :sID")
    void update(int sID, String sText, float sQuantity, int sType);

    //Gives the entire database elements
    @Query("SELECT * FROM table_name  ORDER BY ID DESC")
    List<MainData> getAll();

}
