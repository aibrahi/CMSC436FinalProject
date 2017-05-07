package umd.cmsc436.cmsc436finalproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ahmedinibrahim on 5/7/17.
 */

public class UpdateProfileFragment extends Fragment {

    public static final int GALLERY_PICTURE = 1;
    public static final int CAMERA_REQUEST = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_CAMERA = 3;


    private Button btnChangeEmail;
    private Button btnChangePassword;
    private Button changeEmail;
    private Button changePassword;
    private Button takePictureButton;
    private Button btnChangeName;
    private Button changeUsername;

    private File photoFile;

    private ImageView userImage;

    private EditText usersName, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mFirebaseStorage;
    private FirebaseUser user;

    private Uri downloadUrl;

    private String chatRoomID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        chatRoomID = getArguments().getString("chatRoomID");


    }

    public static UpdateProfileFragment newInstance( String chatRoomID) {

        Bundle args = new Bundle();
        args.putString("chatRoomID", chatRoomID);

        UpdateProfileFragment fragment = new UpdateProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updateprofile, null);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance().getReference();


        userImage = (ImageView) view.findViewById(R.id.userImage);


        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                }
            }
        };

        // get the image for the user
        mFirebaseDatabase.getReference().child("Users").child(user.getUid()).child("photourl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("URL -->", dataSnapshot.getValue().toString());

                StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString());


                Glide.with(getApplicationContext() /* context */)
                        .using(new FirebaseImageLoader())
                        .load(imageStorageReference)
                        .into(userImage);


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnChangeEmail = (Button) view.findViewById(R.id.change_email_button);
        btnChangePassword = (Button) view.findViewById(R.id.change_password_button);
        btnChangeName = (Button) view.findViewById(R.id.change_username_button);

        changeEmail = (Button) view.findViewById(R.id.changeEmail);
        changePassword = (Button) view.findViewById(R.id.changePass);
        changeUsername = (Button) view.findViewById(R.id.changeUserName);

        takePictureButton = (Button) view.findViewById(R.id.change_picture_button);

        usersName = (EditText) view.findViewById(R.id.usersName);
        newEmail = (EditText) view.findViewById(R.id.new_email);
        newPassword = (EditText) view.findViewById(R.id.newPassword);

        usersName.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);

        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        changeUsername.setVisibility(View.GONE);


        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersName.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);

                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                changeUsername.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {


                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();

                                        // Update User database as well
                                        mFirebaseDatabase.getReference().child("Users").child(user.getUid()).child("email").setValue(newEmail.getText().toString().trim());

                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });


                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersName.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                changeUsername.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);

                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersName.setVisibility(View.VISIBLE);
                changeUsername.setVisibility(View.VISIBLE);

                newEmail.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
            }
        });

        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !usersName.getText().toString().equals("")) {
                    if (usersName.getText().toString().trim().length() < 3) {
                        usersName.setError("Username too short, enter minimum 3 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(usersName.getText().toString())
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "User profile updated.", Toast.LENGTH_SHORT).show();

                                            resetVisibility();
                                            // Update User database as well
                                            mFirebaseDatabase.getReference().child("Users").child(user.getUid()).child("displayName").setValue(usersName.getText().toString());
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setTitle("Picture Option");
                myAlertDialog.setMessage("Where would you like to take your picture from?");

                myAlertDialog.setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PICTURE);

                            }
                        });

                myAlertDialog.setNegativeButton("Camera",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {



                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                photoFile = getPhotoFile();

                                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);

                                }

                            }
                        });

                myAlertDialog.show();

            }
        });

        return view;

    }


    public void resetVisibility(){
        usersName.setVisibility(View.GONE);
        usersName.setText("");
        newEmail.setVisibility(View.GONE);
        newEmail.setText("");
        newPassword.setVisibility(View.GONE);
        newPassword.setText("");
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        changeUsername.setVisibility(View.GONE);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private File getPhotoFile(){
        File externalPhotoDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalPhotoDir == null){
            return null;
        }

        return new File(externalPhotoDir, "IMG_"+ System.currentTimeMillis()+".jpg");
    }

    //sign out method
    public void signOut() {

        auth.removeAuthStateListener(authListener);
        auth.signOut();
        Intent myIntent = new Intent(getApplicationContext(), FragmentViewer.class);
        myIntent.putExtra("ChatRoomID", chatRoomID);
        startActivity(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), UsersChatRoomActivity.class);
        myIntent.putExtra("ChatRoomID", chatRoomID);
        startActivity(myIntent);
        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("BITTTMAP -->", "CAME HERE");

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.d("BITTTMAP -->", imageBitmap.toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] databaos = baos.toByteArray();

            StorageReference mountainsRef = mFirebaseStorage.child("IMG_"+ System.currentTimeMillis()+".jpg");

            UploadTask uploadTask = mountainsRef.putBytes(databaos);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl.toString());

                    Glide.with(getApplicationContext() /* context */)
                            .using(new FirebaseImageLoader())
                            .load(imageStorageReference)
                            .into(userImage);

                    // Update User database as well
                    mFirebaseDatabase.getReference().child("Users").child(user.getUid()).child("photourl").setValue(downloadUrl.toString());

                }
            });


        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] databaos = baos.toByteArray();

                    StorageReference mountainsRef = mFirebaseStorage.child("IMG_"+ System.currentTimeMillis()+".jpg");


                    UploadTask uploadTask = mountainsRef.putBytes(databaos);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl.toString());

                            Glide.with(getApplicationContext() /* context */)
                                    .using(new FirebaseImageLoader())
                                    .load(imageStorageReference)
                                    .into(userImage);

                            // Update User database as well
                            mFirebaseDatabase.getReference().child("Users").child(user.getUid()).child("photourl").setValue(downloadUrl.toString());

                        }
                    });



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}

