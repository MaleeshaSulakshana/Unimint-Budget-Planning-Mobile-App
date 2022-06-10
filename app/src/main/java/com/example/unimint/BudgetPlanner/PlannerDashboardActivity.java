package com.example.unimint.BudgetPlanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.unimint.Adapters.BudgetPlannerAdapter;
import com.example.unimint.Constructors.BudgetPlanner;
import com.example.unimint.DashboardActivity;
import com.example.unimint.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlannerDashboardActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private TextView btnNext, dateSelector;
    private ListView plannerView;
    private Button btnExitYes, btnExitNo;
    private ImageView btnBack;
    private Dialog exitDialog;

    Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private String date;
    private DatabaseReference reference;
    private ArrayList<BudgetPlanner> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_dashboard);

//        Get exit layouts
        exitDialog = new Dialog(PlannerDashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnNext = (TextView) findViewById(R.id.btnNext);
        plannerView = (ListView) findViewById(R.id.plannerView);
        dateSelector = (TextView) findViewById(R.id.dateSelector);

//        Get today date
        date = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        dateSelector.setText(date);

//        Show data on list view
        getBudgetPlannerDetails();

//        Onclick listener for listview
        plannerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(PlannerDashboardActivity.this, ViewPlannerDetailsActivity.class);
                detailsPage.putExtra("plannerID", arrayList.get(i).getId());
                detailsPage.putExtra("month", arrayList.get(i).getMonth());
                startActivity(detailsPage);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

//        Show date picker
        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(PlannerDashboardActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlannerDashboardActivity.this, DashboardActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlannerDashboardActivity.this, AddPlannerActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

//    Hide status and navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

//    For exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

    private void updateLabel() {
        String mFormat = "yyyy-MM";
        SimpleDateFormat sdf = new SimpleDateFormat(mFormat, Locale.US);
        dateSelector.setText(sdf.format(mCalendar.getTime()));
        getBudgetPlannerDetails();
    }

//    Show budget planning data on listview
    private void getBudgetPlannerDetails()
    {
        arrayList = new ArrayList<>();
        plannerView.setAdapter(null);
        String date = dateSelector.getText().toString();
        reference = FirebaseDatabase.getInstance().getReference("Budget_Planner/");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BudgetPlannerAdapter budgetPlannerAdapter = new BudgetPlannerAdapter(PlannerDashboardActivity.this, R.layout.budget_planner_row, arrayList);
                plannerView.setAdapter(budgetPlannerAdapter);

                for(DataSnapshot data: dataSnapshot.getChildren()){

                    if (data.getKey().equals(date)) {

                        for(DataSnapshot dataSnapshot1: data.getChildren()){
                            arrayList.add(new BudgetPlanner(dataSnapshot1.child("id").getValue().toString(),
                                    dataSnapshot1.child("month").getValue().toString(),
                                    dataSnapshot1.child("title").getValue().toString(),
                                    "Rs. "+dataSnapshot1.child("amount").getValue().toString()));
                            budgetPlannerAdapter.notifyDataSetChanged();
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}