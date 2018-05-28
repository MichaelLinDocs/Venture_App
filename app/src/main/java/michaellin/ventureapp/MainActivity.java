package michaellin.ventureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Intent intent_buffer;
    EditText editText_buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loginToVenture(View view)
    {
        intent_buffer = new Intent(this, MapsActivity.class);
        startActivity(intent_buffer);
    }

    public void clickUsername(View view)
    {
        editText_buffer = (EditText)findViewById(R.id.editText_username);
        editText_buffer.setText("");
    }

    public void clickPassword(View view)
    {
        editText_buffer = (EditText)findViewById(R.id.editText_password);
        editText_buffer.setText("");
    }
}
