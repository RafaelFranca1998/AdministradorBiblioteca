/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.activity;


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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.actions.Delete;
import release.saosalvador.com.administradorbiblioteca.config.actions.Get;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.AdapterRecyclerView;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.RecyclerItemClickListener;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings;
    private LinearLayout layoutFabSave;
    private LinearLayout layoutFabAdd;
    static String mCaminho;
    private String KEY;
    NavigationView navigationView;
    DrawerLayout drawer;


    private static List<Livro> listLivros;
    private static RecyclerView listView;
    private AdapterRecyclerView adapterListView;
    private String caminhoDoArquivo;
    static int itemPosition;
    Toolbar toolbar;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //------------------------------------------------------------------------------------------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //------------------------------------------------------------------------------------------
        KEY = getString(R.string.tag_id);

        listLivros =  new ArrayList<>();
        listView = findViewById(R.id.recycler_view);

        fabSettings =  findViewById(R.id.fabSetting);
        layoutFabSave = findViewById(R.id.layoutFabSave);
        layoutFabAdd = findViewById(R.id.layoutFabAdd);
        //  layoutFabPhoto = (LinearLayout) this.findViewById(R.id.layoutFabPhoto);

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        closeSubMenusFab();

        layoutFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,AddBookActivity.class);
                startActivity(intent);
            }
        });

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_livros);

//        databaseReference = DAO.getFireBase().child(getString(R.string.child_book));
        adapterListView =  new AdapterRecyclerView(MainActivity.this,listLivros);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                listLivros.clear();
//                for (DataSnapshot data:dataSnapshot.getChildren()){
//                    Livro livro = data.getValue(Livro.class);
//                    listLivros.add(livro);
//                }
//                adapterListView.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.i(getString(R.string.error),databaseError.getMessage());
//            }
//        });
        Get get =  new Get();
        listLivros = get.getLivro();
        adapterListView.notifyDataSetChanged();

        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        listView.setLayoutManager(gridLayoutManager);

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                        intent.putExtra(KEY,listLivros.get(position).getIdLivro());
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        itemPosition = position;
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
                    intent.putExtra(KEY,listLivros.get(itemPosition).getIdLivro());
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    Intent intent = new Intent(MainActivity.this, OpenBookActivity.class);
                    intent.putExtra(KEY, listLivros.get(itemPosition).getIdLivro());
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 2:
                try{
                    Intent intent = new Intent(MainActivity.this,EditActivity.class);
                    intent.putExtra(KEY,listLivros.get(itemPosition).getIdLivro());
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 3:
                Delete delete =  new Delete(MainActivity.this,listLivros.get(itemPosition),databaseReference);
                delete.deleteBook();
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void closeSubMenusFab(){
        layoutFabSave.setVisibility(View.INVISIBLE);
        layoutFabAdd.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.ic_add_green_24dp);
        fabExpanded = false;
    }

    private void openSubMenusFab(){
        layoutFabSave.setVisibility(View.VISIBLE);
        layoutFabAdd.setVisibility(View.VISIBLE);
        fabSettings.setImageResource(R.drawable.ic_close_red_24dp);
        fabExpanded = true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_livros) {
            Intent intent =  new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_livro) {
            Intent intent =  new Intent(MainActivity.this,AddBookActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categorias) {
            Intent intent =  new Intent(MainActivity.this,CategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_edit) {
            //Intent intent =  new Intent(MainActivity.this,)
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public static class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            itemPosition = listView.indexOfChild(v);
        }
    }

}
