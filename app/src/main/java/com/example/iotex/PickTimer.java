package com.example.iotex;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.example.iotex.UserMainActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PickTimer extends AppCompatActivity {
    private Button btn_time_start, btn_time_end;
    private SimpleDateFormat simpleDateFormat;
    private TimePicker picker;
    private String email, daily, stringHour;
    private FirebaseUser user;
    private String today;
    private String yesterday, stringDate;
    private ImageView btn_back_dialog;
    private int hourStart = -1;
    private int hourEnd = -1;
    private int minuteStart = -1;
    private int minuteEnd = -1;
    private FirebaseFirestore rootRef;
    private DatabaseReference mDatabase;
    private RadioButton radio_buttonOn,radio_buttonOff;
    TextView textView_start, textView_end;
    private RadioGroup radio_group;
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        rootRef = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timepicker);
        btn_back_dialog = (ImageView) findViewById(R.id.btn_back_dialog);
        textView_end = (TextView) findViewById(R.id.textView_end);
        textView_start = (TextView) findViewById(R.id.textView_start);
        btn_time_start = (Button) findViewById(R.id.button_start);
        btn_time_end = (Button) findViewById(R.id.button_end);
        radio_buttonOn = (RadioButton) findViewById(R.id.radio_buttonOn);
        radio_buttonOff = (RadioButton) findViewById(R.id.radio_buttonOff);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        btn_back_dialog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PickTimer.this, UserMainActivity.class);
                startActivity(intent);
            }
        });
        config();
        checkDaily();
        this.btn_time_start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                buttonSelectTime();
            }
        });
        this.btn_time_end.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                buttonSelectTime1();
            }
        });
        date();
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_buttonOn:
                        setRadioOn();
                        break;
                    case R.id.radio_buttonOff:
                        setRadioOff();
                        break;
                }
            }
        });
        //getData();
    }

    private void buttonSelectTime() {
        if (this.hourStart == -1) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            this.hourStart = c.get(Calendar.HOUR_OF_DAY);
            this.minuteStart = c.get(Calendar.MINUTE);
        }

        // Time Set Listener.
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                textView_start.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                hourStart = hourOfDay;
                minuteStart = minute;
                setFirebase();
            }
        };

        // Create TimePickerDialog:
        TimePickerDialog timePickerDialog = null;

        timePickerDialog = new TimePickerDialog(this,
                timeSetListener, hourStart, minuteStart, true);

        // Show
        timePickerDialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTimer() {
        UserMainActivity a = new UserMainActivity();
        a.date();
        stringDate = a.stringDate;
        today = a.today;
        yesterday = a.yesterday;
        mDatabase.child(email).child(stringDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> text1 = (HashMap<String, String>) snapshot.getValue();
                if (text1 == null) {
                    today = stringDate;
                    textView_start.setText("00:00:00");
                    textView_end.setText("00:00:00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void setTimer() {
        getTimer();
        mDatabase.child(email).child(today).child("timer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String text = snapshot.getValue().toString();
                if (snapshot.getValue() == null) {
                    textView_start.setText("00:00:00");
                    textView_end.setText("00:00:00");
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getData() {
        UserMainActivity a;
        a = new UserMainActivity();
        a.information();
        email = a.email;
        if (email != null) {
            rootRef.collection(email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

    }

    private void buttonSelectTime1() {
        if (this.hourEnd == -1) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            this.hourEnd = c.get(Calendar.HOUR_OF_DAY);
            this.minuteEnd = c.get(Calendar.MINUTE);
        }

        // Time Set Listener.
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                textView_end.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                hourEnd = hourOfDay;
                minuteEnd = minute;
                setFirebase();
            }
        };

        // Create TimePickerDialog:
        TimePickerDialog timePickerDialog = null;

        timePickerDialog = new TimePickerDialog(this,
                timeSetListener, hourEnd, minuteEnd, true);


        // Show
        timePickerDialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void setFirebase() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        today = date.format(formatter);
        UserMainActivity a;
        a = new UserMainActivity();
        a.information();
        email = a.email;
        mDatabase.child(email).child(today).child("timer").child("on").setValue(hourStart + ":" + minuteStart);
        mDatabase.child(email).child(today).child("timer").child("off").setValue(hourEnd + ":" + minuteEnd);
    }

//    public void onRadioButtonClicked() {
//        if(radio_button.isChecked())
//        {
//            mDatabase.child(email).child(today).child("daily").setValue("1");
//        }
//        else
//        {
//            mDatabase.child(email).child(today).child("daily").setValue("0");
//        }
//    }

    public void config() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        email = email.replaceAll("[\\-\\+\\.\\^:,]", "");
    }

    //Kiem tra Daily cua trường cuối cùng set trên firebase để set cho ngày hiện tại


    //Kiem tra daily cua ngay cũ nhất xem là on hay off
    public String checkDaily() {
        Query lastQuery = mDatabase.child("sang230799@gmailcom").orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dayCheck = snapshot.getValue().toString().substring(1,11);
                daily = snapshot.child(dayCheck).child("daily").getValue().toString();
                if (daily.contains("1")) {
                    mDatabase.child(email).child(stringDate).child("daily").setValue("1");
                    radio_buttonOn.setChecked(true);
                    radio_buttonOff.setChecked(false);
                }
                else {
                    mDatabase.child(email).child(stringDate).child("daily").setValue("0");
                    radio_buttonOff.setChecked(true);
                    radio_buttonOn.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return daily;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void date() {
        Calendar calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        stringDate = simpleDateFormat.format(calendar.getTime());
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        stringHour = simpleDateFormat.format(calendar.getTime());
        //Giảm 1 ngày
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        String date1 = date.format(formatter);
        LocalDate yesterday1 = LocalDate.parse(date1, formatter);
        yesterday1 = yesterday1.minusDays(1);
        yesterday = yesterday1.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    private void setRadioOn() {
        radio_buttonOn.setChecked(true);
        mDatabase.child(email).child(stringDate).child("daily").setValue("1");

    }
    private void setRadioOff() {
        radio_buttonOff.setChecked(true);
        mDatabase.child(email).child(stringDate).child("daily").setValue("0");

    }
}

