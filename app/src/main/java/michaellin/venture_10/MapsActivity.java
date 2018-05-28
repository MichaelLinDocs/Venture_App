package michaellin.venture_10;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @SuppressWarnings("FieldCanBeLocal")
    private GoogleMap venture_map;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Location current_location;
    double latitude, longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(venture_map != null)
        {
            retrieveMarkers();
            configureMapInfo();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        venture_map = googleMap;

        current_location = getCurrentLocation();

        if(current_location != null)
        {
            latitude = current_location.getLatitude();
            longitude = current_location.getLongitude();
        }

        CameraUpdateFactory.zoomBy(10);
        venture_map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

        retrieveMarkers();
        configureMapInfo();
    }

    protected void configureMapInfo()
    {
        venture_map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String extra = (String)marker.getTag();
                Intent intent = new Intent(getApplicationContext(), ViewEventActivity.class);
                intent.putExtra("user_id", extra);
                startActivity(intent);
            }});
    }

    protected void retrieveMarkers()
    {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();

                DatabaseReference database_reference = database.getReference("events");

                database_reference.orderByKey().addChildEventListener(new ChildEventListener() {

                    MarkerOptions marker_data_buffer;
                    LatLng coordinates_buffer;

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        VentureEvent event = dataSnapshot.getValue(VentureEvent.class);
                        assert event!= null;

                        coordinates_buffer = new LatLng(event.latitude, event.longitude);
                        marker_data_buffer = new MarkerOptions();
                        marker_data_buffer.position(coordinates_buffer).title(event.title).snippet(event.datetime);
                        venture_map.addMarker(marker_data_buffer).setTag(event.user_id);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }).start();
    }

    private Location getCurrentLocation() {

        Location result_location = null;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if(!venture_map.isMyLocationEnabled()){
            venture_map.setMyLocationEnabled(true);
        }

        LocationManager location_manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        CurrentLocationListener location_listener = new CurrentLocationListener();
        assert(location_manager != null);

        if(location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            result_location = location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (result_location == null && location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            result_location = location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (result_location == null && location_manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            result_location = location_manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return result_location;
    }
}
