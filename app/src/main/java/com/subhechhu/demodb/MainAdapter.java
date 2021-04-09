package com.subhechhu.demodb;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<MainData> dataList;
    private Activity context;
    private RoomDB database;
    int radioCheckIndex = 0;

    public MainAdapter(Activity context, List<MainData> dataList) { //constructor class
        this.context = context;
        this.dataList = dataList;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creates one row one the adapter gets the data.
        //Every index of the list will have one separate view, here the view is row_items;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_items, parent, false);
        return new ViewHolder(view); //create the new view for particular index of data by passing the layout
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //holder gives exact row. Position gives the position of the holder or the element that you will interact with
        MainData data = dataList.get(position); //get the same indexed data from the database as the position of the view
        database = RoomDB.getInstance(context); //create the instance of the database

        String quantity = data.getQuantity() + Const.getType(data.getType());

        holder.textview_quantity.setText(quantity);
        holder.textView.setText(data.getText()); //get the text from data base and set it on the exact position. Developer need not worry about it. Holder will handle the position where the text will be set
        holder.imageViewEdit.setOnClickListener(new View.OnClickListener() { //holder determines click on the exact row element. Holder will handle it.
            @Override
            public void onClick(View view) {
                MainData d = dataList.get(holder.getAdapterPosition()); //get the data of the clicked position from DB
                int sId = d.getID(); //get the id
                String sText = d.getText(); //get the text from the db
                String sQuantity = String.valueOf(d.getQuantity());

                Dialog dialog = new Dialog(context); //Create the dialog for editing the text
                dialog.setContentView(R.layout.update_dialog); //get the view /layout for the dialog
                int width = WindowManager.LayoutParams.MATCH_PARENT; //set the width programmatically
                int height = WindowManager.LayoutParams.WRAP_CONTENT; //set the height programmatically
                dialog.getWindow().setLayout(width, height); //set the height and the width to the dialog window

                dialog.show(); //render the dialog

                EditText editText = dialog.findViewById(R.id.edittext); //get the edittext from the dialog window to get the modified text
                EditText editText_quantity = dialog.findViewById(R.id.edittext_quantity); //get the quantity from the dialog window to get the modified text
                RadioGroup radioGroup = dialog.findViewById(R.id.radiogroup_dialog);
                Button update = dialog.findViewById(R.id.button_update);

                editText.setText(sText);
                editText_quantity.setText(sQuantity);

                if (d.getType() == 0)
                    radioGroup.check(R.id.radioButton_kg_dialog);
                else if (d.getType() == 1)
                    radioGroup.check(R.id.radioButton_lt_dialog);
                else
                    radioGroup.check(R.id.radioButton_item_dialog);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (i == R.id.radioButton_kg_dialog)
                            radioCheckIndex = 0;
                        else if (i == R.id.radioButton_lt_dialog)
                            radioCheckIndex = 1;
                        else if (i == R.id.radioButton_item_dialog)
                            radioCheckIndex = 2;
                    }
                });

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String updatedText = String.valueOf(editText.getText());  //get the text on the edittext view
                        String updatedQuantity = String.valueOf(editText_quantity.getText());

                        if (updatedText.isEmpty() || updatedQuantity.isEmpty()) {
                            Toast.makeText(context, "Cannot Update Empty Item or Quantity To Shopping List", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss(); //close the dialog on the button press

                            //update the element in the DB. SID is taken from #52 via position of the adapter
                            //we are updating the same SID. Hence it replaces the older text with the newer text
                            database.mainDao().update(sId,
                                    updatedText,
                                    Float.parseFloat(updatedQuantity),
                                    radioCheckIndex);

                            Toast.makeText(context, sText + " updated to " + updatedText, Toast.LENGTH_SHORT).show();

                            dataList.clear(); //remove all the element from the current list
                            dataList.addAll(database.mainDao().getAll()); //add the updated elements from the DB to the list
                            notifyDataSetChanged(); //notify the View class about the changes so that it will refresh the UI
                        }
                    }
                });
            }
        });

        //holder will get the delete view of the exact position where user clicks
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainData d = dataList.get(holder.getAdapterPosition()); //get the element from the data base on the basis of the position of the click
                database.mainDao().delete(d); //delete it from the database
                int position = holder.getAdapterPosition();

                Toast.makeText(context, d.getText() + " at position " + position + " deleted", Toast.LENGTH_SHORT).show();

                dataList.remove(position); //remove the element from the same index of the holder
                notifyItemRemoved(position); //notify the UI about the position of the item removed
                notifyItemRangeChanged(position, dataList.size()); //notify the position changes for the remaining elements as all need to move an index before the current index
            }
        });

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                MainData mainData = dataList.get(holder.getAdapterPosition());
                intent.putExtra("item", mainData.getText());
                intent.putExtra("id", mainData.getID());
                intent.putExtra("type", mainData.getType());
                intent.putExtra("q", mainData.getQuantity());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();//returns the size of the list
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textview_quantity;
        ImageView imageViewEdit, imageViewDelete;
        CardView cardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardview);
            textView = itemView.findViewById(R.id.textview); //textview which belongs to the row item
            textview_quantity = itemView.findViewById(R.id.textview_quantity); //textview_quantity which belongs to the row item
            imageViewEdit = itemView.findViewById(R.id.imageview_edit); //imageview which belongs to the row item
            imageViewDelete = itemView.findViewById(R.id.imageview_delete); //imageview which belongs to the row item

        }
    }
}
