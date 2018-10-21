package release.saosalvador.com.administradorbiblioteca.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.config.actions.Delete;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class InfoActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String idLivro;
    private Livro livro;
    //---------------------------------------------------------------------------------------------
    private TextView txtNome;
    private TextView txtAutor;
    private TextView txtCategoria;
    private TextView txtAno;
    private TextView txtCurso;
    //----------------------------------------------------------------------------------------------
    private Button buttonOpen;
    private Button buttonEdit;
    private Button buttonDelete;
    //----------------------------------------------------------------------------------------------
    private String TAG;
    private String KEY;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        TAG = getString(R.string.tag_debug);
        KEY = getString(R.string.tag_id);

        buttonOpen = findViewById(R.id.bt_open_book);
        buttonEdit = findViewById(R.id.bt_edit_book);
        buttonDelete =findViewById(R.id.bt_delete_book);
        txtNome = findViewById(R.id.tv_nome);
        txtAutor = findViewById(R.id.tv_autor);
        txtCategoria = findViewById(R.id.tv_categoria);
        txtAno = findViewById(R.id.tv_ano);
        txtCurso = findViewById(R.id.tv_curso);


        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e(TAG,"Não está null");
            idLivro = extra.getString(KEY);
            databaseReference = DAO.getFireBase().child("livros");
        }

        getDatabase1();

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(InfoActivity.this,OpenBookActivity.class);
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
                AlertDialog alerta;
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                builder.setTitle(getString(R.string.button_delete));
                builder.setMessage(getString(R.string.text_dialog)+ livro.getNome()+" ?");
                builder.setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Delete delete =  new Delete(InfoActivity.this,livro,databaseReference);
                        delete.deleteBook();
                        closeActivity();

                    }
                });
                builder.setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alerta = builder.create();
                alerta.show();
            }
        });
    }

    private void getDatabase1(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Log.i(TAG,dataSnapshot.getChildren().toString());
                    Log.i(TAG, idLivro);
                    String key2 = data.getKey();
                    assert key2 != null;
                    if ( key2.equals(idLivro) ) {
                        livro = data.getValue(Livro.class);
                        assert livro != null;
                        txtNome.setText(livro.getNome());
                        txtAutor.setText(livro.getAutor());
                        txtCategoria.setText(livro.getCategoria());
                        txtAno.setText(livro.getAno());
                        txtCurso.setText(livro.getArea());
                        url = livro.getImgDownload();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void closeActivity(){
        this.finish();
    }
}
