package com.example.commutersafety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MarkZone extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    ImageView i1, i2;
    Button bt1;
    EditText et1, et2;
    Spinner sp;

    private Uri uri;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = "CapturePicture";
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private ImageView image;
    private String pictureFilePath;
    private FirebaseStorage firebaseStorage;
    private String deviceIdentifier;
    private FirebaseAuth auth;
    private ProgressDialog mProgress;
    Bitmap imageBitmap;
    byte[] dataBAOS;
    int rresultCode;
    DatabaseReference databaseReference, databaseReference1;
    public String zoneImageURI = null;
    String ZoneTitle;
    String uid;

    LocationManager locationManager;
    String lattitude,longitude;
    public String Lat , Logg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_zone);

        databaseReference = FirebaseDatabase.getInstance().getReference("Zones");
        //databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Zones");
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        i1 = findViewById(R.id.imageView1_markzone);
        i2 = findViewById(R.id.imageView2_markzone);
        bt1 = findViewById(R.id.button_markzone);
        et1 = findViewById(R.id.editText_description);
        et2 = findViewById(R.id.editText_solution);
        sp = findViewById(R.id.spinner_markzone);


        mProgress = new ProgressDialog(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unsafezone, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        bt1.setOnClickListener(this);
        i1.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view==bt1){
            final String ZoneData = et1.getText().toString().trim();
            final String ZoneSolution = et2.getText().toString().trim();
            final String ZoneImage = zoneImageURI;


            if (TextUtils.isEmpty(ZoneData)) {
                Toast.makeText(MarkZone.this, "Please Enter The Zone Description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(ZoneSolution)) {
                Toast.makeText(MarkZone.this, "Please Enter The Zone Solution", Toast.LENGTH_SHORT).show();
                return;
            }

            if(dataBAOS.equals("")){
                Toast.makeText(MarkZone.this, "Please Capture The Image First", Toast.LENGTH_SHORT).show();
                return;
            }


            Log.d("bataBAOS0", String.valueOf(dataBAOS[0]));
            mProgress.setMessage("Uploading your Status...");
            mProgress.show();
            StorageReference mStorage = FirebaseStorage.getInstance().getReference()
                    .child(auth.getUid())
                    .child("" + new Date().getTime());
            UploadTask uploadTask = mStorage.putBytes(dataBAOS);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Sending failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png' in uri
                            zoneImageURI = uri.toString();
                            System.out.println(uri.toString());
                            Log.d("ZONE",zoneImageURI);
                            String url = zoneImageURI;
                            System.out.println("The url passed in addnames is   :" + url);


                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                buildAlertMessageNoGps();
                            }
                            getLocation();

                            final String ZoneLat = Lat;
                            final String ZoneLong = Logg;
                            String ZoneKey = databaseReference.push().getKey();

                            Zone zn = new Zone(auth.getUid()+ new Random().nextInt(1000), ZoneTitle, ZoneData, ZoneSolution,
                                    ZoneLat, ZoneLong,0,0 ,0, zoneImageURI);
                            databaseReference.child(ZoneKey).setValue(zn);
                            addData();

                            //;setValue(hm);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("imageUri",exception.toString());
                            // Handle any errors
                        }
                    });
                    //Log.d("ZONE",zoneImageURI);
                    // Log.d("imageUri", zoneImageURI);

                }


            });
        }
        if(view==i1){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void addData() {
        databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Zones");
        String ZoneKey1 = databaseReference1.push().getKey();
        User_Zones un = new User_Zones(uid,ZoneKey1,zoneImageURI);
        databaseReference1.child(ZoneKey1).setValue(un);

        //HashMap<String,String,String> hm = new HashMap<String, String>();
        //Log.d("HM",hm.toString());
        //hm.put(ZoneKey1,uid,zoneImageURI);
        //databaseReference1.child(ZoneKey1).child(hm).setValue();

        Toast.makeText(MarkZone.this, " All The Details Added Successfully", Toast.LENGTH_SHORT);
        startActivity(new Intent(MarkZone.this,MapsActivity.class));
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /*super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        i2.setImageBitmap(bitmap);*/
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            dataBAOS = baos.toByteArray();
            rresultCode = resultCode;
            i2.setImageBitmap(imageBitmap);

            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        ZoneTitle = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    protected void buildAlertMessageNoGps() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MarkZone.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MarkZone.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MarkZone.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;



            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;

            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }
}