package papin_maps.maps.presenter;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class SignInPres implements BackManager.getLoginModelAnswer {

    public interface getLoginPresAnswer{
        void getLoginPresAnswer(boolean answer);
    }

    public void login(String sEmail, String sPassword) {
        BackManager.getInstance().login(sEmail, sPassword, this);
    }


    @Override
    public void getLoginModelAnswer(boolean answer) {

    }
}
