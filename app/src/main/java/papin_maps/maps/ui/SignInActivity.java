package papin_maps.maps.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import papin_maps.maps.R;
import papin_maps.maps.core.BackManager;
import papin_maps.maps.presenter.SignInPres;

/**
 * Created by Papin on 19.10.2016.
 */

public class SignInActivity extends Activity implements BackManager.getLoginModelAnswer {

    private Button sigIn, signUp;
    private SharedPreferences sp;
    private MaterialEditText email;
    private MaterialEditText password;
    private String sEmail;
    private String sPassword;
    private AlertDialog dialog;
    private SignInPres signInPres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new SpotsDialog(this);
        setContentView(R.layout.sign_in_layout);
        sp = getSharedPreferences("Data", MODE_PRIVATE);
        email = (MaterialEditText) findViewById(R.id.email);
        password = (MaterialEditText) findViewById(R.id.password);
        sigIn = (Button) findViewById(R.id.butSignIn);
        signUp = (Button) findViewById(R.id.butSignUp);
        signInPres = new SignInPres();

        sigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                sEmail = email.getText().toString();
                sPassword = password.getText().toString();
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("EMAIL", sEmail);
                ed.putString("PASSWORD", sPassword);
                ed.commit();
                signInPres.login(sEmail, sPassword);
                //BackManager.getInstance().login(sEmail, sPassword, SignInActivity.this);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void getLoginModelAnswer(boolean answer) {
        if (answer) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            dialog.cancel();
        } else {
            dialog.cancel();
            Toast.makeText(this, "Wrond Email or Password", Toast.LENGTH_SHORT).show();
        }
    }
}
