package papin_maps.maps.MVP.registtration;

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;

import java.io.IOException;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class RegistPresenter implements BackManager.getRegistResponseModel {

    private RegistInterface answer;


    public RegistPresenter(RegistInterface interfaceRegistration) {
        this.answer = interfaceRegistration;
    }

    public void tryRegistration(String sEmail, String sPassword) {
        try {
            if (answer != null)
                BackManager.getInstance().register(sEmail, sPassword, RegistPresenter.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getRegistAnswerCompleted(BackendlessUser respone) throws IOException {
        if (answer != null)
            answer.RegistComplete(respone);

    }

    @Override
    public void getRegistAnswerFail(BackendlessFault fault) throws IOException {
        if (answer != null)
            answer.RegistFail(fault);
    }

    public void attach(RegistInterface view) {
        answer = view;
    }

    public void dettach() {
        answer = null;
    }
}
