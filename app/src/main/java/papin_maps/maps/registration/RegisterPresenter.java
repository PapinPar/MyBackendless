package papin_maps.maps.registration;

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;

import java.io.IOException;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class RegisterPresenter implements BackManager.registrationAnswer{

    private RegisterAnswer registerAnswer;

    public interface RegisterAnswer{

        void getRegistAnswer(BackendlessUser respone) throws IOException;
        void getRegistAnswer(BackendlessFault fault) throws IOException;
    }

    public RegisterPresenter(RegisterAnswer registerAnswer) {
        this.registerAnswer = registerAnswer;
    }

    public void getRegister(String sEmail,String sPassword)
    {
        try {
            BackManager.getInstance().register(sEmail,sPassword,RegisterPresenter.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getRegistAnswer(BackendlessUser respone) throws IOException {
        registerAnswer.getRegistAnswer(respone);

    }

    @Override
    public void getRegistAnswer(BackendlessFault fault) throws IOException {
        registerAnswer.getRegistAnswer(fault);
    }
}
