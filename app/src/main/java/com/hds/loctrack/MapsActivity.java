package com.hds.loctrack;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Creating objects
    private DatabaseReference databaseReference;
    private DatabaseReference dbref;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    private EditText editTextLatitude;
    private EditText editTextLongitude;
    private Button readbutton;
    private String main_lat;
    private String main_long;

//    private Context mContext;
//    private Resources mResources;

    private static final String CHANNEL_ID = "01";
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createNotificationChannel();
        readbutton = (Button) findViewById(R.id.button3);
        //Creating a Bitmap
        Bitmap bg = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);

        //Setting the Bitmap as background for the ImageView
        ImageView i = (ImageView) findViewById(R.id.imageView);
        i.setBackgroundDrawable(new BitmapDrawable(bg));

        //Creating the Canvas Object
        Canvas canvas = new Canvas(bg);

        //Creating the Paint Object and set its color & TextSize
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        //Drawing Square around text boxes
        canvas.drawRect(65, 1040, 655, 1225, paint);

//        canvas.drawRect(430, 1070, 655, 1200, paint);

        //Requesting Permissions from the User
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        //Finding Latitude and Longitude
        editTextLatitude = findViewById(R.id.editText1Lat);

        editTextLongitude = findViewById(R.id.editText2Long);

        // Creating Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Location");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String databaseLatituteString = snapshot.child("latitude").getValue().toString().substring(1,snapshot.child("latitude").getValue().toString().length() -1);
                    String databaseLongituteString = snapshot.child("longitude").getValue().toString().substring(1,snapshot.child("longitude").getValue().toString().length() -1);

                    String[] stringLat = databaseLatituteString.split(", ");
                    Arrays.sort(stringLat);
                    String latitude = stringLat[stringLat.length-1].split("=")[1];

                    String[] stringLong = databaseLongituteString.split(", ");
                    Arrays.sort(stringLong);
                    String longitude = stringLong[stringLong.length-1].split("=")[1];

                    String latitude_m = stringLat[stringLat.length-2].split("=")[1];
                    String longitude_m = stringLong[stringLong.length-1].split("=")[1];

                    main_lat = latitude_m;
                    main_long = longitude_m;

                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + " , " + longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    editTextLatitude.setText(Double.toString(location.getLatitude()));
                    editTextLongitude.setText(Double.toString(location.getLongitude()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
            @Override
            public void onProviderEnabled(String s) {

            }
            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , MIN_TIME, MIN_DIST, locationListener);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    public void notifyNow(){
        Intent intent = new Intent(this, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("LocTrack Alert!")
                .setContentText("Latitude and Longitude Saved to Database")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notif.notify(6934,builder.build());
//        Intent intent = new Intent(this,ResultActivity.class);
//        PendingIntent pending = PendingIntent.getActivity(this,0,intent,0);
//
//
//        Notification notif = new Notification.Builder(this)
//                .setContentTitle("New Message:")
//                .setContentText("Location saved to Database").setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pending).build();
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notif.flags |= Notification.FLAG_AUTO_CANCEL;
//        manager.notify(0,notif);

    }

    public void updateButtonOnCLick(View view) {

        databaseReference.child("latitude").push().setValue(editTextLatitude.getText().toString());
        databaseReference.child("longitude").push().setValue(editTextLongitude.getText().toString());
        //Send notification
        notifyNow();

//        mContext = getApplicationContext();
//        mResources = getResources();
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
//        builder.setSmallIcon(R.drawable.ic_launcher_background);
////        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.ic_launcher_foreground);
////        val bitmap = (ResourcesCompat.getDrawable(this.resources, R.mipmap.ic_launcher, null) as BitmapDrawable).bitmap
////        builder.setLargeIcon(bitmap);
//        builder.setContentTitle("Notification Title");
//        builder.setContentText("Hello! Notification service.");
//        int notificationId = 001;
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(notificationId, builder.build());

    }

    public void updateButtonOnCLickCurrent(View view) {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    databaseReference.child("latitude").push().setValue(location.getLatitude());
                    databaseReference.child("longitude").push().setValue(location.getLongitude());

                    //Send notification
                    notifyNow();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
            @Override
            public void onProviderEnabled(String s) {

            }
            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , MIN_TIME, MIN_DIST, locationListener);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showToast(String val){
        // Creating a Toast
        LayoutInflater inf = getLayoutInflater();
        View layout = inf.inflate(R.layout.toast_layout,(ViewGroup) findViewById(R.id.toast_root));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

    public void readdbButtonI(View view){
//        dbref = FirebaseDatabase.getInstance().getReference().child("Location");
        Log.d("updatedb",main_lat+"\n"+main_long);
        // Create a Toast
        Toast mToast = Toast.makeText(getApplicationContext(),"Latitude : "+main_lat+"\nLongitude : "+main_long,Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
        View vieww=mToast.getView();
        TextView  view1 = vieww.findViewById(android.R.id.message);
        view1.setTextColor(Color.WHITE);
        vieww.setBackgroundResource(R.color.colorPrimaryDark);
        mToast.show();

        // Custom Toast, Text Value cannot be changed at this moment(There maybe some method)
////    showToast("Latitude : "+s_latitude+" Longitude : "+s_longitude);
//
////    Log.d("testPrintlat","Latitude : "+s_latitude+"\nLongitude : "+s_longitude+"\n"+lat+"\n"+longi);

    }

}