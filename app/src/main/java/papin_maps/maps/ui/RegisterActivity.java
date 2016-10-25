package papin_maps.maps.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import papin_maps.maps.R;
import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class RegisterActivity extends Activity implements BackManager.registrationAnswer{

   private Button registration;
   private MaterialEditText email, password;
   private String sEmail,sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        password = (MaterialEditText)findViewById(R.id.passwordRegist);
        email = (MaterialEditText) findViewById(R.id.emailRegist);

        registration = (Button) findViewById(R.id.butRegister);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = email.getText().toString();
                sPassword = password.getText().toString();
                BackManager.getInstance().register(sEmail,sPassword,RegisterActivity.this);
            }
        });
    }

    @Override
    public void getRegistAnswer(BackendlessUser respone) throws IOException {
        Toast.makeText(this, "Registration compelted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void getRegistAnswer(BackendlessFault fault) throws IOException {
        Toast.makeText(this, ""+fault.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
