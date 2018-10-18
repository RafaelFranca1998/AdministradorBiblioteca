package release.saosalvador.com.administradorbiblioteca;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFViewAct;

import java.util.ArrayList;
import java.util.List;

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

    private static List<Livro> listLivros;
    private static RecyclerView listView;
    private AdapterRecyclerView adapterListView;
    static int itemPosition;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listLivros =  new ArrayList<>();
        listView = findViewById(R.id.recycler_view);

        fabSettings =  findViewById(R.id.fabSetting);
        layoutFabSave = findViewById(R.id.layoutFabSave);
        layoutFabAdd = findViewById(R.id.layoutFabAdd);
      //  layoutFabPhoto = (LinearLayout) this.findViewById(R.id.layoutFabPhoto);

        //When main Fab (Settings) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Settings) open/close behavior
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

        //Only main FAB is visible in the beginning
        closeSubMenusFab();

        layoutFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,AddBookActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        databaseReference = DAO.getFireBase().child("livros");
        adapterListView =  new AdapterRecyclerView(MainActivity.this,listLivros);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listLivros.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Livro livro = data.getValue(Livro.class);
                    listLivros.add(livro);
                }
                adapterListView.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Erro: ",databaseError.getMessage());
            }
        });
        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        listView.setLayoutManager(gridLayoutManager);

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(MainActivity.this,"Clickou",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                        intent.putExtra("id",listLivros.get(position).getIdLivro());
                        startActivity(intent);                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    //TODO context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                intent.putExtra("id",listLivros.get(itemPosition).getIdLivro());
                startActivity(intent);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_livros) {
            Intent intent =  new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_livro) {
            Intent intent =  new Intent(MainActivity.this,AddBookActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categorias) {

        } else if (id == R.id.nav_edit) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabSave.setVisibility(View.INVISIBLE);
        layoutFabAdd.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.ic_add_green_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabSave.setVisibility(View.VISIBLE);
        layoutFabAdd.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fabSettings.setImageResource(R.drawable.ic_close_red_24dp);
        fabExpanded = true;
    }

    public static class FireMissilesDialogFragment extends DialogFragment {
        String[] myStrings = new String[] {"Abrir Livro","Ver Info","Excluir(Breve)"};
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Livro")
                    .setItems(myStrings, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which){
                                case 0:
                                    MainActivity activity = new MainActivity();
                                    activity.abrirLivro(mCaminho);
                                    break;
                                case 1:
                                    //Todo pagina info


                            }
                        }
                    });
            return builder.create();
        }
    }
    private void abrirLivro(String caminhoDoArquivo){
        if (caminhoDoArquivo != null && !caminhoDoArquivo.equals("")) {
            try {
                Global.Init(MainActivity.this);
                Intent intent = new Intent();
                intent.setClass(this, PDFViewAct.class);
//                String caminho = data.getData().getPath(); // usar @caminhoDoArquivo
                intent.putExtra("PDFPath", caminhoDoArquivo);
                startActivity(intent);
            } catch (NullPointerException e) {
                Toast.makeText(this, "Selecione um livro.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "Selecione um livro.", Toast.LENGTH_LONG).show();
        }
    }
    //todo tentar aqui, abrir menu
    public static class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            itemPosition = listView.indexOfChild(v);
//            Log.e("Clicked and Position",String.valueOf(itemPosition));
        }
    }
}
