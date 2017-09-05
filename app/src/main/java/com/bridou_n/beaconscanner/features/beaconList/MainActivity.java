package com.bridou_n.beaconscanner.features.beaconList;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bridou_n.beaconscanner.AppSingleton;
import com.bridou_n.beaconscanner.R;
import com.bridou_n.beaconscanner.events.Events;
import com.bridou_n.beaconscanner.events.RxBus;
import com.bridou_n.beaconscanner.models.BeaconSaved;
import com.bridou_n.beaconscanner.utils.BluetoothManager;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

//import android.os.Handler;


public class MainActivity extends AppCompatActivity implements BeaconConsumer, EasyPermissions.PermissionCallbacks, TextToSpeech.OnInitListener {
    protected static final String TAG = "MAIN_ACTIVITY";
    private static final String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int RC_COARSE_LOCATION = 1;
    private static final int RC_SETTINGS_SCREEN = 2;
    private static final String PREF_TUTO_KEY = "PREF_TUTO_KEY";
    private static final String STATE_SCANNING = "scanState";
    public static String distance_value = "-1";//지혜
    public static String major = "-1";//지혜
    public static String minor = "-1";
    public static String uuid_value = "-1";
    public static String yaw = "-1";
    public static String yaw5 = "noValue";
    public static String yaw6 = "noValue";
    public static String yaw7 = "noValue";
    public static String yaw8 = "noValue";
    public static char yaw0; //지혜
    public static char yaw10; //지혜
    public static char yaw100; //지혜
    public static char yaw1; //지혜
    public static String one = "0"; //은주//지혜(char-->string 변수형 변환)
    public static String two = "0";//은주 //지혜(char-->string 변수형 변환)
    public static String three = "0"; //지혜
    public static String four = "0";//지혜
    public static int flag = -1;//지혜
    public static int test = 0;
    PopupWindow helpPopupWindow;

    private CompositeSubscription subs = new CompositeSubscription();

