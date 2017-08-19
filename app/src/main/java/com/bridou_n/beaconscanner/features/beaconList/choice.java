package com.bridou_n.beaconscanner.features.beaconList;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bridou_n.beaconscanner.R;

public class choice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        Button squat = (Button) findViewById(R.id.squat);
        Button lunge = (Button) findViewById(R.id.lunge);
        Button rope_skipping = (Button) findViewById(R.id.rope_skipping);
        squat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(choice.this, squatvideo.class);
                startActivity(intent);
            }
        });

        lunge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(choice.this, lungevideo.class);
                startActivity(intent);
            }
        });

        rope_skipping.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(choice.this, ropevideo.class);
                startActivity(intent);
            }
        });

    }
}
