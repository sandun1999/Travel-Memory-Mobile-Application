package com.example.travel_location;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ListFragment listFragment;
    private List<Model> modelList;

    public MyAdapter(ListFragment listFragment, List<Model> modelList) {
        this.listFragment = listFragment;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(listFragment.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = modelList.get(position);

        // Set data to views
        Glide.with(listFragment).load(model.getImageUrl()).into(holder.imageView);
        holder.streetTextView.setText(model.getStreet());
        holder.cityCountryTextView.setText(model.getCity() + ", " + model.getCountry());

        // Click listener for item view
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOptionsDialog(position);
                return true; // consume the long click
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(listFragment.getContext());
        builder.setTitle("Select Option")
                .setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            editPlace(position);
                        } else {
                            deletePlace(position);
                        }
                    }
                });
        builder.create().show();
    }

    private void editPlace(int position) {
        // Implement the logic to edit a place here
        Toast.makeText(listFragment.getContext(), "Edit functionality will be implemented here", Toast.LENGTH_SHORT).show();
    }

    private void deletePlace(int position) {
        Model model = modelList.get(position); // Get the selected item
        String placeKey = model.getKey(); // Get the Firebase key

        if (placeKey == null || placeKey.isEmpty()) {
            Toast.makeText(listFragment.getContext(), "Unable to delete: Key is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete from Firebase
        FirebaseDatabase.getInstance()
                .getReference("places")
                .child(placeKey) // Use the Firebase key
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted, update local list
                    modelList.remove(position); // Remove from list
                    notifyItemRemoved(position); // Notify RecyclerView
                    notifyItemRangeChanged(position, modelList.size());
                    Toast.makeText(listFragment.getContext(), "Place deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(listFragment.getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView streetTextView, cityCountryTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.listItemImage);
            streetTextView = itemView.findViewById(R.id.listItemStreet);
            cityCountryTextView = itemView.findViewById(R.id.listItemCityCountry);
        }
    }
}