    @Inject
    @Named("fab_search")
    Animation rotate;
    @Inject
    BluetoothManager bluetooth;
    @Inject
    BeaconManager beaconManager;
    @Inject
    RxBus rxBus;
    @Inject
    Realm realm;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main)
    CoordinatorLayout rootView;
    @BindView(R.id.bluetooth_state)
    TextView bluetoothState;

    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    //@BindView(R.id.beacons_rv) RecyclerView beaconsRv;
    @BindView(R.id.scan_fab)
    FloatingActionButton scanFab;
    @BindView(R.id.scan_progress)
    ProgressBar scanProgress;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView1_1)
    TextView textView1_1;
    @BindView(R.id.textView2_1)
    TextView textView2_1;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.textView3_1)
    TextView textView3_1;
    @BindView(R.id.textView4_1)
    TextView textView4_1;

    @BindView(R.id.leftHand)
    ImageView leftHand; //지혜
    @BindView(R.id.rightHand)
    ImageView rightHand; //지혜
    @BindView(R.id.leftFoot)
    ImageView leftFoot; //지혜
    @BindView(R.id.rightFoot)
    ImageView rightFoot; //지혜

    /*value*/
    int leftHandValue;
    int rightHandValue;
    int leftFootValue;
    int rightFootValue;
    Toast toast;

    TextToSpeech myTTS;

    int limitLeftHand = 0, limitRightHand = 0;
    int limitLeftFoot = 0, limitRightFoot = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {

            textView1.setText("major = 1234" + "\n" + "minor = 5678" + "\n" + "yaw = " + yaw5);
            textView2.setText("major = 1234" + "\n" + "minor = 6789" + "\n" + "yaw = " + yaw6);
            textView3.setText("major = 1234" + "\n" + "minor = 7891" + "\n" + "yaw = " + yaw7);
            textView4.setText("major = 1234" + "\n" + "minor = 8912" + "\n" + "yaw = " + yaw8);

            if (yaw5 != "noValue") {
                leftHandValue = Integer.parseInt(yaw5);
            } else {
                leftHandValue = 0;
            }

            if (yaw6 != "noValue") {
                rightHandValue = Integer.parseInt(yaw6);
            } else {
                rightHandValue = 0;
            }

            if (yaw7 != "noValue") {
                leftFootValue = Integer.parseInt(yaw7);
            } else {
                leftFootValue = 0;
            }

            if (yaw8 != "noValue") {
                rightFootValue = Integer.parseInt(yaw8);
            } else {
                rightFootValue = 0;
            }


            if (leftHandValue < 1600) {
                leftHand.setImageResource(R.drawable.bargraph0);
            } else if (leftHandValue < 2800) {
                leftHand.setImageResource(R.drawable.bargraph);
            } else if (leftHandValue < 4000) {
                leftHand.setImageResource(R.drawable.bargraph2);
            } else {
                leftHand.setImageResource(R.drawable.bargraph3);
            }

            if (rightHandValue < 1600) {
                rightHand.setImageResource(R.drawable.bargraph0);
            } else if (rightHandValue < 2800) {
                rightHand.setImageResource(R.drawable.bargraph);
            } else if (rightHandValue < 4000) {
                rightHand.setImageResource(R.drawable.bargraph2);
            } else {
                rightHand.setImageResource(R.drawable.bargraph3);
            }

            if (leftFootValue < 1600) {
                leftFoot.setImageResource(R.drawable.bargraph0);
            } else if (leftFootValue < 2800) {
                leftFoot.setImageResource(R.drawable.bargraph);
            } else if (leftFootValue < 4000) {
                leftFoot.setImageResource(R.drawable.bargraph2);
            } else {
                leftFoot.setImageResource(R.drawable.bargraph3);
            }


            if (rightFootValue < 1600) {
                rightFoot.setImageResource(R.drawable.bargraph0);
            } else if (rightFootValue < 2800) {
                rightFoot.setImageResource(R.drawable.bargraph);
            } else if (rightFootValue < 4000) {
                rightFoot.setImageResource(R.drawable.bargraph2);
            } else {
                rightFoot.setImageResource(R.drawable.bargraph3);
            }


            if (leftHandValue > rightHandValue) {
                if (leftHandValue - rightHandValue > 1500) {
                    limitLeftHand++;
                } else {
                    limitLeftHand = 0;
                }
            } else {
                if (rightHandValue - leftHandValue > 1500) {
                    limitRightHand++;
                } else {
                    limitRightHand = 0;
                }
            }

            if (leftFootValue > rightFootValue) {
                if (leftFootValue - rightFootValue > 1500) {
                    limitLeftFoot++;
                } else {
                    limitLeftFoot = 0;
                }
            } else {
                if (rightFootValue - leftFootValue > 1500) {
                    limitRightFoot++;
                } else {
                    limitRightFoot = 0;
                }
            }


            if (leftHandValue != 0 && rightHandValue != 0) {
                if (limitLeftHand > 5) {
                    Toast.makeText(MainActivity.this, "LeftHand Is Fast", Toast.LENGTH_SHORT).show();
                    myTTS.speak("왼손빠름", TextToSpeech.QUEUE_FLUSH, null);
                    limitLeftHand = 0;
                }
                if (limitRightHand > 5) {
                    Toast.makeText(MainActivity.this, "RightHand Is Fast", Toast.LENGTH_SHORT).show();
                    myTTS.speak("오른손빠름", TextToSpeech.QUEUE_FLUSH, null);
                    limitRightHand = 0;
                }
            }

            if (leftFootValue != 0 && rightFootValue != 0) {
                if (limitLeftFoot > 5) {
                    Toast.makeText(MainActivity.this, "LeftFoot Is Fast", Toast.LENGTH_SHORT).show();
                    myTTS.speak("왼발빠름", TextToSpeech.QUEUE_FLUSH, null);
                    limitLeftFoot = 0;
                }

                if (limitRightFoot > 5) {
                    Toast.makeText(MainActivity.this, "RightFoot Is Fast", Toast.LENGTH_SHORT).show();
                    myTTS.speak("오른발빠름", TextToSpeech.QUEUE_FLUSH, null);
                    limitRightFoot = 0;
                }
            }
            handler.postDelayed(this, 500);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AppSingleton.activityComponent().inject(this);
        myTTS = new TextToSpeech(this, this);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        myTTS.setSpeechRate(1);

        RealmResults<BeaconSaved> beaconResults = realm.where(BeaconSaved.class).findAllSortedAsync(new String[]{"lastMinuteSeen", "distance"}, new Sort[]{Sort.DESCENDING, Sort.ASCENDING});


        subs.add(rxBus.toObserverable() //
                .observeOn(AndroidSchedulers.mainThread()) // We use this so we use the realm on the good thread & we can make UI change
                .subscribe(e -> {
                    if (e instanceof Events.RangeBeacon) {
                        updateUiWithBeaconsArround(((Events.RangeBeacon) e).getBeacons());
                    }
                }, throwable -> Log.e("Error", throwable.getMessage())));

        runnable.run();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

        // Setup an observable on the bluetooth changes
        subs.add(bluetooth.observe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> {
                    if (e instanceof Events.BluetoothState) {
                        bluetoothStateChanged(((Events.BluetoothState) e).getState());
                    }
                }));
        subs.add(bluetooth.observe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> {
                    if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_SCANNING)) {
                        startScan();
                    }
                }));
    }

    public void onInit(int status) {
        //  myTTS.speak("삐",TextToSpeech.QUEUE_FLUSH,null);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateUiWithBeaconsArround(Collection<Beacon> beacons) {
        realm.executeTransactionAsync(tRealm -> {
            Observable.from(beacons)
                    .subscribe(b -> {
                        BeaconSaved beacon = new BeaconSaved();

                        if (b.getServiceUuid() == 0xfeaa) { // This is an Eddystone beacon
                        } else { // This is an iBeacon or ALTBeacon
                            beacon.setBeaconType(b.getBeaconTypeCode() == 0xbeac ? BeaconSaved.TYPE_ALTBEACON : BeaconSaved.TYPE_IBEACON); // 0x4c000215 is iBeacon
                            beacon.setUUID(b.getId1().toString());
                            uuid_value = b.getId1().toString();//지혜

                            yaw0 = uuid_value.charAt(1);
                            yaw100 = uuid_value.charAt(3);
                            yaw10 = uuid_value.charAt(5);
                            yaw1 = uuid_value.charAt(7);
                            //지혜
                            yaw = yaw0 + "" + yaw100 + "" + yaw10 + "" + yaw1;

                            beacon.setMajor(b.getId2().toString());
                            major = b.getId2().toString();//지혜
                            beacon.setMinor(b.getId3().toString());
                            minor = b.getId3().toString();//지혜

                            switch (minor) {
                                case "5678":
                                    yaw5 = yaw;
                                    break;
                                case "6789":
                                    yaw6 = yaw;
                                    break;
                                case "7891":
                                    yaw7 = yaw;
                                    break;
                                case "8912":
                                    yaw8 = yaw;
                                    break;
                            }

                            Log.d("update ", "minor     " + minor + "             value      " + yaw);
                        }
                        tRealm.copyToRealmOrUpdate(beacon);
                    });
        });
    }

    ///check if this works
    private void bluetoothStateChanged(int state) {
        bluetoothState.setVisibility(View.VISIBLE);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                bluetoothState.setTextColor(ContextCompat.getColor(this, R.color.bluetoothDisabledLight));
                bluetoothState.setBackgroundColor(ContextCompat.getColor(this, R.color.bluetoothDisabled));
                bluetoothState.setText(getString(R.string.bluetooth_disabled));
                invalidateOptionsMenu();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                bluetoothState.setTextColor(ContextCompat.getColor(this, R.color.bluetoothTurningOffLight));
                bluetoothState.setBackgroundColor(ContextCompat.getColor(this, R.color.bluetoothTurningOff));
                bluetoothState.setText(getString(R.string.turning_bluetooth_off));
                stopScan();
                break;
            case BluetoothAdapter.STATE_ON:
                bluetoothState.setVisibility(View.GONE); // If the bluetooth is ON, we don't warn the user
                bluetoothState.setText(getString(R.string.bluetooth_enabled));
                invalidateOptionsMenu();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                bluetoothState.setTextColor(ContextCompat.getColor(this, R.color.bluetoothTurningOnLight));
                bluetoothState.setBackgroundColor(ContextCompat.getColor(this, R.color.bluetoothTurningOn));
                bluetoothState.setText(getString(R.string.turning_bluetooth_on));
                break;
        }
    }

    public void bindBeaconManager() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            beaconManager.bind(this);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, perms, RC_COARSE_LOCATION);
        }
    }

    @OnClick(R.id.scan_fab)
    public void startStopScan() {
        if (!beaconManager.isBound(this)) {
            if (!bluetooth.isEnabled()) {
                Snackbar.make(rootView, getString(R.string.enable_bluetooth_to_start_scanning), Snackbar.LENGTH_LONG).show();
                return;
            }
            startScan();
        } else {
            stopScan();
        }
    }

    public void startScan() {
        bindBeaconManager();
        rotate.setRepeatCount(Animation.INFINITE);
        scanFab.startAnimation(rotate);
        scanProgress.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.scanning_for_beacons));
    }

    public void stopScan() {
        beaconManager.unbind(this);
        rotate.setRepeatCount(0);
        scanProgress.setVisibility(View.INVISIBLE);
        toolbar.setTitle(getString(R.string.app_name));
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier((beacons, region) -> {
            rxBus.send(new Events.RangeBeacon(beacons, region));
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("com.bridou_n.beaconscanner", null, null, null));
        } catch (RemoteException e) {
            rxBus.sendError(e);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        bindBeaconManager();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> permList) {
        if (requestCode == RC_COARSE_LOCATION) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, permList)) {
                showPermissionSnackbar();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, perms, RC_COARSE_LOCATION);
            }
        }
    }

    public void showPermissionSnackbar() {
        final Snackbar snackBar = Snackbar.make(rootView, getString(R.string.enable_permission_from_settings), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(getString(R.string.enable), v -> {
            snackBar.dismiss();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivityForResult(intent, RC_SETTINGS_SCREEN);
        });
        snackBar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (!bluetooth.isEnabled()) {
            menu.getItem(1).setIcon(R.drawable.ic_bluetooth_disabled_white_24dp);
        }
        return true;
    }

    int popupFlag =0;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bluetooth) {
            bluetooth.toggle();
            return true;
        }
        if (id == R.id.action_clear) {
            realm.executeTransactionAsync(tRealm -> {
                tRealm.where(BeaconSaved.class).findAll().deleteAllFromRealm();
            });
        }
        if (id == R.id.action_help) {
            if(popupFlag==0) {
                showHelpPopup();
                popupFlag=1;
            }else{
                dismissPopupWindow();
                popupFlag=0;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void showHelpPopup(){

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View helpLayout = inflater.inflate(R.layout.help_ropeskipping, null);

        PopupWindow helpPopupWindow = new PopupWindow(helpLayout, WindowManager.LayoutParams.MATCH_PARENT, 500, false);
        helpPopupWindow.setOutsideTouchable(false);
        helpPopupWindow.showAtLocation(helpLayout, Gravity.CENTER, 50, 50);
        setPopupWindow(helpPopupWindow);
    }

    public void setPopupWindow(PopupWindow helpPopupWindow){
        this.helpPopupWindow = helpPopupWindow;
    }

    public void dismissPopupWindow(){
        helpPopupWindow.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_SCANNING, beaconManager.isBound(this)); // save the scanning state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        subs.unsubscribe();
        if (beaconManager.isBound(this)) {
            beaconManager.unbind(this);
        }
        realm.close();
        myTTS.shutdown();
        if (toast != null) {
            toast.cancel();
        }

    }
}
