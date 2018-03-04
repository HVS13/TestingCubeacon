package id.tiregdev.testingapps;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferUser;
import com.eyro.mesosfer.RegisterCallback;

import java.util.Locale;


public class Register extends AppCompatActivity {

    private ImageView iconLogo;
    private TextView iconTagline;
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText pass;
    private EditText confirmpass;
    private Button daftar;
    private TextView lupaPass;
    private String emails, password, firstname, lastname, konfirmpass;
    private AlertDialog dialog;

    private void findViews() {
        iconLogo = findViewById( R.id.icon_logo );
        iconTagline = findViewById( R.id.icon_tagline );
        email = findViewById( R.id.email );
        firstName = findViewById( R.id.firstName );
        lastName = findViewById( R.id.lastName );
        pass = findViewById( R.id.pass );
        confirmpass = findViewById( R.id.confirmpass );
        daftar = findViewById( R.id.daftar );
        lupaPass = findViewById( R.id.login );
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViews();
        login();
    }

    public void handleRegister() {
        // get all value from input
        emails = email.getText().toString();
        password = pass.getText().toString();
        firstname = firstName.getText().toString();
        lastname = lastName.getText().toString();
        konfirmpass = confirmpass.getText().toString();


        MesosferUser newUser = MesosferUser.createUser();
        // set default field
        newUser.setEmail(emails);
        newUser.setPassword(password);
        newUser.setFirstName(firstname);
        newUser.setLastName(lastname);
        // set custom field
        newUser.setData("dateOfBirth", konfirmpass);
        newUser.registerAsync(new RegisterCallback() {
            @Override
            public void done(MesosferException e) {
                // setup alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setNegativeButton(android.R.string.ok, null);

                // check if there is an exception happen
                if (e != null) {
                    builder.setTitle("Error Happen");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                    return;
                }

                email.setText("");
                email.setHint("email");
                pass.setText("");
                pass.setHint("pass");
                firstName.setText("");
                firstName.setHint("first name");
                lastName.setText("");
                lastName.setHint("last name");
                confirmpass.setText("");
                confirmpass.setHint("your dream");

                builder.setTitle("Register Succeeded");
                builder.setMessage("Thank you for registering.");
                dialog = builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Register.this, Login.class);
        startActivity(i);
    }

    public void login(){
        lupaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
            }
        });
    }

}
