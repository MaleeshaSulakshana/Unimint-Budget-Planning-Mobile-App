package com.example.unimint.Tutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.unimint.BudgetPlanner.AddPlannerActivity;
import com.example.unimint.BudgetPlanner.PlannerDashboardActivity;
import com.example.unimint.DashboardActivity;
import com.example.unimint.R;

public class TutorialDashboardActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private Button btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private ImageView btnBack;
    private LinearLayout identity, cutoff, execution ,continueProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_dashboard);

//        Get exit layouts
        exitDialog = new Dialog(TutorialDashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnBack = (ImageView) this.findViewById(R.id.btnBack);
        identity = (LinearLayout) this.findViewById(R.id.identity);
        cutoff = (LinearLayout) this.findViewById(R.id.cutoff);
        execution = (LinearLayout) this.findViewById(R.id.execution);
        continueProcess = (LinearLayout) this.findViewById(R.id.continueProcess);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialDashboardActivity.this, DashboardActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        identity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialDashboardActivity.this, TutorialListViewActivity.class);
                intent.putExtra("category", "identity");
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cutoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialDashboardActivity.this, TutorialListViewActivity.class);
                intent.putExtra("category", "cutoff");
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        execution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialDashboardActivity.this, TutorialListViewActivity.class);
                intent.putExtra("category", "execution");
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        continueProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialDashboardActivity.this, TutorialListViewActivity.class);
                intent.putExtra("category", "continueProcess");
                startActivity(intent);
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
}