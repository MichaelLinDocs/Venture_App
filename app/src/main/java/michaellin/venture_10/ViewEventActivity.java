package michaellin.venture_10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;

public class ViewEventActivity extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    TextView view_title;
    TextView view_address;
    TextView view_datetime;
    TextView view_description;

    ImageView view_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        String user_id = getIntent().getStringExtra("user_id");

        DatabaseReference database_reference = database.getReference("events/" + user_id);
        StorageReference storage_reference = storage.getReference().child("events/" + user_id + ".jpg");

        view_title = findViewById(R.id.view_title);
        view_address = findViewById(R.id.view_address);
        view_datetime = findViewById(R.id.view_datetime);
        view_description = findViewById(R.id.view_description);

        view_image = findViewById(R.id.view_image);

        database_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VentureEvent event = dataSnapshot.getValue(VentureEvent.class);

                if(event != null) {
                    view_title.setText(event.title);
                    view_address.setText(event.address);
                    view_datetime.setText(event.datetime);
                    view_description.setText(event.description);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        final long TWO_MEGABYTES = 1024*1024*2;
        storage_reference.getBytes(TWO_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                view_image.setImageBitmap(bitmap);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void returnToMap(View view)
    {
        startActivity(getParentActivityIntent());
    }
}
