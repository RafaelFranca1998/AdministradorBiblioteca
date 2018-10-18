package release.saosalvador.com.administradorbiblioteca;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import release.saosalvador.com.administradorbiblioteca.config.DAO;
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
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        txtNome = findViewById(R.id.tv_nome);
        txtAutor = findViewById(R.id.tv_autor);
        txtCategoria = findViewById(R.id.tv_categoria);
        txtAno = findViewById(R.id.tv_ano);
        txtCurso = findViewById(R.id.tv_curso);


        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e("Aqui:","Não está null");
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
        this.finish();
    }
}
