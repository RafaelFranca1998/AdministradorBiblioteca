/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import release.saosalvador.com.administradorbiblioteca.ToHashMap;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class Get {
    private FirebaseFirestore firebaseFirestore;
    private OnGetSuccessListener listener;
    private Context mContext;

    public Get(Context context) {
        mContext = context;
    }

    public ArrayList<Livro> getLivroByCategoria(String s){
        final ArrayList<Livro> livrosList =  new ArrayList<>();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseFirestore.collection("livros").whereArrayContains("categoria",s).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Map<String,Object> stringMap = document.getData();
                        livrosList.add(ToHashMap.hashMapToLivro(stringMap)) ;
                    }
                } else {
                    Log.w("D", "Error getting documents.", task.getException());
                }
            }
        });
        return livrosList;
    }
    public ArrayList<Livro> getLivro(){
        final ArrayList<Livro> livrosList =  new ArrayList<>();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseFirestore.collection("livros").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String,Object> stringMap = document.getData();
                        livrosList.add(ToHashMap.hashMapToLivro(stringMap)) ;
                    }
                    if (listener != null) {
                        listener.onCompleteUpdate(null);
                    }
                } else {
                    Log.w("D", "Error getting documents.", task.getException());
                }
            }
        });
        return livrosList;
    }

    public interface OnGetSuccessListener {void onCompleteUpdate(UploadTask.TaskSnapshot taskSnapshot);}

    public void addOnSuccessListener(Get.OnGetSuccessListener listener) {
        this.listener = listener;
    }
}
