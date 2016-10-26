package papin_maps.maps.MVP.signIn;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class SignInPresenter implements BackManager.getLoginModelAnswer {

    private InterfaceSighIn answer;


    public SignInPresenter(InterfaceSighIn answer) {
        this.answer = answer;
    }

    public void backAnswer(String email, String password) {
        if (answer != null)
            BackManager.getInstance().login(email, password, SignInPresenter.this);
    }


    @Override
    public void getLoginModelAnswer(boolean ModelAnswer) {
        if (answer != null) {
            answer.sigInRespone(ModelAnswer);
        }
    }

    public void attach(InterfaceSighIn view) {
        answer = view;
    }

    public void dettach() {
        answer = null;
    }
}
