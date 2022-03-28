package com.example.iotex;

import static java.lang.Integer.parseInt;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class UserMainActivity extends AppCompatActivity {
    private FirebaseUser user;
    private Button btn_time_start, btn_time_end, btnSetTemp;
    private ImageButton btnHeater;
    private double hour;
    String email, stringDate, stringHour, temp, daily;
    String yesterday, today, dayInMonth;
    private SimpleDateFormat simpleDateFormat;
    private FirebaseAuth auth;
    final ArrayList<String> list = new ArrayList<>();
    private TimePicker picker;
    private int hourStart = -1;
    private BarChart mChart_hour;
    private CombinedChart mChart_hour2;
    private CombinedChart mChart_tem;
    private int hourEnd = -1;
    private int minuteStart = -1;
    private int minuteEnd = -1;
    float decimalTime;
    ArrayList<String> array1 = new ArrayList<>();
    ArrayList<String> array2 = new ArrayList<>();
    DatabaseReference databaseReference;
    private FirebaseFirestore rootRef;
    private RadioButton radio_buttonOn,radio_buttonOff;
    TextView textView_start, textView_end;
    private RadioGroup radio_group_timer;
    LineData lineDatas_hum = new LineData();
    LineData lineDatas_tem = new LineData();
    CombinedData data_hum = new CombinedData();
    CombinedData data_tem = new CombinedData();
    EditText editTextTemp;
    RadioGroup radio_group;
    RadioButton radio_auto;
    RadioButton radio_hand;
    RadioButton radio_time;
    ConstraintLayout ct_time;
    ConstraintLayout set_temp;
    LinkedList<Entry> entries = new LinkedList<Entry>();
    LinkedList<Entry> entries_tem = new LinkedList<Entry>();
    private DatabaseReference mDatabase;
    private SwitchCompat switchCompat, switchChartHour, switchChartHour2, switchChartTem;
    private ImageView btn_back_uma;
    ArrayList<String> labelNames = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button btnSignOut = (Button) findViewById(R.id.btn_signout);
        btnSetTemp = (Button) findViewById(R.id.btn_set_temp);
        mChart_hour = (BarChart) findViewById(R.id.chart_hour);
        mChart_hour2 = (CombinedChart) findViewById(R.id.chart_hour2);
        mChart_tem = (CombinedChart) findViewById(R.id.chart_tem);
        Button btnSetTemp = (Button) findViewById(R.id.btn_setTemp);
        switchCompat = (SwitchCompat) findViewById(R.id.sw_on_off);
        switchChartHour = (SwitchCompat) findViewById(R.id.sw_chart_hour);
        switchChartHour2 = (SwitchCompat) findViewById(R.id.sw_chart_hour2);
        switchChartTem = (SwitchCompat) findViewById(R.id.sw_chart_tem);
        btnHeater = (ImageButton) findViewById(R.id.imgbtn_heater);
        ct_time = (ConstraintLayout) findViewById(R.id.ct_time);
        set_temp = (ConstraintLayout) findViewById(R.id.set_temp);
        radio_group = (RadioGroup) findViewById(R.id.radio_group_main);
        radio_auto = (RadioButton) findViewById(R.id.radio_auto);
        radio_hand = (RadioButton) findViewById(R.id.radio_hand);
        radio_time = (RadioButton) findViewById(R.id.radio_time);
        textView_end = (TextView) findViewById(R.id.textView_end);

        textView_start = (TextView) findViewById(R.id.textView_start);
        btn_time_start = (Button) findViewById(R.id.button_start);
        btn_time_end = (Button) findViewById(R.id.button_end);
        radio_buttonOn = (RadioButton) findViewById(R.id.radio_buttonOn);
        radio_buttonOff = (RadioButton) findViewById(R.id.radio_buttonOff);
        radio_group_timer = (RadioGroup) findViewById(R.id.radio_group);
        editTextTemp = (EditText) findViewById(R.id.editText_temp_main);
        auth = FirebaseAuth.getInstance();
        final TextView btn_pick_time = (TextView) findViewById(R.id.btn_picktimer);
        checkDate();
        radioTimer();
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_auto:
                        set_temp();
//                        time2();
                        break;

                    case R.id.radio_hand:
                        time1();
                        break;
                    case R.id.radio_time:
//                        time();
                        time3();
                        config();
                        checkDaily();

                        break;
                }
            }
        });
        ChoseSwChart();

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

