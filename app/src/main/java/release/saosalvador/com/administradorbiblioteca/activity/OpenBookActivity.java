/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFViewAct;

import java.io.File;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class OpenBookActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;
    private String idLivro;
    private Livro livro;
    private ProgressDialog dialog;
    private double progress;
    private static boolean isFirstOpen =  true;


    @Override
    protected void onStart() {
        super.onStart();
        if (isFirstOpen){
            getDatabase1();
            isFirstOpen = false;
        }else {
            finish();
        }
    }

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

    }


    private void getDatabase1(){
        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("livros")
                .document(idLivro)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            assert doc != null;
                            livro = doc.toObject(Livro.class);
                            if (livro != null){
                                downloadFile(livro.getLinkDownload(),livro.getNome());
                            }
                        } else {
                            Log.w("D", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private File bookFile;

    private  void downloadFile(String url, final String nomeLivro) {
        String mNome = Base64Custom.renoveSpaces(nomeLivro);
        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url + "/" + mNome);

        bookFile = new File(getFilesDir(), mNome);

        if (bookFile.exists()) {
            abrirLivro(bookFile.getAbsolutePath());
        } else{

            dialog = new ProgressDialog(OpenBookActivity.this);

            islandRef.getFile(bookFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if (!dialog.isShowing()) {
                        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        dialog.setTitle("Baixando " + livro.getNome());
                        dialog.setMax((int) taskSnapshot.getTotalByteCount());
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    dialog.setProgress((int) progress);
                }
            }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ", ";local tem file created  created " + bookFile.getAbsolutePath());
                    //  updateDb(timestamp,localFile.toString(),position);
                    abrirLivro(bookFile.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.getCause();
                    exception.printStackTrace();
                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                }
            });

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
