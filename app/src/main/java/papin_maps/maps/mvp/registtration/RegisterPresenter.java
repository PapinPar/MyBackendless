package papin_maps.maps.MVP.registtration;

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;

import java.io.IOException;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class RegisterPresenter implements BackManager.registrationAnswer {

    private InterfaceRegistration answer;


    public RegisterPresenter(InterfaceRegistration interfaceRegistration) {
        this.answer = interfaceRegistration;
    }

    public void getRegister(String sEmail, String sPassword) {
        try {
            if (answer != null)
                BackManager.getInstance().register(sEmail, sPassword, RegisterPresenter.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getRegistAnswer(BackendlessUser respone) throws IOException {
        if (answer != null)
            answer.getRegistAnswer(respone);

    }

    @Override
    public void getRegistAnswer(BackendlessFault fault) throws IOException {
        if (answer != null)
            answer.getRegistAnswer(fault);
    }

    public void attach(InterfaceRegistration view) {
        answer = view;
    }

    public void dettach() {
        answer = null;
    }
}
