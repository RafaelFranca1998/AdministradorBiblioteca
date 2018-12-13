/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.actions.Insert;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.AdapterRecyclerViewCategory;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.RecyclerItemClickListener;
import release.saosalvador.com.administradorbiblioteca.model.Categorias;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class CategoriasActivity extends AppCompatActivity {
    private List<Categorias> categoriasList;
    private RecyclerView listView;
    private AdapterRecyclerViewCategory adapterListView;
    private int itemPosition;
    private FirebaseFirestore firebaseFirestore;
    private boolean isFirstOpen =  true;

    @Override
    protected void onPause() {
        super.onPause();
        getDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFirstOpen =  true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        //------------------------------------------------------------------------------------------
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Categorias disponiveis");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //------------------------------------------------------------------------------------------
        categoriasList =  new ArrayList<>();
        listView = findViewById(R.id.recycler_view_category);
        adapterListView =  new AdapterRecyclerViewCategory(CategoriasActivity.this, categoriasList);
        //------------------------------------------------------------------------------------------
        getDatabase();

        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        listView.setLayoutManager(gridLayoutManager);



        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(CategoriasActivity.this,EditarCategoriasActivity.class);
                        String categoryName = categoriasList.get(position).getCategoryName();
                        intent.putExtra("category", categoryName);
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        itemPosition = position;
                    }
                })
        );
    }
    //---------------------------------Obtem dados do banco-----------------------------------------
    private void getDatabase(){
        firebaseFirestore =  FirebaseFirestore.getInstance();

        firebaseFirestore.collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categoriasList.clear();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        categoriasList.add(document.toObject(Categorias.class)) ;
                    }
                    if (isFirstOpen) {
                        checkCategory();
                        isFirstOpen = false;
                    }
                    adapterListView.notifyDataSetChanged();
                } else {
                    Log.w("D", "Error getting documents.", task.getException());
                }
            }
        });
    }
    //----------------------------------Obtem dados dos livros--------------------------------------
    /**
     * Obtem as categorias de todos os livros.
     */
    private void checkCategory(){
        final ArrayList<String> strings =  new ArrayList<>();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("livros")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Livro livro = document.toObject(Livro.class);
                                strings.add(livro.getCategoria()) ;
                                Log.i("Debug Livros",livro.getCategoria());
                            }
                            adapterListView.notifyDataSetChanged();
                        } else {
                            Log.w("D", "Error getting documents.", task.getException());
                        }
                        createCategory(removerDuplicados(strings));
                    }
                });
    }
    //---------------------------------Remove categorias repetidas----------------------------------

    /**
     * Remove categorias repetidas.
     * @param list
     * @return
     */
    private ArrayList removerDuplicados(ArrayList list) {
        for (int i = 0; i < list.size(); i++) {
            Object a = list.get(i);
            for (int j = i+1; j < list.size(); j++) {
                Object b = list.get(j);
                if (a.equals(b)) {
                    Log.i("Debug Duplicados",list.get(j).toString());
                    list.remove(j);
                    j--;
                }
            }
        }
        return list;
    }

    /**
     * Atualiza categorias no banco de dados.
     * @param livros
     */
    private void createCategory(ArrayList<String> livros){
        for (String livro: livros){
            Insert insert =  new Insert(this);
            Categorias categorias =  new Categorias();
            categorias.setCategoryName(livro);
            insert.saveCategoryFireStore(categorias);
        }
        getDatabase();
    }
}
