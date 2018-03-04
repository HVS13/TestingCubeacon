package id.tiregdev.testingapps;

import android.app.Application;

import com.eyro.cubeacon.Cubeacon;
import com.eyro.mesosfer.Mesosfer;

/**
 * Created by Eyro on 10/19/16.
 */
public class MesosferApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // uncomment this line below to show Mesosfer log in verbose mode
        // Mesosfer.setLogLevel(Mesosfer.LOG_LEVEL_VERBOSE);
        Mesosfer.setPushNotification(true);

        // initialize Mesosfer SDK
        Mesosfer.initialize(this, "wPW3cqhqG6", "RQM68u7nC3nMf6e1wKt8p37qQq7RZRVE");

        Cubeacon.initialize(this);
    }
}
