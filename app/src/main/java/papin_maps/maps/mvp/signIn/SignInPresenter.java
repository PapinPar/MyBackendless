package papin_maps.maps.MVP.signIn;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class SignInPresenter implements BackManager.getLoginModelAnswer {

    private SigInInterface answer;


    public SignInPresenter(SigInInterface answer) {
        this.answer = answer;
    }

    public void trySignIn(String email, String password) {
        if (answer != null)
            BackManager.getInstance().login(email, password, SignInPresenter.this);
    }


    @Override
    public void getLoginModelAnswer(boolean ModelAnswer) {
        if (answer != null) {
            answer.sigInResponse(ModelAnswer);
        }
    }

    public void attach(SigInInterface view) {
        answer = view;
    }

    public void dettach() {
        answer = null;
    }
}
