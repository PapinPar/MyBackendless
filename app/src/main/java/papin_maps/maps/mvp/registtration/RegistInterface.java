package papin_maps.maps.MVP.registtration;

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;

import java.io.IOException;

/**
 * Created by Papin on 26.10.2016.
 */

public interface RegistInterface {
    void RegistComplete(BackendlessUser respone) throws IOException;
    void RegistFail(BackendlessFault fault) throws IOException;
}
