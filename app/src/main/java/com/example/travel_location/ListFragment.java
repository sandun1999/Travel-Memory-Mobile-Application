package com.example.travel_location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListFragment extends Fragment {



    private ArrayList<Model> placeList;
    private MyAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeList = new ArrayList<>();
        adapter = new MyAdapter(this, placeList); // Pass 'this' as the first parameter instead of 'getActivity()'

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView); // Replace 'recyclerView' with the actual ID of your RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchDataFromFirebase();
    }
    private void fetchDataFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference("places");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeList.clear(); // Clear the list to avoid duplicate entries
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model model = snapshot.getValue(Model.class);
                    if (model != null) {
                        model.setKey(snapshot.getKey()); // Set the Firebase key
                        placeList.add(model);
                    }
                }
                Log.d("DataFetch", "Number of items retrieved: " + placeList.size());
                adapter.notifyDataSetChanged(); // Notify adapter about changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DataFetch", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

}


