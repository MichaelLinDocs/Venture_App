package michaellin.venture_10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    ImageButton image_button;
    EditText input_title, input_address, input_datetime, input_description;
    LinearLayout event_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        image_button = findViewById(R.id.create_imagebutton);
        event_layout = findViewById(R.id.event_linear_layout);
        input_title = findViewById(R.id.input_title);
        input_address = findViewById(R.id.input_address);
        input_datetime = findViewById(R.id.input_datetime);
        input_description = findViewById(R.id.input_description);

        event_layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    image_button.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                else
                    image_button.setVisibility(View.GONE);
            }
        });
    }

    public void cancel(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivity(this.getParentActivityIntent());
        }
    }

    public void uploadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            image_button.setImageBitmap(bitmap);
        }
    }

    private Location getCurrentLocation() {

        Location result_location = null;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateEventActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        LocationManager location_manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        CurrentLocationListener location_listener = new CurrentLocationListener();
        assert(location_manager != null);

        if(location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, location_listener);
            result_location = location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (result_location == null && location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            location_manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, location_listener);
            result_location = location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (result_location == null && location_manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            location_manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, location_listener);
            result_location = location_manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        location_manager.removeUpdates(location_listener);

        return result_location;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void createEvent(View view)
    {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Location current_location = getCurrentLocation();
                FirebaseAuth authenticator = FirebaseAuth.getInstance();
                FirebaseUser current_user = authenticator.getCurrentUser();

                if(current_location == null)
                {
                    Toast.makeText(getApplicationContext(), "Location Seek Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(current_user == null){
                    Toast.makeText(getApplicationContext(),"Must Be Logged In", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(input_title.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Must Input Title", Toast.LENGTH_SHORT).show();
                    return;
                }

                String user_id = current_user.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference entry_reference = database.getReference("events");

                LatLng coordinates = new LatLng(current_location.getLatitude(), current_location.getLongitude());
                VentureEvent event_entry = new VentureEvent(input_title.getText().toString(),
                        input_address.getText().toString(),
                        input_datetime.getText().toString(),
                        input_description.getText().toString(),
                        coordinates.latitude,
                        coordinates.longitude,
                        user_id);

                Map<String, Object> event_entry_values = event_entry.toMap();

                entry_reference.child(user_id).setValue(event_entry_values);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storage_reference = storage.getReference("events/" + user_id + ".jpg");
                storage_reference.putBytes(getImageData());
            }
        }).start();

        startActivity(getParentActivityIntent());
    }

    private byte[] getImageData(){
        image_button.setDrawingCacheEnabled(true);
        image_button.buildDrawingCache();
        Bitmap bitmap = image_button.getDrawingCache();

        ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_stream);

        return output_stream.toByteArray();
    }
}