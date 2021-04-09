package com.subhechhu.demodb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText, edittext_quantity;
    Button buttonAdd, buttonReset;
    RecyclerView recyclerView;
    RadioGroup radioGroup;
    int radioCheckIndex = 0;
    boolean isList = true;

    List<MainData> dataList = new ArrayList<>();// used to get the entire element from the database
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    RoomDB database;
    MainAdapter adapter; //Recyclerview adapter to render rows.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edittext);
        edittext_quantity = findViewById(R.id.edittext_quantity);
        buttonAdd = findViewById(R.id.button_add);
        buttonReset = findViewById(R.id.button_reset);

        radioGroup = findViewById(R.id.radiogroup);

        recyclerView = findViewById(R.id.recyclerview);

        database = RoomDB.getInstance(this); //initialize the database
        dataList = database.mainDao().getAll(); //get all the rows from the database

        Log.e("TAGGED", "DataList: " + dataList.toString());

        linearLayoutManager = new LinearLayoutManager(this); //recyclerview needs a layoutmanager to render the items. you can change it to make it a gridview layout
        gridLayoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MainAdapter(this, dataList); //pass the current list of element to the adapter.

        recyclerView.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButton_kg)
                    radioCheckIndex = 0;
                else if (i == R.id.radioButton_lt)
                    radioCheckIndex = 1;
                else if (i == R.id.radioButton_item)
                    radioCheckIndex = 2;
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = editText.getText().toString();
                String quantity = edittext_quantity.getText().toString();

                if (text.isEmpty() || quantity.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Cannot Add Empty Item or Quantity To Shopping List", Toast.LENGTH_SHORT).show();
                } else {
                    // proceed if the edittext is not empty and something is written
                    // create the instance of the model class so that we can pass it to the database.
                    // Database stores the type of the MainData.
                    // If you check DAO interface to insert the code is,
                    // insert(MainData mainData);
                    //hence we need an mainData Object
                    MainData data = new MainData();

                    // add the text.
                    // no need to add the ID as it is auto incremented.
                    // Specified in #16 in MainData.Java
                    data.setText(text);
                    data.setQuantity(Float.parseFloat(quantity));
                    data.setType(radioCheckIndex);

                    // add the MainData object with the text to database
                    database.mainDao().insert(data);
                    Toast.makeText(MainActivity.this, text + " added to Shopping List", Toast.LENGTH_SHORT).show();

                    // clear the text on the edittext
                    editText.requestFocus();
                    editText.setText("");
                    edittext_quantity.setText("");
                    radioGroup.check(R.id.radioButton_kg);

                    Log.e("TAGGED", "before clearing: " + dataList.toString());

                    dataList.clear();// remove all the element from the current list
                    dataList.addAll(database.mainDao().getAll());  // add all the elements from the DB to the list

                    Log.e("TAGGED", "after getting all data from db: " + dataList.toString());

                    adapter.notifyDataSetChanged();  //notify the View class about the changes so that it will refresh the UI

                }
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(true)
                        .setMessage("Are you sure to delete all items?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                clearAllData();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            }
        });
    }

    private void clearAllData() {
        if (database.mainDao().getAll().size() > 0) {
            database.mainDao().reset(dataList); //calls method to remove everything in DAO.java interface

            Toast.makeText(MainActivity.this, "Shopping List Cleared", Toast.LENGTH_SHORT).show();

            dataList.clear();// remove all the element from the current list
            dataList.addAll(database.mainDao().getAll());  // add all the elements from the DB to the list
            adapter.notifyDataSetChanged();  //notify the View class about the changes so that it will refresh the UI

            Toast.makeText(MainActivity.this, "Shopping list is cleared", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(MainActivity.this, "Nothing to clear. Shopping List is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(getString(R.string.switch_layout))) {
            if (isList) {
                isList = false;
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                isList = true;
                recyclerView.setLayoutManager(linearLayoutManager);
            }

        }

        return super.onOptionsItemSelected(item);
    }
}