//        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.radio_buttonOn:
//                        setRadioOn();
//                        break;
//                    case R.id.radio_buttonOff:
//                        setRadioOff();
//                        break;
//                }
//            }
//        });

//        btn_pick_time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserMainActivity.this, PickTimer.class);
//                startActivity(intent);
//            }
//        });

//        btnSetTemp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserMainActivity.this, ChooseTemp.class);
//                startActivity(intent);
//            }
//        });

//        btnSignOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signOut();
//            }
//        });

//        mDatabase.child("state").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
    }


//    protected void comPat() {
//        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton,
//                                         boolean isChecked) {
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//                Boolean switchState = switchCompat.isChecked();
//                user = FirebaseAuth.getInstance().getCurrentUser();
//
//                 = user.getEmail();
//                email = email.replaceAll("[\\-\\+\\.\\^:,]", "");
//                mDatabase.child(email).child("state").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue().toString().startsWith("1")) {
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
////                if (isChecked) {
////                    btnHeater.setBackgroundResource(R.drawable.nong_lanh_on);
////                    mDatabase.child(email).child("state").setValue("1");
////                } else {
////                    btnHeater.setBackgroundResource(R.drawable.nong_lanh_off);
////                    mDatabase.child(email).child("state").setValue("0");
////                }
////                System.out.println(switchState);
//            }
//        });
//    }
    private void time1() {
        radio_hand.setChecked(true);
        switchCompat.setVisibility(View.VISIBLE);
        ct_time.setVisibility(View.GONE);
        switchChartHour.setVisibility(View.VISIBLE);
        switchChartHour2.setVisibility(View.VISIBLE);
        switchChartTem.setVisibility(View.VISIBLE);
        set_temp.setVisibility(View.GONE);
        mDatabase.child(email).child(stringDate).child("auto").setValue(false);
    }
    private void time2() {
        radio_auto.setChecked(true);
        switchCompat.setVisibility(View.GONE);
        ct_time.setVisibility(View.VISIBLE);
        set_temp.setVisibility(View.GONE);
    }
    private void time3() {
        mChart_tem.setVisibility(View.GONE);
        mChart_hour.setVisibility(View.GONE);
        mChart_hour2.setVisibility(View.GONE);
        radio_time.setChecked(true);
        switchChartHour.setVisibility(View.GONE);
        switchChartHour2.setVisibility(View.GONE);
        switchChartTem.setVisibility(View.GONE);
        switchCompat.setVisibility(View.GONE);
        ct_time.setVisibility(View.VISIBLE);
        set_temp.setVisibility(View.GONE);
    }
    private void ChoseSwChart() {
        switchChartHour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mChart_hour.setVisibility(View.VISIBLE);
                    hourChart();
                }
                else {
                    mChart_hour.setVisibility(View.GONE);
                }
            }
        });
        switchChartHour2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mChart_hour2.setVisibility(View.VISIBLE);
                    entries.clear();
                    hourChart2();
                }
                else {
                    mChart_hour2.setVisibility(View.GONE);
                }
            }
        });
        switchChartTem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mChart_tem.setVisibility(View.VISIBLE);
                    entries_tem.clear();
                    temChart();
                }
                else {
                    mChart_tem.setVisibility(View.GONE);
                }
            }
        });

    }
    //Kiem tra xem co phai email moi hay khong
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void checkNewDate() {

        information();
        date();
        mDatabase.child(email).child(stringDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> text1 = (HashMap<String, String>) snapshot.getValue();
                if (text1 == null) {
                    today = stringDate;
                    mDatabase.child(email).child(today).child("state").child(stringHour).setValue("off");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Thực hiện chính hẹn giờ
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void checkDate() {
//        date();


        information();
        checkNewDate();
        radio_hand.setChecked(true);
        switchCompat.setVisibility(View.VISIBLE);
        ct_time.setVisibility(View.GONE);
        set_temp.setVisibility(View.GONE);
        mDatabase.child(email).child(stringDate).child("state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> text1 = (HashMap<String, String>) snapshot.getValue();
                if (text1 == null) {
                    today = yesterday;
                }
                else {
                    today = stringDate;
                }
                checkCompat();
                comPat3();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child(email).child(stringDate).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String text1 = (String) snapshot.getValue();
                if (text1 == null) {
                    today = yesterday;
                }
                else {
                    today = stringDate;
                }
                checkCompat();
                comPat3();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //sign out method
    public void signOut() {
        auth.signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void date() {

        Calendar calendar = Calendar.getInstance();
//        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        stringDate = simpleDateFormat.format(calendar.getTime());
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        stringHour = simpleDateFormat.format(calendar.getTime());
        //Giảm 1 ngày
        LocalDate date = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date1 = date.format(formatter);
        LocalDate yesterday1 = LocalDate.parse(date1, formatter);
        yesterday1 = yesterday1.minusDays(1);
//        yesterday = yesterday1.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        yesterday = yesterday1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dayInMonth = firstTwo(yesterday);
    }

    public void information() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        email = email.replaceAll("[\\-\\+\\.\\^:,]", "");
    }

    //lấy 2 phần tử đầu
    public String firstTwo(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    // xem trang thai moi dang nhap vao cua cong tac
    protected void checkCompat() {
//        date();
        information();
        mDatabase.child(email).child(today).child("state").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            // Kiem tra để set trạng thái cho công tắc lúc mới vào

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // text để loại bỏ {} khỏi chuỗi
                if (snapshot.getValue() == null)
                {
                    switchCompat.setChecked(false);
                    return;
                }
                String text = snapshot.getValue().toString().replaceAll("[\\\\[\\\\]{}]", "");

                if (text.contains("on")) {
                    switchCompat.setChecked(true);
                    System.out.println("31");
                } else if(text.contains("off")) {
                    System.out.println("30");
                    switchCompat.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //cua status
        mDatabase.child(email).child(today).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            // Kiem tra để set trạng thái cho công tắc lúc mới vào

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // text để loại bỏ {} khỏi chuỗi
                if (snapshot.getValue() == null)
                {
                    switchCompat.setChecked(false);
                    return;
                }
                String text = snapshot.getValue().toString().replaceAll("[\\\\[\\\\]{}]", "");

                if (text.contains("1")) {
                    switchCompat.setChecked(true);
                    System.out.println("31");
                } else if(text.contains("0")) {
                    System.out.println("30");
                    switchCompat.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    //Thiet lap trang thai,background luc bat tat
    protected void comPat3() {
        information();
        date();
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            // Set trạng thái lúc bật tắt
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Calendar calendar = Calendar.getInstance();
//                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                stringDate = simpleDateFormat.format(calendar.getTime());
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                stringHour = simpleDateFormat.format(calendar.getTime());
                if (isChecked) {
                    btnHeater.setBackgroundResource(R.drawable.nong_lanh_on);
                    mDatabase.child(email).child(stringDate).child("state").child(stringHour).setValue("on");
                    mDatabase.child(email).child(stringDate).child("status").setValue("1");
                } else {
                    btnHeater.setBackgroundResource(R.drawable.nong_lanh_off);
                    mDatabase.child(email).child(stringDate).child("state").child(stringHour).setValue("off");
                    mDatabase.child(email).child(stringDate).child("status").setValue("0");
                }
            }
        });
        setHour();
    }

    private void time() {
        radio_time.setChecked(true);
        ct_time.setVisibility(View.VISIBLE);
        set_temp.setVisibility(View.GONE);
    }
    private void set_temp() {
        mChart_tem.setVisibility(View.GONE);
        mChart_hour.setVisibility(View.GONE);
        mChart_hour2.setVisibility(View.GONE);
        switchChartHour.setVisibility(View.GONE);
        switchChartHour2.setVisibility(View.GONE);
        switchChartTem.setVisibility(View.GONE);
        radio_auto.setChecked(true);
        switchCompat.setVisibility(View.GONE);
        ct_time.setVisibility(View.GONE);
        set_temp.setVisibility(View.VISIBLE);
        mDatabase.child(email).child(stringDate).child("auto").setValue(true);
        btnSetTemp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                UserMainActivity a = new UserMainActivity();
                a.information();
                LocalDate date = LocalDate.now();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                today = date.format(formatter);
                email = a.email;
                temp = editTextTemp.getText().toString();
                int tempInt = parseInt(temp);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(email).child(today).child("tem_auto").setValue(tempInt);
                Toast.makeText(UserMainActivity.this, "Temperature has been set", Toast.LENGTH_LONG).show();
            }
        });

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
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        today = date.format(formatter);
        UserMainActivity a;
        a = new UserMainActivity();
        a.information();
        email = a.email;
        mDatabase.child(email).child(today).child("timer").child("on").setValue(hourStart + ":" + minuteStart);
        mDatabase.child(email).child(today).child("timer").child("off").setValue(hourEnd + ":" + minuteEnd);
        mDatabase.child(email).child(today).child("auto").child("timer").child("on").setValue(hourStart + ":" + minuteStart);
        mDatabase.child(email).child(today).child("auto").child("timer").child("off").setValue(hourEnd + ":" + minuteEnd);
    }


    public void config() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        email = email.replaceAll("[\\-\\+\\.\\^:,]", "");
    }

    //Kiem tra Daily cua trường cuối cùng set trên firebase để set cho ngày hiện tại


    //Kiem tra daily cua ngay cũ nhất xem là on hay off
    public String checkDaily() {
        Query lastQuery = mDatabase.child(email).orderByKey().limitToLast(2);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dayCheck = snapshot.getValue().toString().substring(1,11);
                daily = snapshot.child(dayCheck).child("daily").getValue().toString();
                if (daily.equals("null")) {
                    radio_buttonOff.setChecked(true);
                }
                else if (daily.contains("1")) {
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

    private void radioTimer() {
        radio_group_timer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
    }

    private void setRadioOn() {
        radio_buttonOn.setChecked(true);
        mDatabase.child(email).child(stringDate).child("daily").setValue("1");

    }
    private void setRadioOff() {
        radio_buttonOff.setChecked(true);
        mDatabase.child(email).child(stringDate).child("daily").setValue("0");

    }

    //lay cac ngay on, off
    private void getOnOff() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(email);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    list.add(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Ve bieu do hien gio su dung
    private void hourChart() {
        databaseReference = FirebaseDatabase.getInstance().getReference(email).child("0P1");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                int index = 0;
                String[] timeOn = new String[100];
                for (DataSnapshot ds : snapshot.getChildren()) {

                    BarEntry barEntry = new BarEntry(index, Float.parseFloat(ds.getValue().toString()));
                    barEntries.add(barEntry);
                    labelNames.add(ds.getKey());
                    index++;
                }
                System.out.println(labelNames);
                BarDataSet barDataSet = new BarDataSet(barEntries, "Hours");
                //set color
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                //set draw value
                barDataSet.setDrawValues(false);
                // set bar data
                mChart_hour.setData(new BarData(barDataSet));
                //animation
                XAxis xAxis = mChart_hour.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(labelNames.size());
                xAxis.setLabelRotationAngle(270);
                mChart_hour.animateY(5000);
                mChart_hour.getDescription().setText("Used Time Chart");
                mChart_hour.getDescription().setTextColor(Color.BLUE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void hourChart2() {
        databaseReference = FirebaseDatabase.getInstance().getReference(email).child("0P1");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer a=0;
                if(a>10){
                    entries.removeFirst();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    entries.add(new Entry(a++,Float.parseFloat(ds.getValue().toString())));
                    System.out.println("value:  " + ds.getValue().toString());
                }
                lineDatas_hum.clearValues();
                lineDatas_hum.addDataSet((ILineDataSet) dataChart(entries,10));
                data_hum.setData(lineDatas_hum);
                mChart_hour2.setData(data_hum);
                mChart_hour2.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void temChart() {
        XAxis xAxis = mChart_tem.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(0f);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Integer a=0;
                if(a>5){
                    entries_tem.removeFirst();
                }
                entries_tem.add(new Entry(a++,Float.parseFloat(snapshot.getValue().toString())));
                System.out.println("value:  " + snapshot.getValue().toString());
                lineDatas_tem.clearValues();
                lineDatas_tem.addDataSet((ILineDataSet) dataChart(entries_tem,5));
                data_tem.setData(lineDatas_tem);
                mChart_tem.setData(data_tem);
                mChart_tem.invalidate();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.child(email).child(stringDate).child("0temperature").addChildEventListener(childEventListener);

    }
    private DataSet dataChart(LinkedList<Entry> entries, Integer count) {
        ArrayList<Entry> entries1=new ArrayList<>();
        for(int i=0;i<entries.size();i++){
            entries1.add(new Entry(i,entries.get(i).getY()));
        }
//        Toast.makeText(User_TemHum_Activity.this,i.toString(),Toast.LENGTH_SHORT).show();
        LineData d = new LineData();

        LineDataSet set = new LineDataSet(entries1, "Request Ots approved");
        set.setColor(Color.BLUE);
        set.setLineWidth(2f);
        set.setCircleColor(Color.BLUE);
        set.setCircleRadius(1f);
        set.setFillColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(0f);
        set.setValueTextColor(Color.GREEN);
        d.addDataSet(set);

        return set;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void subtractDate() {
        String s1 = "12:04:45";
        String s2 = "13:04:45";
        LocalTime t1 = LocalTime.parse(s1);
        LocalTime t2 = LocalTime.parse(s2);

        Duration d = Duration.between(t2, t1);
        System.out.println(d); //PT-1H

    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    // lay cac gia tri cua trường thời gian trên firebase ra
    private void setHour() {
        databaseReference = FirebaseDatabase.getInstance().getReference(email).child(stringDate).child("state");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot);
                array1 = new ArrayList<String>();
                array2 = new ArrayList<String>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    array1.add(ds.getValue().toString());
                    String str = ds.getKey();
                    System.out.println("str    :" + str + "   ");
                    array2.add(Float.toString(subtractTime("00:00:00", str)));
                }
                System.out.println("array1        :" + array1);
                System.out.println("array2        :" + array2);
                loop();
                System.out.println("hour        :" + hour);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //tính thời gian bật của ngày đó
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loop() {
        hour = 0;
        int indexOn = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).equals("on")) {
                indexOn = i;
                break;
            }
        }
        for (int i = 1; i < array1.size(); i++) {
            if (array1.get(i-1).equals("off") && array1.get(i).equals("on")) {
                indexOn = i;
            }
            if (array1.get(i-1).equals("on") && array1.get(i).equals("off")) {
                hour += Float.parseFloat(array2.get(i)) - Float.parseFloat(array2.get(indexOn));
            }

        }
        setHourField();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private float subtractTime(String s2, String s1) {
        LocalTime t1 = LocalTime.parse(s1);
        LocalTime t2 = LocalTime.parse(s2);
        Duration d = Duration.between(t2, t1);
        long hours = d.toHours();
        float minutes = (int) (d.toMinutes() % 60);
        float secs = (int) (d.getSeconds() % 60);
        float sub = (minutes*60 + secs)/3600;
        decimalTime = hours + sub;
        decimalTime = Float.parseFloat(new DecimalFormat("##.##").format(decimalTime));
        return decimalTime;
    }

    private void setHourField() {
        System.out.println("hour:         " + hour);
        hour = Math.round(hour*10.0)/10.0;
        mDatabase.child(email).child("0P1").child(stringDate).setValue(hour);
    }
}







