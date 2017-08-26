package com.bridou_n.beaconscanner.features.beaconList;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bridou_n.beaconscanner.AppSingleton;
import com.bridou_n.beaconscanner.R;
import com.bridou_n.beaconscanner.events.Events;
import com.bridou_n.beaconscanner.events.RxBus;
import com.bridou_n.beaconscanner.models.BeaconSaved;
import com.bridou_n.beaconscanner.utils.BluetoothManager;
import com.bridou_n.beaconscanner.utils.DividerItemDecoration;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import android.speech.tts.TextToSpeech;

import static com.bridou_n.beaconscanner.R.id.text;
import static com.bridou_n.beaconscanner.R.id.textView1;
//
//
public class SquartActivity extends AppCompatActivity implements BeaconConsumer, EasyPermissions.PermissionCallbacks, TextToSpeech.OnInitListener
{

    protected static final String TAG = "SQUART_ACTIVITY";
    private static final String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int RC_COARSE_LOCATION = 1;
    private static final int RC_SETTINGS_SCREEN = 2;
    private static final String PREF_TUTO_KEY = "PREF_TUTO_KEY";
    private static final String STATE_SCANNING = "scanState";
    public static String distance_value="-1";//지혜
    public static String uuid_value="-1" ;//지혜
    public static String [] major={"-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1"};//지혜
    public static String [] minor={"-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1"};//지혜
    public static String [] yaw={"-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1"};//지혜
    public static String [] roll={"-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1"};//지혜
    public static char  yaw0; //지혜
    public static char  yaw10; //지혜
    public static char  yaw100; //지혜
    public static char roll0;
    public static char yaw1; //지혜
    public static String one="0"; //은주//지혜(char-->string 변수형 변환)
    public static String two="0";//은주 //지혜(char-->string 변수형 변환)
    public static String three="0"; //지혜
    public static String four="0";//지혜
    public static int flag=-1;//지혜
    public static int count=0;
    public static int test=0;

    int exnum, gaptime, pausetime;

    TextToSpeech myTTS;

    private CompositeSubscription subs = new CompositeSubscription();

