package papin_maps.maps.signIn;

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
import papin_maps.maps.ui.MainActivity;
import papin_maps.maps.registration.RegisterActivity;

/**
 * Created by Papin on 19.10.2016.
 */

public class SignInActivity extends Activity implements MyPresenter.myAnswer {

    private Button sigIn, signUp;
    private SharedPreferences sp;
    private MaterialEditText email;
    private MaterialEditText password;
    private String sEmail;
    private String sPassword;
    private AlertDialog dialog;

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
        final MyPresenter s = new MyPresenter(SignInActivity.this);

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
                s.backAnswer(sEmail, sPassword);
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
    public void Answer(boolean mynAswer) {
        if (mynAswer) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            dialog.cancel();
        } else {
            dialog.cancel();
            Toast.makeText(this, "Wrond Email or Password", Toast.LENGTH_SHORT).show();
        }
    }


    //@Override
    //public void getLoginModelAnswer(boolean answer) {
    //    if (answer) {
    //        Intent intent = new Intent(this, MainActivity.class);
    //        startActivity(intent);
    //        dialog.cancel();
    //    } else {
    //        dialog.cancel();
    //        Toast.makeText(this, "Wrond Email or Password", Toast.LENGTH_SHORT).show();
    //    }
    //}


}
