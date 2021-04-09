package com.subhechhu.demodb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//adding the table name
//@Entity is an interface created my ROOM Library
//which allows us to create the table on the basis
//of the model class easing the table creation
@Entity(tableName = "table_name")
public class MainData implements Serializable {

    @PrimaryKey(autoGenerate = true)// id is the primary key and is auto generated
    private int ID;

    @ColumnInfo(name = "text")//name of the column
    private String text;

    @ColumnInfo(name = "quantity")
    private float quantity;

    @ColumnInfo(name = "type")
    private int type;

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MainData{" +
                "ID=" + ID +
                ", text='" + text + '\'' +
                ", quantity=" + quantity +
                ", type=" + type +
                '}';
    }
}
