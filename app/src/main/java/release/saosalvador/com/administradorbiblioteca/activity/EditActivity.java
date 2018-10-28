/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.config.actions.Insert;
import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class EditActivity extends AppCompatActivity {
    private RadioGroup radioGroupArea;
    private String areaSelecionada;
    private Button btEdit;
    private EditText editTextLivroNome;
    private EditText editTextLivroAutor;
    private EditText editTextLivroCategoria;
    private EditText editTextLivroEditora;
    private EditText editTextLivroAno;
    private DatabaseReference databaseReference;
    private DatabaseReference categoryReference;
    private Livro livro;
    private Category category;
    private String idLivro;
    private String categoryName;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        radioGroupArea = findViewById(R.id.radio_group_area_edit);
        btEdit = findViewById(R.id.bt_edit);
        editTextLivroNome = findViewById(R.id.edit_text_livro_nome_edit);
        editTextLivroAutor = findViewById(R.id.edit_text_livro_autor_edit);
        editTextLivroCategoria = findViewById(R.id.edit_text_livro_categoria_edit);
        editTextLivroEditora = findViewById(R.id.edit_text_livro_editora_edit);
        editTextLivroAno = findViewById(R.id.edit_text_livro_ano_edit);

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

        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (livro!=null) {
                    editarLivro();
                }
            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e("bundle","Não está null");
            idLivro = extra.getString("id");
        }
        getDatabase1();

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
                            livro = doc.toObject(Livro.class);
                            editTextLivroNome.setText(livro.getNome());
                            editTextLivroAutor.setText(livro.getAutor());
                            editTextLivroCategoria.setText(livro.getCategoria());
                            editTextLivroEditora.setText(livro.getEditora());
                            editTextLivroAno.setText(livro.getAno());
                            categoryName = livro.getCategoria();
                            getDatabase2();
                        } else {
                            Log.w("D", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void getDatabase2(){
        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("categorias")
                .document(categoryName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            category = doc.toObject(Category.class);
                        } else {
                            Log.w("D", "Error getting documents.", task.getException());
                        }
                    }
                });


//        categoryReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                category = dataSnapshot.getValue(Category.class);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void editarLivro(){
        try {
            String nomedolivro = editTextLivroNome.getText().toString() ;
            if (nomedolivro.contains("=")||nomedolivro.contains(",")||
                    nomedolivro.contains("$")||nomedolivro.contains("#")||
                    nomedolivro.contains("[")||nomedolivro.contains("]")) throw  new IllegalArgumentException();
            livro.setNome(nomedolivro);
            livro.setAutor(editTextLivroAutor.getText().toString());
            livro.setArea(areaSelecionada);
            livro.setCategoria(editTextLivroCategoria.getText().toString());
            livro.setEditora(editTextLivroEditora.getText().toString());
            livro.setAno(editTextLivroAno.getText().toString());
            livro.setIdLivro(livro.getIdLivro());
            Insert insert =  new Insert(EditActivity.this);
            //testa se a categoria existe.
            try {
                String s = category.getCategoryName();
            } catch (NullPointerException e){
                e.printStackTrace();
            } finally {
                category =  new Category();
                category.setCategoryName(livro.getCategoria());
                insert.saveCategoryFireStore(category);
            }
            insert.saveInfoFireStore(livro,category);
            insert.addOnSuccessListener(new Insert.OnSuccessInsertListener() {
                @Override
                public void onCompleteInsert(UploadTask.TaskSnapshot taskSnapshot) {
                    finish();
                }
            });
        } catch (IllegalArgumentException e){
            Toast.makeText(this,"Retire os simbolos = '#', '$', '[', or ']'",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
