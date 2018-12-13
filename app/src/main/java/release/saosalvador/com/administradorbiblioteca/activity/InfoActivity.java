package release.saosalvador.com.administradorbiblioteca.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
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

import java.io.File;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.MyCustomUtil;
import release.saosalvador.com.administradorbiblioteca.config.actions.Delete;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class InfoActivity extends AppCompatActivity {
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseFirestore firebaseFirestore;
    private String idLivro;
    private String livroNome;
    private String linkLivro;
    private Livro  livro;
    //---------------------------------------------------------------------------------------------
    private TextView txtNome;
    private TextView txtAutor;
    private TextView txtCategoria;
    private TextView txtAno;
    private TextView txtCurso;
    private TextView txtSituacao;
    //----------------------------------------------------------------------------------------------
    private Button buttonOpen;
    private Button buttonEdit;
    private Button buttonDelete;
    private Button buttonBaixar;
    private Button buttonDeleteLocal;
    //----------------------------------------------------------------------------------------------
    private String TAG;
    private String KEY;
    private String url;
    //----------------------------------------------------------------------------------------------
    private File livroFile;
    private ProgressDialog dialog;
    private ProgressDialog progressDialogHorizontal;
    private double progress;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
//--------------------------------------------------------------------------------------------------
        TAG = getString(R.string.tag_debug);
        KEY = getString(R.string.tag_id);
//----------------------------------------------VIEWS-----------------------------------------------
        buttonOpen = findViewById(R.id.bt_open_book);
        buttonEdit = findViewById(R.id.bt_edit_book);
        buttonDelete =findViewById(R.id.bt_delete_book);
        buttonBaixar =findViewById(R.id.bt_baixar_livro);
        buttonDeleteLocal =findViewById(R.id.bt_apagar_livro);
        txtNome = findViewById(R.id.tv_nome);
        txtAutor = findViewById(R.id.tv_autor);
        txtCategoria = findViewById(R.id.tv_categoria);
        txtAno = findViewById(R.id.tv_ano);
        txtCurso = findViewById(R.id.tv_curso);
        txtSituacao = findViewById(R.id.tv_situation);
        toolbar =  findViewById(R.id.toolbar);
//-----------------------------------------TOOLBAR--------------------------------------------------
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//--------------------------------------------------------------------------------------------------
        Bundle extra = getIntent().getExtras(); // obtem o id do livro passado na atividade anterior.
        if (extra!= null){
            Log.e(TAG,"Não está null");
            idLivro = extra.getString(KEY);
        }

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(InfoActivity.this,AbrirLivroActivity.class);
                intent.putExtra(KEY,livro.getIdLivro());
                startActivity(intent);
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(InfoActivity.this,EditActivity.class);
                intent.putExtra(KEY,livro.getIdLivro());
                startActivity(intent);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog ad;
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                builder.setTitle(getString(R.string.button_delete));
                builder.setMessage(getString(R.string.text_dialog)+ livro.getNome()+" ?");
                builder.setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Delete delete =  new Delete(InfoActivity.this,livro);
                        delete.deleteBook();
                        delete.addOnSuccessListener(new Delete.OnSuccessDeleteListener() {
                            @Override
                            public void onCompleteInsert(@NonNull Void aVoid) {
                                closeActivity();
                            }
                        });
                    }
                });
                builder.setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                ad = builder.create();
                ad.show();
            }
        });

        buttonBaixar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(linkLivro);
            }
        });

        buttonDeleteLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog ad;
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                builder.setTitle(getString(R.string.button_delete));
                builder.setMessage(getString(R.string.text_dialog)+ livro.getNome()+"da memória ?");
                builder.setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        boolean confirm = deleteLocalFile();
                        if (confirm){
                            Toast.makeText(InfoActivity.this,"Livro apagado da memória com sucesso!",Toast.LENGTH_LONG).show();;
                            txtSituacao.setTextColor(getResources().getColor(R.color.red));
                            txtSituacao.setText(R.string.livro_n_baixado);
                            buttonBaixar.setEnabled(true);
                            buttonDeleteLocal.setEnabled(false);
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                ad = builder.create();
                ad.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDatabase();
    }

    /**
     * Obtem dados do livro selecionado.
     */
    private void getDatabase(){
        createDialog();
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
                            assert livro != null;
                            txtNome.setText(MyCustomUtil.removeLines(livro.getNome()));
                            txtAutor.setText(livro.getAutor());
                            txtCategoria.setText(livro.getCategoria());
                            txtAno.setText(livro.getAno());
                            txtCurso.setText(livro.getArea());
                            url = livro.getImgDownload();
                            livroNome = livro.getNome();
                            linkLivro = livro.getLinkDownload();
                            livroFile = new File(getFilesDir(),idLivro);
                            checkLocalFile();
                            dimissDialog();
                            toolbar.setTitle(MyCustomUtil.removeLines(livroNome));
                        } else {
                            Log.w("D", "Error getting documents.", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dimissDialog();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                dimissDialog();
            }
        });
    }

    /**
     * Cria o progressdialog.
     */
    private void createDialog(){
        dialog =  new ProgressDialog(InfoActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Carregando");
        dialog.create();
        dialog.show();
    }

    /**
     * descarta o progressdialog.
     */
    private void dimissDialog(){
        dialog.dismiss();
    }

    /**
     * checa se o arquivo já existe na memória.
     */
    private void checkLocalFile(){
        if (livroFile.exists()) {
            txtSituacao.setTextColor(getResources().getColor(R.color.green));
            txtSituacao.setText(R.string.livro_baixado);
            buttonBaixar.setEnabled(false);
            buttonDeleteLocal.setEnabled(true);
        } else {
            txtSituacao.setTextColor(getResources().getColor(R.color.red));
            txtSituacao.setText(R.string.livro_n_baixado);
            buttonBaixar.setEnabled(true);
            buttonDeleteLocal.setEnabled(false);
        }
        if (!isConected(this)&& livroFile.exists()){
            buttonOpen.setEnabled(false);
        }else {
            buttonOpen.setEnabled(true);
        }
    }

    /**
     * baixa o livro do servidor de arquivos.
     * @param url link do livro.
     */
    private  void downloadFile(String url) {
        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url + "/livro.pdf");
        livroFile = new File(getFilesDir(), idLivro);
        progressDialogHorizontal = new ProgressDialog(InfoActivity.this);
        islandRef.getFile(livroFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                if (!progressDialogHorizontal.isShowing()) {
                    progressDialogHorizontal.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialogHorizontal.setTitle("Baixando " + livro.getNome());
                    progressDialogHorizontal.setMax((int) taskSnapshot.getTotalByteCount());
                    progressDialogHorizontal.setCancelable(false);
                    progressDialogHorizontal.show();
                }
                progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setProgress((int) progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + livroFile.getAbsolutePath());
                txtSituacao.setTextColor(getResources().getColor(R.color.green));
                txtSituacao.setText("Livro Baixado");
                buttonBaixar.setEnabled(false);
                buttonDeleteLocal.setEnabled(true);
                progressDialogHorizontal.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.getCause();
                exception.printStackTrace();
                progressDialogHorizontal.dismiss();
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (livroFile != null) {
            checkLocalFile();
        }
    }

    /**
     * deleta arquivo local.
     * @return verdadeiro se deletato com sucesso.
     */
    private boolean deleteLocalFile(){
        livroFile = new File(getFilesDir(), idLivro);
        return livroFile.delete();
    }

    /**
     * verifica se o dispositivo esta conectadoa internet.
     * @param cont
     * @return true se estiver conectado.
     */
    public static boolean isConected(Context cont){
        ConnectivityManager conmag = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conmag != null ) {
            conmag.getActiveNetworkInfo();

            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) return true;

            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) return true;
        }
        return false;
    }

    /**
     * fecha a atividade.
     */
    public void closeActivity(){
        this.finish();
    }
}
