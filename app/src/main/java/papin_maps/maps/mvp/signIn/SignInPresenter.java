package papin_maps.maps.MVP.signIn;

import com.backendless.exceptions.BackendlessFault;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class SignInPresenter implements BackManager.getLoginModelResponse {

    private SigInInterface answer;


    public SignInPresenter(SigInInterface answer) {
        this.answer = answer;
    }

    public void trySignIn(String email, String password) {
        if (answer != null)
            BackManager.getInstance().login(email, password, SignInPresenter.this);
    }


    @Override
    public void getLoginModelResponse(boolean ModelAnswer, BackendlessFault error) {
        String errorMessage = null;
        if (answer != null) {
            if (ModelAnswer == false)
                errorMessage = error.getMessage();
            answer.sigInResponse(ModelAnswer, errorMessage);
        }
    }

    public void attach(SigInInterface view) {
        answer = view;
    }

    public void dettach() {
        answer = null;
    }
}
