/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.storage.UploadTask;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFViewAct;


import java.util.Objects;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.actions.Insert;
import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class AddBookActivity extends AppCompatActivity {

    private String caminhoDoArquivo;
    private String areaSelecionada;
    private EditText editTextLivroNome;
    private EditText editTextLivroAutor;
    private EditText editTextLivroCategoria;
    private EditText editTextLivroEditora;
    private EditText editTextLivroAno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        RadioGroup radioGroupArea = findViewById(R.id.radio_group_area_add);
        Button btSelecionarLivro = findViewById(R.id.bt_selecionar_livro);
        Button btAdicionar = findViewById(R.id.bt_adicionar);
        Button btAbrirLivro = findViewById(R.id.bt_abrir_livro);
        editTextLivroNome = findViewById(R.id.edit_text__add_livro_nome);
        editTextLivroAutor = findViewById(R.id.edit_text_add_livro_autor);
        editTextLivroCategoria = findViewById(R.id.edit_text_add_livro_categoria);
        editTextLivroEditora = findViewById(R.id.edit_text_add_livro_editora);
        editTextLivroAno = findViewById(R.id.edit_text_add_livro_ano);

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
                        intent.putExtra("PDFPath", caminhoDoArquivo);
                        startActivity(intent);
                    } catch (NullPointerException e) {
                        Toast.makeText(AddBookActivity.this, R.string.invalid_path_error, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(AddBookActivity.this, R.string.invalid_path_error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 & data != null){
            caminhoDoArquivo = Objects.requireNonNull(data.getData()).getPath();
            editTextLivroNome.setText(data.getData().getLastPathSegment());
        }
    }

    private void addBook(){
        try {
            String nomedolivro = editTextLivroNome.getText().toString() ;
            Livro livro = new Livro();
            if (nomedolivro.contains("=")||nomedolivro.contains(",")
                    ||nomedolivro.contains("$")||nomedolivro.contains("#")
                    ||nomedolivro.contains("[")||nomedolivro.contains("]")) throw  new IllegalArgumentException();
            livro.setNome(nomedolivro);
            livro.setAutor(editTextLivroAutor.getText().toString());
            livro.setArea(areaSelecionada);
            livro.setCategoria(editTextLivroCategoria.getText().toString());
            livro.setEditora(editTextLivroEditora.getText().toString());
            livro.setAno(editTextLivroAno.getText().toString());
            livro.setIdLivro(Base64Custom.codificarBase64(editTextLivroNome.getText().toString()));
            Category category = new Category();
            category.setCategoryName(editTextLivroCategoria.getText().toString());
            Insert insert =  new Insert(AddBookActivity.this);
            insert.saveBook(livro,caminhoDoArquivo);
            insert.saveCategoryFireStore(category);
            insert.saveInfoFireStore(livro, category);
           // insert.saveInfo(livro,category);
            insert.addOnSuccessSendListener(new Insert.OnSuccessSendListener() {
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
