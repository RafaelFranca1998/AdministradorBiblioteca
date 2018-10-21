/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFViewAct;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class AddBookActivity extends AppCompatActivity {

    private RadioGroup radioGroupArea;
    private String caminhoDoArquivo;
    private String areaSelecionada;
    private Button btSelecionarLivro;
    private Button btAbrirLivro;
    private Button btAdicionar;
    private EditText editTextLivroNome;
    private EditText editTextLivroAutor;
    private EditText editTextLivroCategoria;
    private EditText editTextLivroEditora;
    private EditText editTextLivroAno;
    private String nomelivro;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Livro livro;
    private double progress;
    private ProgressDialog pd;
    private byte[] byteData;
    private File file;
    private Bitmap imagemCapa;
    private View scrollview;
    private ProgressBar mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        radioGroupArea = findViewById(R.id.radio_group_area_add);
        btSelecionarLivro = findViewById(R.id.bt_selecionar_livro);
        btAdicionar = findViewById(R.id.bt_adicionar);
        btAbrirLivro = findViewById(R.id.bt_abrir_livro);
        editTextLivroNome = findViewById(R.id.edit_text__add_livro_nome);
        editTextLivroAutor = findViewById(R.id.edit_text_add_livro_autor);
        editTextLivroCategoria = findViewById(R.id.edit_text_add_livro_categoria);
        editTextLivroEditora = findViewById(R.id.edit_text_add_livro_editora);
        editTextLivroAno = findViewById(R.id.edit_text_add_livro_ano);
        scrollview =  findViewById(R.id.scrollViewAdd);

        radioGroupArea.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_informatica:
                        areaSelecionada = "Informatica";
                        break;
                    case R.id.rb_administracao:
                        areaSelecionada = "Administracao";
                        break;
                    case R.id.rb_direito:
                        areaSelecionada = "Direito";
                        break;
                    case R.id.rb_matematica:
                        areaSelecionada = "Matematica";
                        break;
                    case R.id.rb_saude:
                        areaSelecionada = "Saude";
                        break;
                }
            }
        });

        btAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        btSelecionarLivro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent =  new Intent( Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/*|application/pdf");
                        startActivityForResult(intent,1);
                    }
                }).start();
            }
        });

        btAbrirLivro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (caminhoDoArquivo != null && !caminhoDoArquivo.equals("")) {
                    try {
                        Global.Init(AddBookActivity.this);
                        Intent intent = new Intent();
                        intent.setClass(AddBookActivity.this, PDFViewAct.class);
//                String caminho = data.getData().getPath(); // usar @caminhoDoArquivo
                        intent.putExtra("PDFPath", caminhoDoArquivo);
                        startActivity(intent);
                    } catch (NullPointerException e) {
                        Toast.makeText(AddBookActivity.this, "Selecione um livro.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(AddBookActivity.this, "Selecione um livro.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 & data != null){
            caminhoDoArquivo = data.getData().getPath();
            editTextLivroNome.setText(data.getData().getLastPathSegment());
        }
    }

    private void addBook(){
        try {
            String nomedolivro = editTextLivroNome.getText().toString() ;
            livro = new Livro();
            if (nomedolivro.contains("=")||nomedolivro.contains(",")||
                    nomedolivro.contains("$")||nomedolivro.contains("#")||
                    nomedolivro.contains("[")||nomedolivro.contains("]")) throw  new IllegalArgumentException();
            livro.setNome(nomedolivro);
            livro.setAutor(editTextLivroAutor.getText().toString());
            livro.setArea(areaSelecionada);
            livro.setCategoria(editTextLivroCategoria.getText().toString());
            livro.setEditora(editTextLivroEditora.getText().toString());
            livro.setAno(editTextLivroAno.getText().toString());
            livro.setIdLivro(Base64Custom.codificarBase64(editTextLivroNome.getText().toString()));
            saveBook(this, livro, caminhoDoArquivo);
        } catch (IllegalArgumentException e){
            Toast.makeText(this,"Retire os simbolos = '#', '$', '[', or ']'",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveBook(Context context, Livro livro, String path){
        try {
            Uri uri = Uri.fromFile(new File(path));
            generateImageFromPdf(uri);

            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("application/pdf").build();
            nomelivro = Base64Custom.renoveSpaces(livro.getNome());
            storageReference = DAO.getFirebaseStorage().child("livros").child(livro.getIdLivro()).child(nomelivro);
            livro.setLinkDownload( storageReference.toString() );
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            imagemCapa.compress(Bitmap.CompressFormat.JPEG,40,stream);
            byte[] byteimagem = stream.toByteArray();
            String linkDownload = livro.getLinkDownload();
            linkDownload = linkDownload.replace("gs:/","");
            linkDownload = linkDownload.replace("bibliotecasaosalvador.appspot.com/","");
            storageReference = null;
            storageReference = DAO.getFirebaseStorage().child(linkDownload).child("thumbnail-"+nomelivro);
            livro.setImgDownload(storageReference.toString());
            UploadTask uploadTask2 = storageReference.putBytes(byteimagem);
            uploadTask2.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(AddBookActivity.this,"Imagem enviada",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    e.getMessage();
                }
            });
            storageReference = null;
            storageReference = DAO.getFirebaseStorage().child(linkDownload).child(nomelivro);
            UploadTask uploadTask = storageReference.putFile(uri, metadata);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    pd = new ProgressDialog(AddBookActivity.this);
                    pd.setCancelable(false);
                    pd.setProgress((int)progress);
                    pd.setMessage("Carregando ("+ (int)progress+"%)");
                    pd.show();
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    pd.dismiss();
                    Toast.makeText(AddBookActivity.this,"Falha ao carregar o livro",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    saveInfo(nomelivro);
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    pd.dismiss();
                }
            });

        } catch (NullPointerException e){
            Toast.makeText(context,"É necessário selecionar um arquivo! "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Erro desconhecido! "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void saveInfo(String path){
        try {
            String local = livro.getLinkDownload();
            local = local.replaceAll(path, "");
            databaseReference = DAO.getFireBase().child("livros").child(livro.getIdLivro());
            databaseReference.setValue(livro);
            databaseReference = null;
            databaseReference = DAO.getFireBase().child("categorias").child(livro.getCategoria()).child(livro.getIdLivro());
            databaseReference.setValue(livro);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            pd.dismiss();
            finish();
        }
    }

    void generateImageFromPdf(Uri pdfUri) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            imagemCapa = bmp;
            //        saveImage(bmp);
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            //todo with exception
        }
    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";

    private void saveImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            file = new File(folder, "PDF.png");
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,40,stream);
            byteData = stream.toByteArray();
        } catch (Exception e) {
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
            }
        }
    }
}
