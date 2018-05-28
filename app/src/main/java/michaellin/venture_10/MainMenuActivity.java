package michaellin.venture_10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainMenuActivity extends AppCompatActivity {

    private FirebaseAuth authenticator = FirebaseAuth.getInstance();
    ListView menu_listview;

    String[] menu_items = {"View Map", "Register an Event", "Delete my Event", "Log Out"};
    ArrayAdapter<String> menu_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        LocationManager location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        assert location_manager != null;
        if(!location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }

        menu_listview = findViewById(R.id.menu_list);
        menu_adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.menu_text_config,menu_items);

        menu_listview.setAdapter(menu_adapter);

        menu_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                FirebaseUser current_user = authenticator.getCurrentUser();

                switch(i)
                {
                    case 0:
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        if(current_user == null)
                        {
                            Toast.makeText(getApplicationContext(), "Cannot Register Event: User Offline", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        deleteData(current_user);
                        break;
                    case 3:
                        authenticator.signOut();
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void deleteData(FirebaseUser current_user)
    {
        if(current_user != null) {
            String user_id = current_user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            try {
                DatabaseReference database_reference = database.getReference("events/" + user_id);
                StorageReference storage_reference = storage.getReference("events/" + user_id + ".jpg");
                database_reference.setValue(null);
                storage_reference.delete();
            }
            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "User Data Not Found",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
