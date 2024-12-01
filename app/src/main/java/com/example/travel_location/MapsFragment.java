package com.example.travel_location;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ZoomControls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travel_location.Model;
import com.example.travel_location.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;

public class MapsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private GoogleMap googleMap;
    private ZoomControls zoomControls;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
            // Enable zoom controls
            googleMap = map;
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Fetch data from Firebase and add markers
            fetchPlacesFromFirebase();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("places");

        // Initialize Map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        // Initialize ZoomControls
        zoomControls = view.findViewById(R.id.zoomControls);

        // Set listeners for ZoomControls
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
            }
        });

        return view;
    }

    private void fetchPlacesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear existing markers
                if (googleMap != null) {
                    googleMap.clear();
                } else {
                    return; // If map is not ready, return
                }

                // Iterate through each place in the database
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    Model model = placeSnapshot.getValue(Model.class);

                    // Add a marker for each place
                    if (model != null) {
                        // Concatenate street, city, and country for a more descriptive title
                        String markerTitle = model.getStreet() + ", " + model.getCity() + ", " + model.getCountry();

                        Log.d("LocationDebug", "Title: " + markerTitle);

                        LatLng location = getLocationFromAddress(model.getStreet() + ", " + model.getCity() + ", " + model.getCountry());

                        if (location != null) {
                            MarkerOptions markerOptions = new MarkerOptions().position(location).title(markerTitle);

                            // Load and set the image using Picasso
                            if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
                                Picasso.get().load(model.getImageUrl()).into(new Target() {

                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                        googleMap.addMarker(markerOptions);
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        // Handle failure to load image
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        // Do nothing here
                                    }
                                });
                            } else {
                                googleMap.addMarker(markerOptions);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            }
                        }
                    }
                }
            }

            // error handling map location
            private LatLng getLocationFromAddress(String strAddress) {
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses;
                LatLng latLng = null;
                try {
                    addresses = geocoder.getFromLocationName(strAddress, 1);

                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // Optional: Set initial zoom level
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                    } else {
                        Log.e("LocationDebug", "No location found for address: " + strAddress);
                    }
                } catch (IOException e) {
                    Log.e("LocationDebug", "Error converting address to location", e);
                }
                return latLng;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
