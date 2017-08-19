package com.bridou_n.beaconscanner.features.beaconList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import com.bridou_n.beaconscanner.R;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        ImageButton wholeButton = (ImageButton)findViewById(R.id.viewButton);
        ImageButton centerButton = (ImageButton)findViewById(R.id.centerButton);


//       click안하고 대기
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                Intent intent = new Intent(IntroActivity.this, choice.class);
//                startActivity(intent);
//                // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
//                finish();
//            }
//        }, 2000);
    }

    public void introClick(View view){
        Intent intent = new Intent(IntroActivity.this, choice.class);
        startActivity(intent);
        // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
        finish();
    }
}
