/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.actions.Insert;
import release.saosalvador.com.administradorbiblioteca.config.actions.Update;
import release.saosalvador.com.administradorbiblioteca.model.Category;

public class CategoryEditActivity extends AppCompatActivity {
    private EditText editTextCategoryName;
    private ImageView imageViewCategory;
    private String categoryName;
    private String oldName;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Categoria");
        setSupportActionBar(toolbar);
        editTextCategoryName =  findViewById(R.id.edit_text_category_name);
        imageViewCategory = findViewById(R.id.imageview_category_img);
        Button buttonUpdate = findViewById(R.id.bt_update_category_image);
        Button buttonConcluir = findViewById(R.id.bt_concluir);

        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e("Debug: ","Não está null");
            categoryName = extra.getString("category");
        }

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("categorias")
                .document(categoryName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    category = doc.toObject(Category.class);
                    editTextCategoryName.setText(Objects.requireNonNull(category).getCategoryName());
                    oldName = category.getCategoryName();
                    updateImgAccount();
                } else {
                    Log.w("D", "Error getting documents.", task.getException());
                }
            }
        });


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImg();
            }
        });

        buttonConcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setCategoryName(editTextCategoryName.getText().toString());
                Update update =  new Update();
                update.editCategory(category,oldName);
                update.addOnSuccessListener(new Update.OnUpdateSuccessListener() {
                    @Override
                    public void onCompleteUpdate(UploadTask.TaskSnapshot taskSnapshot) {
                        finish();
                    }
                });
            }
        });
    }

    public void getImg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            Uri caminhoArquivo = data.getData();
            Insert insert =  new Insert(CategoryEditActivity.this);
            insert.saveCategoryImg(category, caminhoArquivo);
            insert.addOnSuccessListener(new Insert.OnSuccessInsertListener() {
                @Override
                public void onCompleteInsert(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    updateImgAccount();
                }
            });
        }
    }

    //TODO tá dando erro
    ProgressDialog pd;
    private void updateImgAccount(){
        try {
            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(category.getImgDownload());
            final long ONE_MEGABYTE = 1024 * 1024;

            pd = new ProgressDialog(CategoryEditActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Carregando");
            pd.show();
            // mProgressBar.setProgress((int)progress);
            reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageViewCategory.setImageBitmap(bitmap);
                    try {
                        pd.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pd.dismiss();
                    Log.e("Erro: ", exception.getMessage());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
