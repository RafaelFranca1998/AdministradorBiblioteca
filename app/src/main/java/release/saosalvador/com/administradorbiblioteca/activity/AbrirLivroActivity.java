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
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class AbrirLivroActivity extends AppCompatActivity {
    private String idLivro;
    private Livro livro;
    private ProgressDialog dialog;
    private double progress;
    private static boolean isFirstOpen =  true;


    @Override
    protected void onStart() {
        super.onStart();
        if (isFirstOpen){//checa se o livro está sendo abreto ou fechado.
            getDatabase();
            isFirstOpen = false;
        }else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abrir_livro);

        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e("bundle","Não está null");
            idLivro = extra.getString("id");
        }
    }

    /**
     * Obtem dados do livro.
     */
    private void getDatabase(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
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
                                downloadFile(livro.getLinkDownload());
                            }
                        } else {
                            Log.w("D", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private File bookFile;

    /**
     * Baixa o livro do servidor de arquivos.
     * @param url
     */
    private  void downloadFile(String url) {
        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url + "/livro.pdf");
        bookFile = new File(getFilesDir(), idLivro);

        if (bookFile.exists()) {
            abrirLivro(bookFile.getAbsolutePath());
        } else{

            dialog = new ProgressDialog(AbrirLivroActivity.this);

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

    /**
     * Abre o arquivo salvo na  memoria do celular.
     * @param caminhoDoArquivo
     */
    private void abrirLivro(String caminhoDoArquivo){
        if (caminhoDoArquivo != null && !caminhoDoArquivo.equals("")) {
            try {
                Global.Init(AbrirLivroActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFirstOpen = true;
    }
}
