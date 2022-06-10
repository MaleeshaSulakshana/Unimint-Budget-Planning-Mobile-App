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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unimint.Constructors.BudgetPlanner;
import com.example.unimint.DashboardActivity;
import com.example.unimint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPlannerActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private TextView dateSelector;
    private Button btnExitYes, btnExitNo, btnAdd;
    private ImageView btnBack;
    private Dialog exitDialog;
    private TextInputLayout txtTitle, txtAmount;

    Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private DatabaseReference reference;

    private String month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planner);


//        Get exit layouts
        exitDialog = new Dialog(AddPlannerActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnBack = (ImageView) this.findViewById(R.id.btnBack);
        btnAdd = (Button) this.findViewById(R.id.btnAdd);
        txtTitle = (TextInputLayout) this.findViewById(R.id.txtTitle);
        txtAmount = (TextInputLayout) this.findViewById(R.id.txtAmount);
        dateSelector = (TextView) this.findViewById(R.id.dateSelector);

//        Get today date
        month = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        dateSelector.setText(month);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPlannerActivity.this,
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
                startActivity(new Intent(AddPlannerActivity.this, PlannerDashboardActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

//        For add new planner
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPlannerItem();
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
    }

//    Method for add new budget planning item
    private void addNewPlannerItem()
    {
        String title = txtTitle.getEditText().getText().toString();
        String amount = txtAmount.getEditText().getText().toString();

//        Validate fields
        if (title.isEmpty() || amount.isEmpty()){
            Toast.makeText(AddPlannerActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
        } else {
            String id = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String monthKey = dateSelector.getText().toString();
            BudgetPlanner budgetPlanner = new BudgetPlanner(id, month ,title, amount);
            reference = FirebaseDatabase.getInstance().getReference("Budget_Planner/"+monthKey);
            reference.child(id).setValue(budgetPlanner).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddPlannerActivity.this, "Budget planner item added successful!", Toast.LENGTH_SHORT).show();
                        txtTitle.getEditText().setText("");
                        txtAmount.getEditText().setText("");
                    } else {
                        Toast.makeText(AddPlannerActivity.this, "Budget planner item added not successful!", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }

    }

}