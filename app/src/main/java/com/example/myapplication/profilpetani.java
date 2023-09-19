package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class profilpetani extends AppCompatActivity {
    //Deklarasi Firebase//
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageref;

    //Variabel untuk mengidentifikasi pengguna saat ini
    private FirebaseUser currentuser;
    private static final int PICK_IMAGE_REQUEST = 1;

    //Variabel elemen//
    private TextView iptAlamat, iptTelfon, iptluas, iptTinggi, iptGender, Namapengguna, Email;
    private ImageView Ftoprofil;
    private Uri imageuri;
    private Button btnadd, btnsave;
    private ImageButton btnbck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilpetani);

        iptAlamat = findViewById(R.id.iptalamat);
        iptTelfon = findViewById(R.id.iptnomor);
        iptluas = findViewById(R.id.iptluas);
        iptTinggi = findViewById(R.id.inptinggilhn);
        iptGender = findViewById(R.id.iptJK);
        Namapengguna = findViewById(R.id.Tangoi);
        Email = findViewById(R.id.userrr);

        Ftoprofil = findViewById(R.id.ptofil);

        StorageReference profilref = storageref.child("Profil" + mAuth.getCurrentUser().getUid() + "/profil.jpg");
        profilref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(Ftoprofil);
            }
        });

        btnadd = findViewById(R.id.btnedit);
        btnbck = findViewById(R.id.tmblkmbli);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageref = storage.getReference();
        currentuser = mAuth.getCurrentUser();
        String userId = mAuth.getCurrentUser().getUid();


        db.collection("Profil").document(userId)
                        .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()){
                                                Namapengguna.setText(document.getString("Nama"));
                                                Email.setText(document.getString("Pekerjaan"));
                                                iptAlamat.setText(document.getString("Alamat"));
                                                iptGender.setText(document.getString("Jenis Kelamin"));
                                                iptluas.setText(document.getString("Luas lahan"));
                                                iptTinggi.setText(document.getString("Tinggi lahan"));
                                                iptTelfon.setText(document.getString("Telepon"));
                                            }
                                        }else {
                                            Toast.makeText(getApplicationContext(),"Masukan Data Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

        btnadd.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), editProfilptn.class));
            finish();
        });

        btnbck.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MenuUtama.class));
            finish();
        });
    }
}