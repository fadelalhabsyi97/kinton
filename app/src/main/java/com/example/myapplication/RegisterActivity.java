package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.Transliterator;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.model.User.Helperclass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;

public class RegisterActivity extends AppCompatActivity {

    private EditText InputName, InputUsername, Inputpassword, Inputpasswordconf, Inputpekerjaan;
    private Button btnRegister, btnDone;
    private ImageButton btnProfil;
    private CheckBox lihatpass1, lihatpass2;
    private ProgressDialog prosesDialog;
    private FirebaseAuth mAuth;
    private String userID;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InputName = findViewById(R.id.Nama);
        Inputpekerjaan = findViewById(R.id.Jobb);
        InputUsername = findViewById(R.id.username);
        Inputpassword = findViewById(R.id.Password);
        Inputpasswordconf = findViewById(R.id.ulangipassword);
        btnRegister = findViewById(R.id.Daftar);
        btnDone = findViewById(R.id.Sudah);
        lihatpass1 = findViewById(R.id.pendo);
        lihatpass2 = findViewById(R.id.pendo2);

        mAuth = FirebaseAuth.getInstance();
        prosesDialog = new ProgressDialog(RegisterActivity.this);
        prosesDialog.setTitle("Loading");
        prosesDialog.setMessage("Silahkan Tunggu");
        prosesDialog.setCancelable(false);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MenuUtama.class));
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InputName.length() > 0 && Inputpassword.length() > 0) {
                    registerUser(InputName.getText().toString(), Inputpekerjaan.getText().toString(), InputUsername.getText().toString(), Inputpassword.getText().toString());
                        SaveData(InputName.getText().toString(), Inputpekerjaan.getText().toString(), InputUsername.getText().toString(), Inputpassword.getText().toString());
                        finish();
                }else {
                    Toast.makeText(getApplicationContext(),"Silahkan isi semua data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lihatpass1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (lihatpass1.isChecked()) {
                    Inputpassword.setTransformationMethod(null);
                } else {
                    Inputpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//lihat pass//
                }
            }
        });
        lihatpass2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (lihatpass2.isChecked()) {
                    Inputpasswordconf.setTransformationMethod(null);
                } else {
                    Inputpasswordconf.setTransformationMethod(PasswordTransformationMethod.getInstance());//lihat pass//
                }
            }
        });

        btnDone.setOnClickListener(v -> {
            finish();
        });
    }

    private void registerUser (String Nama, String Pekerjaan, String Username, String Password){
        prosesDialog.show();
        mAuth.createUserWithEmailAndPassword(Username, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult() !=null){
                            FirebaseUser user = task.getResult().getUser();
                            if (user !=null){
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(Nama)
                                        .build();
                            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    reload();
                                }
                            });
                            }
                        }
                        prosesDialog.dismiss();
                    }
                });
    }

    private void SaveData(String InputName, String Inputpekerjaan, String InputUsername, String Inputpassword) {
        ///simpan data///
        Map<String, Object> user = new HashMap<>();
        user.put("Nama", InputName);
        user.put("Pekerjaan", Inputpekerjaan);
        user.put("Username", InputUsername);
        user.put("Password", Inputpassword);
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void reload(){
        startActivity(new Intent(getApplicationContext(), MenuUtama.class));
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
}