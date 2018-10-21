/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFViewAct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class OpenBookActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private String idLivro;
    private Livro livro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);

        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e("bundle","Não está null");
            idLivro = extra.getString("id");
            databaseReference = DAO.getFireBase().child("livros");
        }
        getDatabase1();
    }

    private void getDatabase1(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Log.i("Debug:",dataSnapshot.getChildren().toString());
                    Log.i("Debug:", idLivro);
                    String key2 = data.getKey();
                    assert key2 != null;
                    if ( key2.equals(idLivro) ) {
                        livro = data.getValue(Livro.class);
                    }
                }
                if (livro != null){
                    downloadFile(livro.getLinkDownload(),livro.getNome());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //todo escrever pdfs
    public  void writePdf(byte[] pdf,String nome){
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/.Adm/Books";
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir, nome);
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(pdf);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private  void downloadFile2(String url, final String nomeLivro){
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url+"/"+nomeLivro);
                mStorageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                saveFile(bytes,nomeLivro);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void saveFile(byte[] bytes,String n){
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/.Administrador");
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, n);
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(bytes);
            fOut.flush();
            fOut.close();
            abrirLivro(file.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void downloadFile(String url,String nomeLivro) {
        String mNome = Base64Custom.renoveSpaces(nomeLivro);
        StorageReference  islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url+"/"+mNome);

        File rootPath = new File(Environment.getExternalStorageDirectory()+"/.Administrador");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile;

        try {
            localFile = File.createTempFile(nomeLivro,nomeLivro);
            localFile.mkdirs();
            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ",";local tem file created  created " +localFile.toString());
                    //  updateDb(timestamp,localFile.toString(),position);
                    abrirLivro(localFile.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.getCause();
                    exception.printStackTrace();
                    Log.e("firebase ",";local tem file not created  created " +exception.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void abrirLivro(String caminhoDoArquivo){
        if (caminhoDoArquivo != null && !caminhoDoArquivo.equals("")) {
            try {
                Global.Init(OpenBookActivity.this);
                Intent intent = new Intent();
                intent.setClass(this, PDFViewAct.class);
                intent.putExtra("PDFPath", caminhoDoArquivo);
                startActivity(intent);
            } catch (NullPointerException e) {
                Toast.makeText(this, "Selecione um livro.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "Selecione um livro.", Toast.LENGTH_LONG).show();
        }
    }
}