    @Inject @Named("fab_search") Animation rotate;
    @Inject BluetoothManager bluetooth;
    @Inject BeaconManager beaconManager;
    @Inject RxBus rxBus;
    @Inject Realm realm;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.activity_squart) CoordinatorLayout rootView;
    @BindView(R.id.bluetooth_state) TextView bluetoothState;

    @BindView(R.id.empty_view) RelativeLayout emptyView;
    @BindView(R.id.beacons_rv) RecyclerView beaconsRv;
    @BindView(R.id.scan_fab) FloatingActionButton scanFab;
    @BindView(R.id.scan_progress) ProgressBar scanProgress;
    @BindView(R.id.textView1) TextView textView1;
    @BindView(R.id.textView2) TextView textView2;
    @BindView(R.id.textView1_1) TextView textView1_1;
    @BindView(R.id.textView2_1) TextView textView2_1;



    /*value*/
    int leftHandValue ;
    int preValue;
    int state;
    int rightHandValue;
    int leftFootValue;
    int rightFootValue;
    int diff ;
    int time=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squart);
        ButterKnife.bind(this);
        AppSingleton.activityComponent().inject(this);
        myTTS = new TextToSpeech(this,this);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        myTTS.setSpeechRate(1);
        RealmResults<BeaconSaved> beaconResults = realm.where(BeaconSaved.class).findAllSortedAsync(new String[]{"lastMinuteSeen", "distance"}, new Sort[]{Sort.DESCENDING, Sort.ASCENDING});

        beaconResults.addChangeListener(results -> {
            if (results.size() == 0 && emptyView.getVisibility() != View.VISIBLE) {
                beaconsRv.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else if (results.size() > 0 && beaconsRv.getVisibility() != View.VISIBLE) {
                beaconsRv.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        });

        beaconsRv.setHasFixedSize(true);
        beaconsRv.setLayoutManager(new LinearLayoutManager(this));
        beaconsRv.addItemDecoration(new DividerItemDecoration(this, null));
        // beaconsRv.setAdapter(new BeaconsRecyclerViewAdapter(this, beaconResults, true));//지혜

        // Set our event handler

        subs.add(rxBus.toObserverable() //1
                .observeOn(AndroidSchedulers.mainThread()) // We use this so we use the realm on the good thread & we can make UI changes
                .subscribe(e -> {
                    if (e instanceof Events.RangeBeacon) {
                        Log.d("RxBus", "RxBus");
                        updateUiWithBeaconsArround(((Events.RangeBeacon) e).getBeacons());
                        int a = 0;

                        //for (int i = 0; i < 10; i++) {
                        Log.d("roll_i", roll[0]);
                        Log.d("major_i", major[0]);
                        Log.d("minor_i", minor[0]);
                        if (major[0].equals("1234") && minor[0].equals("5678")) {
                            //  if (a % 2 == 0) {
                            textView1.setText("major = 1234" + "\n" + "minor = 5678" + "\n" + "roll = " + roll[0]);//지혜
                            //} else if (a % 2 == 1) {
                            //  textView1_1.setText("roll = " + roll[i]);//지혜
                            //}
                            a++;
                            one = roll[0];
                            Log.d("1234", "5678");
                            leftHandValue = Integer.parseInt(one);
                                /*왼손 범위 적용
                                * one값 이용
                                * */
                            Bundle intent = getIntent().getExtras();
                            exnum = Integer.parseInt(intent.getString("E_NUM3"));
                            gaptime = Integer.parseInt(intent.getString("E_TIME3"));
                            pausetime = Integer.parseInt(intent.getString("P_TIME3"));

                            if (leftHandValue ==0) {
                                myTTS.speak("UP", TextToSpeech.QUEUE_FLUSH, null);
                                // leftHand.setImageResource(R.drawable.lh11);
                            } else if (leftHandValue ==1) {
                                myTTS.speak("유지", TextToSpeech.QUEUE_FLUSH, null);
                                //leftHand.setImageResource(R.drawable.lh22);
                                //함수로 가서 timertask
                            } else if (leftHandValue ==2&&preValue==1) {
                                state=1;//올라가고 있는 상태
                                myTTS.speak("UP", TextToSpeech.QUEUE_FLUSH, null);
                                //leftHand.setImageResource(R.drawable.lh33);
                            } else if (leftHandValue ==2&&preValue==3) {
                                state=0;//내려가고 있는 상태
                                myTTS.speak("DOWN", TextToSpeech.QUEUE_FLUSH, null);
                                //leftHand.setImageResource(R.drawable.lh33);
                            }else if (leftHandValue ==2&&preValue==2&&state==1) {
                                //올라가고 있음
                                myTTS.speak("UP", TextToSpeech.QUEUE_FLUSH, null);
                                //leftHand.setImageResource(R.drawable.lh33);
                            }else if (leftHandValue ==2&&preValue==2&&state==0) {
                                //내려가고가고 있음
                                myTTS.speak("DOWN", TextToSpeech.QUEUE_FLUSH, null);
                                //leftHand.setImageResource(R.drawable.lh33);
                            }else if (leftHandValue ==3&&preValue==3) {
                                myTTS.speak("이제 다시 다운해라", TextToSpeech.QUEUE_FLUSH, null);
                                //leftHand.setImageResource(R.drawable.lh44);
                            }else if (leftHandValue ==3) {
                                time++;
                                myTTS.speak("일어남"+time+"회", TextToSpeech.QUEUE_FLUSH, null);
                                //leftHand.setImageResource(R.drawable.lh44);
                                if(time==exnum){
                                    myTTS.speak("끝", TextToSpeech.QUEUE_FLUSH, null);
                                    //함수추가//아예멈추는
                                    onDestroy();
                                }

                            }
                            preValue=leftHandValue;
                        }
                    }
                    //}
     /*               for(int i=0;i<10;i++){
                        yaw[i]="-1";
                        major[i]="-1";
                        minor[i]="-1";
                    }*/
                }));



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
                    if (!getPreferences(Context.MODE_PRIVATE).getBoolean(PREF_TUTO_KEY, false)) {
                        showTutorial();
                    }
                }));

        subs.add(bluetooth.observe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> {
                    if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_SCANNING)) {
                        startScan();
                    }
                }));
