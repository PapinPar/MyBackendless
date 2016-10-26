package papin_maps.maps.MVP.signIn;

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
import papin_maps.maps.MVP.maps.MainActivity;
import papin_maps.maps.MVP.registtration.RegisterActivity;

/**
 * Created by Papin on 19.10.2016.
 */

public class SignInActivity extends Activity implements InterfaceSighIn,View.OnClickListener {

    private Button sigIn, signUp;
    private SharedPreferences sp;
    private MaterialEditText email;
    private MaterialEditText password;
    private String sEmail;
    private String sPassword;
    private AlertDialog dialog;
    private SignInPresenter signInPresenter;

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

        signInPresenter = new SignInPresenter(SignInActivity.this);

        sigIn.setOnClickListener(this);
        signUp.setOnClickListener(this);

    }

    @Override
    public void sigInRespone(boolean response) {
        if (response) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            dialog.cancel();
        } else {
            dialog.cancel();
            Toast.makeText(this, "Wrond Email or Password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInPresenter.attach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        signInPresenter.dettach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.butSignIn:
                dialog.show();
                sEmail = email.getText().toString();
                sPassword = password.getText().toString();
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("EMAIL", sEmail);
                ed.putString("PASSWORD", sPassword);
                ed.commit();
                signInPresenter.backAnswer(sEmail, sPassword);
                break;
            case R.id.butSignUp:
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
