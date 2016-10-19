package papin_maps.maps.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import papin_maps.maps.R;
import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 19.10.2016.
 */

public class SignInActivity extends Activity implements BackManager.getLoginAnswer {

    private Button sigIn;
    private SharedPreferences sp;
    private MaterialEditText email;
    private MaterialEditText password;
    private String sEmail;
    private String sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);
        sp = getSharedPreferences("Data", MODE_PRIVATE);
        email = (MaterialEditText)findViewById(R.id.email);
        password = (MaterialEditText)findViewById(R.id.password);
        sigIn = (Button) findViewById(R.id.butSignIn);
        sigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = email.getText().toString();
                sPassword = password.getText().toString();
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("EMAIL", sEmail);
                ed.putString("PASSWORD", sPassword);
                ed.commit();
                BackManager.getInstance().login(sEmail, sPassword, SignInActivity.this);
            }
        });

    }


    @Override
    public void getLoginANswer(boolean answer) {
        if (answer){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "Wrond Email or Password", Toast.LENGTH_SHORT).show();
    }
}
