package com.example.unimint.BudgetPlanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unimint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPlannerDetailsActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private TextView dateSelector;
    private Button btnExitYes, btnExitNo, btnDeleteYes, btnDeleteNo, btnUpdate, btnRemove;
    private ImageView btnBack;
    private Dialog exitDialog, deleteDialog;
    private TextInputLayout txtTitle, txtAmount;

    private DatabaseReference reference;
    private String plannerID, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_planner_details);


//        Get put extra value
        Intent intent = getIntent();
        plannerID = intent.getStringExtra("plannerID");
        month = intent.getStringExtra("month");

//        Show planner details
        showPlannerDetails();

//        Get exit layouts
        exitDialog = new Dialog(ViewPlannerDetailsActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        deleteDialog = new Dialog(ViewPlannerDetailsActivity.this);
        deleteDialog.setContentView(R.layout.delete_confirmation_box);

        btnBack = (ImageView) this.findViewById(R.id.btnBack);
        btnUpdate = (Button) this.findViewById(R.id.btnUpdate);
        btnRemove = (Button) this.findViewById(R.id.btnRemove);
        txtTitle = (TextInputLayout) this.findViewById(R.id.txtTitle);
        txtAmount = (TextInputLayout) this.findViewById(R.id.txtAmount);
        dateSelector = (TextView) this.findViewById(R.id.dateSelector);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPreviousPage();
            }
        });

//        For update budget planner details
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlannerDetails();
            }
        });

//        For remove budget planner details
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlannerDetails();
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

    private void navigateToPreviousPage()
    {
        startActivity(new Intent(ViewPlannerDetailsActivity.this, PlannerDashboardActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void showPlannerDetails()
    {

        reference = FirebaseDatabase.getInstance().getReference("Budget_Planner/"+month+"/"+plannerID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {

                    if (snapshot.child("title").exists()){
//                       Set value to text boxes
                        txtTitle.getEditText().setText(snapshot.child("title").getValue().toString());
                        txtAmount.getEditText().setText(snapshot.child("amount").getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for update planner details
    private void updatePlannerDetails()
    {

        String title = txtTitle.getEditText().getText().toString();
        String amount = txtAmount.getEditText().getText().toString();

//        Check fields empty
        if (title.isEmpty() || amount.isEmpty())
        {
            Toast.makeText(ViewPlannerDetailsActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();
        } else {

            reference = FirebaseDatabase.getInstance().getReference("Budget_Planner/"+month+"/"+plannerID);
            reference.child("title").setValue(title);
            reference.child("amount").setValue(amount);

            Toast.makeText(ViewPlannerDetailsActivity.this, "Budget planner details updated successful!", Toast.LENGTH_SHORT).show();
        }

    }

//    Method for remove planner details
    private void removePlannerDetails()
    {

        deleteDialog.show();

        btnDeleteYes = (Button) deleteDialog.findViewById(R.id.btnYes);
        btnDeleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Budget_Planner/"+month+"/"+plannerID);
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ViewPlannerDetailsActivity.this, "Budget Planner details remove successful!", Toast.LENGTH_SHORT).show();
                            textClear();
                            navigateToPreviousPage();
                        } else {
                            Toast.makeText(ViewPlannerDetailsActivity.this, "Budget Planner details remove not successful!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        btnDeleteNo = (Button) deleteDialog.findViewById(R.id.btnNo);
        btnDeleteNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

    }

//    Clear text fields
    private void textClear()
    {
        txtTitle.getEditText().setText("");
        txtAmount.getEditText().setText("");
    }


}