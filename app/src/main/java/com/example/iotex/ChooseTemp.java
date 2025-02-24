package com.example.iotex;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChooseTemp extends AppCompatActivity {
    private Button btnSetTemp;
    private EditText editTextTemp;
    private String temp, email, today;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_choose);

        btnSetTemp = (Button) findViewById(R.id.btn_set_temp);
        editTextTemp = (EditText) findViewById(R.id.editText_temp);
        btnSetTemp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                UserMainActivity a = new UserMainActivity();
                a.information();
                LocalDate date = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                today = date.format(formatter);
                email = a.email;
                temp = editTextTemp.getText().toString();
                int tempInt = Integer.parseInt(temp);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(email).child(today).child("tem_auto").setValue(tempInt);
                Toast.makeText(ChooseTemp.this, "Temperature has been set", Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}
