package com.example.unimint.Tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.unimint.Adapters.BudgetPlannerAdapter;
import com.example.unimint.Adapters.TutorialAdapter;
import com.example.unimint.BudgetPlanner.PlannerDashboardActivity;
import com.example.unimint.BudgetPlanner.ViewPlannerDetailsActivity;
import com.example.unimint.Constructors.BudgetPlanner;
import com.example.unimint.Constructors.Tutorial;
import com.example.unimint.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TutorialListViewActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private Button btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private ImageView btnBack;
    private TextView btnNext;
    private ListView tutorialView;
    String txtcategory;

    private DatabaseReference reference;
    private ArrayList<Tutorial> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_list_view);

//        Get put extra value
        Intent intent = getIntent();
        txtcategory = intent.getStringExtra("category");

//        Get exit layouts
        exitDialog = new Dialog(TutorialListViewActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnBack = (ImageView) this.findViewById(R.id.btnBack);
        btnNext = (TextView) this.findViewById(R.id.btnNext);
        tutorialView = (ListView) this.findViewById(R.id.tutorialView);

        getTutorialDetails();

//        Onclick listener for listview
        tutorialView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(TutorialListViewActivity.this, ViewTutorialDetailsActivity.class);
                detailsPage.putExtra("tutorialID", arrayList.get(i).getId());
                startActivity(detailsPage);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialListViewActivity.this, TutorialDashboardActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialListViewActivity.this, AddTutorialActivity.class);
                intent.putExtra("categoryName", txtcategory);
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

//    Show tutorial data on listview
    private void getTutorialDetails()
    {
        arrayList = new ArrayList<>();
        tutorialView.setAdapter(null);
        reference = FirebaseDatabase.getInstance().getReference("Tutorial/");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TutorialAdapter tutorialAdapter = new TutorialAdapter(TutorialListViewActivity.this, R.layout.tutorial_row, arrayList);
                tutorialView.setAdapter(tutorialAdapter);

                for(DataSnapshot data: dataSnapshot.getChildren()){

                    if (data.child("category").getValue().toString().equals(txtcategory)) {
                        arrayList.add(new Tutorial(data.child("id").getValue().toString(),
                                data.child("date").getValue().toString(),
                                data.child("title").getValue().toString(),
                                data.child("description").getValue().toString(),
                                data.child("tutorialUrl").getValue().toString(),
                                data.child("category").getValue().toString(),
                                data.child("categoryName").getValue().toString()));
                        tutorialAdapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}