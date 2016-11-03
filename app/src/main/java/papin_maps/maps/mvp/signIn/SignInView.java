package papin_maps.maps.MVP.signIn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import papin_maps.maps.MVP.maps.MainView;
import papin_maps.maps.MVP.registtration.RegistView;
import papin_maps.maps.R;
import papin_maps.maps.core.MyApplication;

/**
 * Created by Papin on 19.10.2016.
 */

public class SignInView extends Activity implements SigInInterface, View.OnClickListener {

    private Button sigIn, signUp;
    private SharedPreferences sp;
    private MaterialEditText email;
    private MaterialEditText password;
    private String sEmail;
    private String sPassword;
    private AlertDialog dialog;
    private SignInPresenter signInPresenter;

    private Tracker mTracker;
    private MyApplication application;

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
        signInPresenter = new SignInPresenter(SignInView.this);

        sigIn.setOnClickListener(this);
        signUp.setOnClickListener(this);

        application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SignInView");
        mTracker.send(new HitBuilders.EventBuilder()
                .setAction("Create")
                .setCategory("View")
                .build());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butSignIn:
                dialog.show();
                sEmail = email.getText().toString();
                sPassword = password.getText().toString();
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("EMAIL", sEmail);
                ed.putString("PASSWORD", sPassword);
                ed.commit();
                signInPresenter.trySignIn(sEmail, sPassword);
                break;
            case R.id.butSignUp:
                Intent intent = new Intent(SignInView.this, RegistView.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void sigInResponse(boolean response, String error) {
        if (response) {
            Intent intent = new Intent(this, MainView.class);
            startActivity(intent);
            dialog.cancel();
        } else {
            dialog.cancel();
            Toast.makeText(this, " "+error, Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
    }
}
