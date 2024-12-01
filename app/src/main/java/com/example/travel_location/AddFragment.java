package com.example.travel_location;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText streetEditText, cityEditText, countryEditText;
    private ImageView imageView;
    private Button saveButton;

    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        streetEditText = view.findViewById(R.id.streatID);
        cityEditText = view.findViewById(R.id.cityID);
        countryEditText = view.findViewById(R.id.countryID);
        imageView = view.findViewById(R.id.imageID);
        saveButton = view.findViewById(R.id.savebtnID);

        databaseReference = FirebaseDatabase.getInstance().getReference("places");
        storageReference = FirebaseStorage.getInstance().getReference("place_images");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading...");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlace();
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void savePlace() {
        final String street = streetEditText.getText().toString().trim();
        final String city = cityEditText.getText().toString().trim();
        final String country = countryEditText.getText().toString().trim();

        if (street.isEmpty() || city.isEmpty() || country.isEmpty() || imageUri == null) {
            Toast.makeText(getActivity(), "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Upload image to Firebase Storage
        final StorageReference imageRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Image upload successful, now save data to Firebase Realtime Database
                                String imageUrl = uri.toString();

                                Map<String, Object> placeMap = new HashMap<>();
                                placeMap.put("street", street);
                                placeMap.put("city", city);
                                placeMap.put("country", country);
                                placeMap.put("imageUrl", imageUrl);

                                String placeId = databaseReference.push().getKey();
                                databaseReference.child(placeId).setValue(placeMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), "Place added successfully", Toast.LENGTH_SHORT).show();
                                                clearFields();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("FirebaseUpload", "Failed to upload image", e);
                                    }
                                });


                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        // Get the file extension from the image URI
        return "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(getActivity().getContentResolver().getType(uri));
    }

    private void clearFields() {
        streetEditText.setText("");
        cityEditText.setText("");
        countryEditText.setText("");
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}
