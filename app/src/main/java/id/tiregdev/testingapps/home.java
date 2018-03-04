package id.tiregdev.testingapps;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eyro.cubeacon.CBMonitoringListener;
import com.eyro.cubeacon.CBRegion;
import com.eyro.cubeacon.CBServiceListener;
import com.eyro.cubeacon.Cubeacon;
import com.eyro.cubeacon.MonitoringState;
import com.eyro.cubeacon.SystemRequirementManager;
import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.GetCallback;
import com.eyro.mesosfer.LogOutCallback;
import com.eyro.mesosfer.MesosferBeacon;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferLog;
import com.eyro.mesosfer.MesosferStoryline;
import com.eyro.mesosfer.MesosferStorylineDetail;
import com.eyro.mesosfer.MesosferUser;
import com.eyro.mesosfer.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity implements CBServiceListener, CBMonitoringListener {

    private List<CBRegion> regions;
    private Cubeacon beaconManager;
    Button logout, connect;
    TextView nameUser;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        beaconManager = Cubeacon.getInstance();
        // get list of parcel beacon from an activity intent
        List<beacon> beaconList = getIntent().getParcelableArrayListExtra("BEACONS");

        // populate list of beacon into region scanning
        regions = new ArrayList<>();
        for (beacon beacon : beaconList) {
            CBRegion region = new CBRegion(beacon.getIdentifier(), beacon.getProximityUUID(),
                    beacon.getMajor(), beacon.getMinor());
            regions.add(region);
        }
        setLogout();
        setUser();
        setConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check all requirements to comply
        if (SystemRequirementManager.checkAllRequirementUsingDefaultDialog(this)) {
            // connecting to the service scanning
            beaconManager.connect(this);
        }
    }

    public void setUser(){
        nameUser = findViewById(R.id.nameUser);
        final MesosferUser user = MesosferUser.getCurrentUser();
        user.fetchAsync(new GetCallback<MesosferUser>() {
            @Override
            public void done(MesosferUser mesosferUser, MesosferException e) {
                // check if there is an exception happen
                if (e != null) {
                    // handle the exception
                    return;
                }
                // fetch user data succeeded
                nameUser.setText(user.getFirstName());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setLogout(){
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MesosferUser.logOutAsync(new LogOutCallback() {
                    @Override
                    public void done(MesosferException e) {
                        // check if there is an exception happen
                        if (e != null) {
                            // handle the exception
                            return;
                        }

                        Intent i = new Intent(home.this, Login.class);
                        startActivity(i);
                        Toast.makeText(home.this, "Logout Success", Toast.LENGTH_SHORT).show();
                        // log out succeeded
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (beaconManager != null) {
            beaconManager.disconnect(this);
        }

        super.onDestroy();
    }

    @Override
    public void onBeaconServiceConnect() {
            try {
                // set beacon listener
                beaconManager.addMonitoringListener(this);

                // start scanning beacon when service already connected
                beaconManager.startMonitoringForRegions(regions);
            } catch (RemoteException e) {
                Log.e("MAIN", "Error happen while monitoring region: " + e);
            }
    }

    @Override
    public void didEnterRegion(final CBRegion region) {
        Toast.makeText(this, "Tesss", Toast.LENGTH_SHORT).show();
        if (flag == 1) {

            Log.d("MAIN", "Entering region: " + region);
            searchForStoryline(region, MesosferBeacon.Event.ENTER);
            sendLog(region, MesosferBeacon.Event.ENTER);
        }
    }

    @Override
    public void didExitRegion(CBRegion region) {
        if (flag == 1) {
            Toast.makeText(this, "Tesss", Toast.LENGTH_SHORT).show();
            Log.d("MAIN", "Exiting region: " + region);
            searchForStoryline(region, MesosferBeacon.Event.EXIT);
            sendLog(region, MesosferBeacon.Event.EXIT);
        }
    }

    @Override
    public void didDetermineStateForRegion(MonitoringState state, CBRegion region) {
            switch (state) {
                case INSIDE:
                    Log.d("MAIN", "Change state to entering region: " + region);
                    break;
                case OUTSIDE:
                    Log.d("MAIN", "Change state to exiting region: " + region);
                    break;
            }
    }

    private void searchForStoryline(CBRegion region, MesosferBeacon.Event event) {
        MesosferStorylineDetail
                .getQuery()
                .whereEqualTo("beacons", region.getIdentifier())
                .whereEqualTo("event", event.name())
                .setLimit(1)
                .findAsync(new FindCallback<MesosferStorylineDetail>() {
                    @Override
                    public void done(List<MesosferStorylineDetail> list, MesosferException e) {
                        if (e != null) {
                            Log.e("MAIN", "Error happen when finding storyline: " + e);
                            return;
                        }

                        if (list != null && !list.isEmpty()) {
                            MesosferStorylineDetail detail = list.get(0);
                            displayStoryline(detail);
                            String details = String.valueOf(detail);
                            Toast.makeText(home.this, details, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void displayStoryline(MesosferStorylineDetail detail) {
        if (detail.getCampaign() == MesosferStoryline.Campaign.TEXT) {
            String title = detail.getAlertTitle();
            String message = detail.getAlertMessage();

            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(android.R.string.ok, null)
                    .show();
        }
    }

    private void sendLog(CBRegion region, final MesosferBeacon.Event event) {
        String beaconId = region.getIdentifier();
        MesosferBeacon beacon = MesosferBeacon.createWithObjectId(beaconId);

        MesosferLog.createLog()
                .setEvent(event)
                .setBeacon(beacon)
                .setModule(MesosferBeacon.Module.PRESENCE)
                .sendAsync(new SaveCallback() {
                    @Override
                    public void done(MesosferException e) {
                        if (e != null) {
                            Log.e("MAIN", "Failed to send log: " + e);
                            return;
                        }

                        String state = event == MesosferBeacon.Event.ENTER ? "Check in" : "Check out";
                        Toast.makeText(home.this, state + " sent.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setConnect(){
        connect = findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0){
                    flag = 1;
                    String flags = String.valueOf(flag);
                    Toast.makeText(home.this, flags, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    flag = 0;
                    String flags = String.valueOf(flag);
                    Toast.makeText(home.this, flags, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
