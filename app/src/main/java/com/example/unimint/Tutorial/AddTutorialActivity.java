package com.example.unimint.Tutorial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.unimint.BudgetPlanner.AddPlannerActivity;
import com.example.unimint.Constructors.Tutorial;
import com.example.unimint.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTutorialActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private Button btnExitYes, btnExitNo, btnAdd;
    private Dialog exitDialog;
    private ImageView btnBack;
    private VideoView videoView;
    private TextInputLayout txtTitle, txtDesc;
    private TextView categorySelect;
    Uri videoUri;
    MediaController mediaController;
    UploadTask uploadTask;

    private DatabaseReference reference;
    private StorageReference storageReference;

    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tutorial);

//        Get put extra value
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("categoryName").toString();

//        Get exit layouts
        exitDialog = new Dialog(AddTutorialActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnBack = (ImageView) this.findViewById(R.id.btnBack);
        btnAdd = (Button) this.findViewById(R.id.btnAdd);
        txtTitle = (TextInputLayout) this.findViewById(R.id.txtTitle);
        txtDesc = (TextInputLayout) this.findViewById(R.id.txtDesc);
        categorySelect = (TextView) this.findViewById(R.id.categorySelect);
        videoView = (VideoView) this.findViewById(R.id.videoView);
        mediaController = new MediaController(AddTutorialActivity.this);
        videoView.setMediaController(mediaController);
        videoView.start();

        storageReference = FirebaseStorage.getInstance().getReference("Tutorial/");
        showCategory();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddTutorialActivity.this, TutorialListViewActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopUp();
                chooseVideo();
            }
        });

//        Add new tutorial to firebase
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();
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

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    private String getExtra(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    private void uploadVideo()
    {
        String title = txtTitle.getEditText().getText().toString();
        String desc = txtDesc.getEditText().getText().toString();

//        Get today date
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String dateKey = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

        if (title.isEmpty() || desc.isEmpty()){
            Toast.makeText(AddTutorialActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
        } else if (videoUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(AddTutorialActivity.this);
            progressDialog.setTitle("Uploading Video");
            progressDialog.setMessage("Please wait. Video is Uploading..");
            progressDialog.show();

            final StorageReference stRef = storageReference.child(dateKey+"."+getExtra(videoUri));
            uploadTask = stRef.putFile(videoUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return stRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                progressDialog.dismiss();
                                String nameOfCategory = (String) categorySelect.getText();

                                Tutorial tutorial = new Tutorial(dateKey,date,title,desc,downloadUrl.toString(), categoryName, nameOfCategory);
                                reference = FirebaseDatabase.getInstance().getReference("Tutorial/");
                                reference.child(dateKey).setValue(tutorial).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(AddTutorialActivity.this, "Video Uploaded!", Toast.LENGTH_SHORT).show();
                                        txtTitle.getEditText().setText("");
                                        txtDesc.getEditText().setText("");
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AddTutorialActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(AddTutorialActivity.this, "Please upload video!", Toast.LENGTH_SHORT).show();
        }
    }

//    Method for show category
    private void showCategory()
    {
        if (categoryName.equals("identity")){
            categorySelect.setText("Identify your expenses");
        } else if (categoryName.equals("cutoff")){
            categorySelect.setText("Cutoff your extra expenses");
        } else if (categoryName.equals("execution")){
            categorySelect.setText("Execution");
        } else if (categoryName.equals("continueProcess")){
            categorySelect.setText("Continue the process");
        }
    }
}