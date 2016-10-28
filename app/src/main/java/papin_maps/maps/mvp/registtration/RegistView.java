package papin_maps.maps.MVP.registtration;

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

/**
 * Created by Papin on 25.10.2016.
 */

public class RegistView extends Activity implements RegistInterface {

    private Button registration;
    private MaterialEditText email, password;
    private String sEmail, sPassword;
    private RegistPresenter registerRespone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        password = (MaterialEditText) findViewById(R.id.passwordRegist);
        email = (MaterialEditText) findViewById(R.id.emailRegist);
        registerRespone = new RegistPresenter(RegistView.this);

        registration = (Button) findViewById(R.id.butRegister);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = email.getText().toString();
                sPassword = password.getText().toString();
                if (sEmail.length() > 0 && sPassword.length() > 0)
                    registerRespone.tryRegistration(sEmail, sPassword);
                else
                    Toast.makeText(RegistView.this, "Please input data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void RegistComplete(BackendlessUser respone) throws IOException {
        Toast.makeText(this, "Registration compelted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void RegistFail(BackendlessFault fault) throws IOException {
        Toast.makeText(this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerRespone.attach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        registerRespone.dettach();
    }
}