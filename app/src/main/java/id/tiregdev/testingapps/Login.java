package id.tiregdev.testingapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.LogInCallback;
import com.eyro.mesosfer.MesosferBeacon;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Login extends AppCompatActivity {

    EditText email, pass;
    Button login, register;
    private ProgressDialog loading;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findId();
        sessionUser();
    }

    public void findId(){
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.btnLogin);
        register =findViewById(R.id.daftar);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    public void handleLogin(){
        final String mail = email.getText().toString();
        final String pas = pass.getText().toString();

        if (TextUtils.isEmpty(mail)) {
            Toast.makeText(Login.this, "Username is empty", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pas)) {
            Toast.makeText(Login.this, "Password is empty", Toast.LENGTH_SHORT).show();
            pass.requestFocus();
            return;
        }

        loading.setMessage("Logging in...");
        loading.show();

        MesosferUser.logInAsync(mail, pas, new LogInCallback() {
            @Override
            public void done(MesosferUser user, MesosferException e) {
                loading.dismiss();
                if (e != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("Login Failed");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                    return;
                }

                Toast.makeText(Login.this, "User logged in...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, splash.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void sessionUser(){

        MesosferUser user = MesosferUser.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(Login.this, splash.class);
            startActivity(intent);
        } else {
            getBaseContext();
        }
    }

    @Override
    protected void onDestroy() {
        // dismiss any resource showing
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}