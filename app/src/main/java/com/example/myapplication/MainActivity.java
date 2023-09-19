package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextView register;
    private EditText InputUsername, Inputpassword;
    private String Username, password;
    private Button btnMasuk, btnBA;
    private CheckBox lihatpass;
    private ProgressDialog prosesdialog;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///Input pass dan user///
        InputUsername = findViewById(R.id.Nama);
        Inputpassword = findViewById(R.id.Password);
        btnMasuk = findViewById(R.id.Masuk);
        btnBA = findViewById(R.id.Buat_akun);
        lihatpass = findViewById(R.id.cuki);

        mAuth = FirebaseAuth.getInstance();
        prosesdialog = new ProgressDialog(MainActivity.this);
        prosesdialog.setTitle("Loading");
        prosesdialog. setMessage("Silahkan Tunggu");
        prosesdialog.setCancelable(false);


        lihatpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (lihatpass.isChecked()){
                    Inputpassword.setTransformationMethod(null); ///Lihat pass///
                }else {
                    Inputpassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //sembunyikan Password//
                }
            }
        });
        btnBA.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
        btnMasuk.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MenuUtama.class));
        });

        btnMasuk.setOnClickListener(v -> {
                if (InputUsername.getText().length() > 0 && Inputpassword.getText().length() > 0) {
                    Masuk(InputUsername.getText().toString(), Inputpassword.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Silahkan Isi Semua Data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        private void Masuk(String Username, String password){
        prosesdialog.show();
            ///Coding Login///
            mAuth.signInWithEmailAndPassword(Username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful() && task.getResult()!=null){
                    if (task.getResult()!=null){
                        reload();
                    }else{
                        Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_SHORT).show();
                    }
                   }else{
                       Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_SHORT).show();
                   }
                   prosesdialog.dismiss();
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