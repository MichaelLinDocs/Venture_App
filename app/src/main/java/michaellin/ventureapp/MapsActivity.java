package michaellin.ventureapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap venture_map;
    Intent intent_buffer;
    LatLng location_buffer;
    LatLng current_location;
    Coordinates coordinates_buffer;
    ArrayList<Coordinates> marker_coordinates;
    Random coordinates_generator;
    LatLng event_location_stub;
    Marker marker_stub;

    MarkerOptions markerOptions = new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        marker_coordinates = new ArrayList<>();
        intent_buffer = new Intent(this, EventActivity.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        venture_map = googleMap;
        event_location_stub = new LatLng(33.93777,-84.52006);
        marker_stub = venture_map.addMarker(new MarkerOptions().position(event_location_stub).title("Classroom"));
        current_location = getCurrentLocation();

        venture_map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().equals("Classroom"))
                {
                    startActivity(intent_buffer);
                }
                return false;
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(venture_map != null)
        {
            current_location = getCurrentLocation();
            event_location_stub = new LatLng(33.93777,-84.52006);
            marker_stub = venture_map.addMarker(new MarkerOptions().position(event_location_stub).title("Classroom"));
            intent_buffer = new Intent(this, EventActivity.class);
        }
    }

    public void createEvent(MenuItem menuItem)
    {
        current_location = getCurrentLocation();
        if(current_location != null)
        {
            for(int i = 0; i < 20; i++)
            {
                coordinates_generator = new Random();
                coordinates_buffer = new Coordinates(-0.005 + current_location.latitude + 0.01*coordinates_generator.nextDouble(), -0.005 + current_location.longitude + 0.01*coordinates_generator.nextDouble(), "" + coordinates_generator.nextDouble());
                marker_coordinates.add(coordinates_buffer);
                Log.d("OUTPUT", "Marker Added");
            }
        }
    }

    public void findEvents(MenuItem menuItem)
    {
        if(marker_coordinates != null && !marker_coordinates.isEmpty())
            for(Coordinates coordinates : marker_coordinates)
            {
                location_buffer = new LatLng(coordinates.latitude, coordinates.longitude);
                drawMarker(location_buffer, coordinates.title);
            }
    }

    private void drawMarker(LatLng point, String title)
    {
        markerOptions.position(point);
        markerOptions.title(title);
        venture_map.addMarker(markerOptions);
    }

    public LatLng getCurrentLocation()
    {
        LatLng result = new LatLng(0,0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            if(!venture_map.isMyLocationEnabled()) {
                venture_map.setMyLocationEnabled(true);
                Log.d("OUTPUT", "LOCATION_ENABLED " + venture_map.isMyLocationEnabled());
            }

            LocationManager location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location current_location = location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(current_location == null)
            {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = location_manager.getBestProvider(criteria, true);
                current_location = location_manager.getLastKnownLocation(provider);
            }

            if(current_location != null)
            {
                result = new LatLng(current_location.getLatitude(), current_location.getLongitude());
            }

        }

        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelableArrayList("marker_coordinates", marker_coordinates);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        marker_coordinates = savedInstanceState.getParcelableArrayList("marker_coordinates");

        if(marker_coordinates != null && !marker_coordinates.isEmpty())
            for(Coordinates coordinate : marker_coordinates)
            {
                location_buffer = new LatLng(coordinate.latitude, coordinate.longitude);
                drawMarker(location_buffer, coordinate.title);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
