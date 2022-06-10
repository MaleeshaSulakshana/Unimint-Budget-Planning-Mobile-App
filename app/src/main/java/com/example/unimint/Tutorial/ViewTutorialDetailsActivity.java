package com.example.unimint.Tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.unimint.BudgetPlanner.ViewPlannerDetailsActivity;
import com.example.unimint.Constructors.BudgetPlanner;
import com.example.unimint.Constructors.Tutorial;
import com.example.unimint.DashboardActivity;
import com.example.unimint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewTutorialDetailsActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private Button btnExitYes, btnExitNo, btnDeleteYes, btnDeleteNo, btnUpdate, btnRemove;
    private Dialog exitDialog, deleteDialog;
    private ImageView btnBack;
    private TextInputLayout txtTitle, txtDesc;
    private TextView dateSelector;
    private VideoView videoView;

    private DatabaseReference reference;

    String tutorialID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutorial_details);

//        Get put extra value
        Intent intent = getIntent();
        tutorialID = intent.getStringExtra("tutorialID");

//        Get exit layouts
        exitDialog = new Dialog(ViewTutorialDetailsActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        deleteDialog = new Dialog(ViewTutorialDetailsActivity.this);
        deleteDialog.setContentView(R.layout.delete_confirmation_box);

        btnBack = (ImageView) this.findViewById(R.id.btnBack);
        btnUpdate = (Button) this.findViewById(R.id.btnUpdate);
        btnRemove = (Button) this.findViewById(R.id.btnRemove);
        txtTitle = (TextInputLayout) this.findViewById(R.id.txtTitle);
        txtDesc = (TextInputLayout) this.findViewById(R.id.txtDesc);
        dateSelector = (TextView) this.findViewById(R.id.dateSelector);
        videoView = (VideoView) this.findViewById(R.id.videoView);

        showDetails();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewTutorialDetailsActivity.this, TutorialDashboardActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDetails();
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

    private void showDetails()
    {

        reference = FirebaseDatabase.getInstance().getReference("Tutorial/"+tutorialID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {

//                   Set value to text boxes
                    if (snapshot.child("title").exists()){
                        txtTitle.getEditText().setText(snapshot.child("title").getValue().toString());
                        txtDesc.getEditText().setText(snapshot.child("description").getValue().toString());
                        dateSelector.setText(snapshot.child("categoryName").getValue().toString());
                        videoView.setVideoPath(snapshot.child("tutorialUrl").getValue().toString());
                        videoView.setMediaController(new MediaController(ViewTutorialDetailsActivity.this));
                        videoView.requestFocus();
                        videoView.start();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for update details
    private void updateDetails()
    {

        String title = txtTitle.getEditText().getText().toString();
        String amount = txtDesc.getEditText().getText().toString();

//        Check fields empty
        if (title.isEmpty() || amount.isEmpty())
        {
            Toast.makeText(ViewTutorialDetailsActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();
        } else {

            reference = FirebaseDatabase.getInstance().getReference("Tutorial/"+tutorialID);
            reference.child("title").setValue(title);
            reference.child("description").setValue(amount);

            Toast.makeText(ViewTutorialDetailsActivity.this, "Details updated successful!", Toast.LENGTH_SHORT).show();
        }

    }

//    Method for remove details
    private void removeDetails()
    {

        deleteDialog.show();

        btnDeleteYes = (Button) deleteDialog.findViewById(R.id.btnYes);
        btnDeleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Tutorial/"+tutorialID);
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ViewTutorialDetailsActivity.this, "Details remove successful!", Toast.LENGTH_SHORT).show();
                            textClear();

                            startActivity(new Intent(ViewTutorialDetailsActivity.this, TutorialDashboardActivity.class));
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(ViewTutorialDetailsActivity.this, "Details remove not successful!", Toast.LENGTH_SHORT).show();

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
        txtDesc.getEditText().setText("");
    }
}