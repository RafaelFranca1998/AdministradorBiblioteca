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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.AdapterRecyclerViewCategory;
import release.saosalvador.com.administradorbiblioteca.config.recyclerview.RecyclerItemClickListener;
import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class CategoryActivity extends AppCompatActivity {
    private List<Category> categoryList;
    private RecyclerView listView;
    private AdapterRecyclerViewCategory adapterListView;
    private int itemPosition;
    private DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onPause() {
        super.onPause();
        adapterListView.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Categorias disponiveis");
        setSupportActionBar(toolbar);
        categoryList=  new ArrayList<>();
        listView = findViewById(R.id.recycler_view_category);
        adapterListView =  new AdapterRecyclerViewCategory(CategoryActivity.this,categoryList);

        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseFirestore.collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categoryList.add(document.toObject(Category.class)) ;
                    }
                    adapterListView.notifyDataSetChanged();
                } else {
                    Log.w("D", "Error getting documents.", task.getException());
                }
            }
        });
        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        listView.setLayoutManager(gridLayoutManager);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(CategoryActivity.this,CategoryEditActivity.class);
                        String categoryName = categoryList.get(position).getCategoryName();
                        intent.putExtra("category", categoryName);
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        itemPosition = position;
                    }
                })
        );
    }
}
