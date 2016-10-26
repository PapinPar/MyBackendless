package papin_maps.maps.MVP.registtration;

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;

import java.io.IOException;

/**
 * Created by Papin on 26.10.2016.
 */

public interface InterfaceRegistration {
    void getRegistAnswer(BackendlessUser respone) throws IOException;
    void getRegistAnswer(BackendlessFault fault) throws IOException;
}
