package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class editProfilptn extends AppCompatActivity {

    private EditText ExAlamat, ExTelepon, ExLuas, ExTinggi, Pengguna, Jobb, gender;
    private RadioGroup gnder;
    private RadioButton btngnder;
    private CircleImageView Fprofil;
    private ImageButton btnbk;
    private Button Simpan;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageref;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ProgressDialog prosesdialog;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profilptn);

        ExAlamat = findViewById(R.id.iptalamat);
        ExTelepon = findViewById(R.id.iptnomor);
        ExLuas = findViewById(R.id.iptluas);
        ExTinggi = findViewById(R.id.inptinggilhn);
        Fprofil = findViewById(R.id.ptofil);
        Pengguna = findViewById(R.id.Tangoi);
        gender = findViewById(R.id.iptpukiii);
        Jobb = findViewById(R.id.userrr);
        btnbk = findViewById(R.id.tmblkmbli);
        Simpan = findViewById(R.id.btnsave);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageref = storage.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = mAuth.getCurrentUser().getUid();

        prosesdialog = new ProgressDialog(editProfilptn.this);
        prosesdialog.setTitle("Loading");
        prosesdialog.setMessage("Silahkan Tunggu");
        prosesdialog.setCancelable(false);

        StorageReference profilref = storageref.child("Profil"+ mAuth.getCurrentUser().getUid()+ "/profil.jpg");
        profilref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(Fprofil);
            }
        });

        btnbk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), profilpetani.class));
                finish();
            }
        });

        Fprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pilihgambar();

            }
        });

        Simpan.setOnClickListener(view -> {
            if (Pengguna.getText().length() > 0 && Jobb.getText().length() > 0 && ExAlamat.getText().length() > 0 && ExTelepon.getText().length() > 0) {
                if (user != null) {
                    Savedata(userId.toString(), Pengguna.getText().toString(), Jobb.getText().toString(), ExAlamat.getText().toString(), ExTelepon.getText().toString(), gender.getText().toString(), ExLuas.getText().toString(), ExTinggi.getText().toString(), Fprofil.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Silahkan masukkan data utama", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Pilihgambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            uploadGambar(imageUri);

        }
    }

    private void uploadGambar(Uri imageUri) {
        StorageReference profilref = storageref.child("Profil/" + mAuth.getCurrentUser().getUid() + "/profil.jpg");
        profilref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              Toast.makeText(getApplicationContext(),"Gambar diupload", Toast.LENGTH_SHORT).show();
              profilref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                      Picasso.get().load(uri).into(Fprofil);
                  }
              });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Savedata(String userId, String Pengguna, String Jobb, String Alamat, String Telepon, String gender, String Luas, String Tinggi, String Foto) {
        prosesdialog.show();
        Map<String, Object> Petani = new HashMap<>();
        Petani.put("Nama", Pengguna);
        Petani.put("Pekerjaan", Jobb);
        Petani.put("Alamat", Alamat);
        Petani.put("Telepon", Telepon);
        Petani.put("Jenis Kelamin", gender);
        Petani.put("Luas lahan", Luas);
        Petani.put("Tinggi lahan", Tinggi);
        Petani.put("imageUrl", Foto);
        DocumentReference documentReference = db.collection("Profil").document(userId);
        documentReference.set(Petani, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            prosesdialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Gagal disimpan", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}