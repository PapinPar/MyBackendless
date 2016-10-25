package papin_maps.maps.signIn;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 25.10.2016.
 */

public class MyPresenter implements BackManager.getLoginModelAnswer{

   private myAnswer answer;

    public interface myAnswer{
        void Answer(boolean a);
    }

    public MyPresenter(myAnswer answer) {
        this.answer = answer;
    }

    public void backAnswer(String email, String password)
    {
        BackManager.getInstance().login(email,password, MyPresenter.this);
    }


    @Override
    public void getLoginModelAnswer(boolean ModelAnswer) {
        answer.Answer(ModelAnswer);
    }
}
