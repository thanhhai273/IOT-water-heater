package com.example.iotex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TotalRoomActivity extends AppCompatActivity {
    private RelativeLayout btnBathroom;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.total_room);
        btnBathroom = findViewById(R.id.bathroom);
        btnBathroom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(TotalRoomActivity.this, UserMainActivity.class) );
            }
        });
    }
}
