package com.example.mobilewireless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class InsertDetailsActivity extends AppCompatActivity {

    private EditText name;
    private Button btnconfirm, choosebtn;
    String emailAdd;
    Intent intent;
    FirebaseAuth mFirebaseAuth;
    private DatabaseReference databaseReference;
    String currentUserId;
    public Uri imguri = null;
    ImageView picture;

    private StorageTask uploadTask;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Insert Details");

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserId);

        name = findViewById(R.id.nameText);
        btnconfirm = findViewById(R.id.confirmbtn);
        choosebtn = findViewById(R.id.chooseBtn);
        picture = findViewById(R.id.imageView);

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupUser();
            }
        });

        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });
    }

    public void SetupUser(){
        mStorageRef = FirebaseStorage.getInstance().getReference("UserImage").child(currentUserId);
        final String Name = name.getText().toString().trim();

        if(TextUtils.isEmpty(Name)){
            name.setError("Please enter your first name");
            name.requestFocus();
            return;
        }

        if(Name.length() > 10){
            name.setError("Please enter your first name not more than 10 characters");
            name.requestFocus();
            return;
        }

        if (imguri != null) {
            final String getEmail = mFirebaseAuth.getCurrentUser().getEmail();
            Toast.makeText(InsertDetailsActivity.this, "Submitting", Toast.LENGTH_SHORT).show();

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getExtension(imguri));

            uploadTask = fileReference.putFile(imguri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("imageURL", mUri);
                        userMap.put("name", Name);
                        userMap.put("email", getEmail);
                        userMap.put("id", currentUserId);

                        databaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(InsertDetailsActivity.this,HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                    Toast.makeText(InsertDetailsActivity.this, "Information updated",Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(InsertDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(InsertDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(InsertDetailsActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void Filechooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            picture.setImageURI(imguri);
        }
    }
}