/*
        if (!getPreferences(Context.MODE_PRIVATE).getBoolean(PREF_TUTO_KEY, false)) {
            showTutorial();
        }

        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_SCANNING)) {
            startScan();
        }
        */
    }

    public void onInit(int status){
        //  myTTS.speak("삐",TextToSpeech.QUEUE_FLUSH,null);
    }


    public void showTutorial() {
        AppCompatActivity _this = this;

        TapTargetView.showFor(this,
                TapTarget.forToolbarMenuItem(toolbar, R.id.action_bluetooth, getString(R.string.bluetooth_control), getString(R.string.feature_bluetooth_content)).cancelable(false).dimColor(R.color.primaryText).drawShadow(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        bluetooth.enable();
                        TapTargetView.showFor(_this,
                                TapTarget.forView(scanFab, getString(R.string.feature_scan_title), getString(R.string.feature_scan_content)).tintTarget(false).cancelable(false).dimColor(R.color.primaryText).drawShadow(true),
                                new TapTargetView.Listener() {
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);
                                        startStopScan(); // We start scanning for beacons
                                        TapTargetView.showFor(_this,
                                                TapTarget.forToolbarMenuItem(toolbar, R.id.action_clear, getString(R.string.feature_clear_title), getString(R.string.feature_clear_content)).cancelable(false).dimColor(R.color.primaryText).drawShadow(true),
                                                new TapTargetView.Listener() {
                                                    @Override
                                                    public void onTargetClick(TapTargetView view) {
                                                        super.onTargetClick(view);
                                                        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(PREF_TUTO_KEY, true).apply();
                                                    }
                                                });
                                    }
                                });
                    }
                });
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
                            beacon.setBeaconType(b.getBeaconTypeCode() == 0xbeac? BeaconSaved.TYPE_ALTBEACON : BeaconSaved.TYPE_IBEACON); // 0x4c000215 is iBeacon
                            beacon.setUUID(b.getId1().toString());
                            uuid_value=b.getId1().toString();//지혜
                            roll0=uuid_value.charAt(10);
                            // yaw0=uuid_value.charAt(1);
                            // yaw100 = uuid_value.charAt(3);
                            // yaw10 = uuid_value.charAt(5);
                            // yaw1 = uuid_value.charAt(7);
                            //지혜
                            Log.d("roll",uuid_value.charAt(10)+"");
                            // yaw[count]=yaw0 + "" + yaw100 + "" + yaw10 + "" + yaw1;
                            roll[count]=roll0+"";
                            beacon.setMajor(b.getId2().toString());
                            major[count]=b.getId2().toString();//지혜
                            beacon.setMinor(b.getId3().toString());
                            minor[count]=b.getId3().toString();//지혜
                            Log.d("roll",roll[count]);
                            Log.d("major",major[count]);
                            Log.d("minor",minor[count]);
                        }
                        Log.d("update","ui");
                        tRealm.copyToRealmOrUpdate(beacon);
                        count++;
                    });
            Log.d("check","bloothe");
            count=0;
        });
    }

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
            ActivityCompat.requestPermissions(SquartActivity.this, perms, RC_COARSE_LOCATION);
        }
    }

    @OnClick(R.id.scan_fab)
    public void startStopScan() {
        if (!beaconManager.isBound(this)) {
            if (!bluetooth.isEnabled()) {
                Snackbar.make(rootView, getString(R.string.enable_bluetooth_to_start_scanning), Snackbar.LENGTH_LONG).show();
                return ;
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
                ActivityCompat.requestPermissions(SquartActivity.this, perms, RC_COARSE_LOCATION);
            }
        }
    }

    public void showPermissionSnackbar() {
        final Snackbar snackBar = Snackbar.make(rootView, getString(R.string.enable_permission_from_settings), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(getString(R.string.enable),v -> {
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
        return super.onOptionsItemSelected(item);
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

    }
}

