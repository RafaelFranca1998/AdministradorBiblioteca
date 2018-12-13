/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.actions.Delete;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.AdapterRecyclerView;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.RecyclerItemClickListener;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private String KEY = "id";
    private Livro livro;
    private NavigationView navigationView;
    private static List<Livro> listLivros;
    @SuppressLint("StaticFieldLeak")
    private static RecyclerView listView;
    private AdapterRecyclerView adapterListView;
    static int posicaoItem;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //---------------------------------------TOOLBAR--------------------------------------------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //---------------------------------------FAB------------------------------------------------
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,AddLivroActivity.class);
                startActivity(intent);
            }
        });
        //---------------------------------------NAV_VIEW-------------------------------------------
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_livros);
        //---------------------------------------RECYCLERVIEW---------------------------------------
        listLivros =  new ArrayList<>();
        listView = findViewById(R.id.recycler_view);
        adapterListView =  new AdapterRecyclerView(MainActivity.this,listLivros);
        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        listView.setLayoutManager(gridLayoutManager);
        //------------------------------------------------------------------------------------------
        atualizarLista();

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                        intent.putExtra(KEY,listLivros.get(position).getIdLivro());
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        posicaoItem = position;
                    }
                })
        );
    }

    //TODO context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                try{
                    Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                    intent.putExtra(KEY,listLivros.get(posicaoItem).getIdLivro());
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    Intent intent = new Intent(MainActivity.this, AbrirLivroActivity.class);
                    intent.putExtra(KEY, listLivros.get(posicaoItem).getIdLivro());
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 2:
                try{
                    Intent intent = new Intent(MainActivity.this,EditActivity.class);
                    intent.putExtra(KEY,listLivros.get(posicaoItem).getIdLivro());
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 3:
                Delete delete =  new Delete(MainActivity.this,listLivros.get(posicaoItem));
                delete.deleteBook();
                delete.addOnSuccessListener(new Delete.OnSuccessDeleteListener() {
                    @Override
                    public void onCompleteInsert(@NonNull Void aVoid) {
                        adapterListView.notifyDataSetChanged();
                    }
                });
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void atualizarLista(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection(getString(R.string.child_book))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listLivros.clear();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                livro = document.toObject(Livro.class);
                                listLivros.add(livro) ;
                            }
                            adapterListView.notifyDataSetChanged();
                        } else {
                            Log.w("D", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarLista();
        navigationView.setCheckedItem(R.id.nav_livros);
        toolbar.setTitle(R.string.main_activity_title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_livros) {
            if (!menuItem.isChecked()) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_add_livro) {
            Intent intent =  new Intent(MainActivity.this,AddLivroActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categorias) {
            Intent intent =  new Intent(MainActivity.this,CategoriasActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            posicaoItem = listView.indexOfChild(v);
        }
    }
}
