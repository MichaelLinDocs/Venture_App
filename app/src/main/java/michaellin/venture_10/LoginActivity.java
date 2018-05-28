package michaellin.venture_10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth authenticator = FirebaseAuth.getInstance();
    LinearLayout login_layout;
    EditText input_email;
    EditText input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        input_email = findViewById(R.id.edit_email);
        input_password = findViewById(R.id.edit_password);
        login_layout = findViewById(R.id.login_layout);

        login_layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser current_user = authenticator.getCurrentUser();
        if(current_user != null)
            updateUI(current_user);
    }

    protected void updateUI(FirebaseUser current_user) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("USER_ID", current_user.getUid());
        startActivity(intent);
    }

    public void attemptLogin(View view) {
        String email, password;
        email = input_email.getText().toString();
        password = input_password.getText().toString();

        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
            return;
        }


        authenticator.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser current_user = authenticator.getCurrentUser();
                            updateUI(current_user);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp(View view) {

        final EditText signup_email, signup_password;

        @SuppressLint("InflateParams") final View signup_view = LayoutInflater.from(this).inflate(R.layout.signup_dialog, null);
        AlertDialog.Builder signup_dialog = new AlertDialog.Builder(this);
        signup_dialog.setView(signup_view);

        signup_email = signup_view.findViewById(R.id.signup_input_email);
        signup_password = signup_view.findViewById(R.id.signup_input_password);

        signup_dialog.setCancelable(false)
                .setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = signup_email.getText().toString();
                        String password = signup_password.getText().toString();

                        if(password.length() < 6)
                        {
                            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                        else if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            authenticator.createUserWithEmailAndPassword(email, password).
                                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                FirebaseUser new_user = authenticator.getCurrentUser();
                                                updateUI(new_user);
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Invalid Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        signup_dialog.create();
        signup_dialog.show();
    }
}
