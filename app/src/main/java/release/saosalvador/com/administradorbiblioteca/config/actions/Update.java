/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.actions;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import release.saosalvador.com.administradorbiblioteca.ToHashMap;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class Update {

    Category mCategory;
    Livro mLivro;
    Context mContext;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;
    OnUpdateSuccessListener listener;
    ProgressDialog pd;
    double progress;

    public Update(Context mContext) {
        this.mContext = mContext;
    }
    /**
     * salva a categoria após a edição.
     * @param category objeto do tipo {@link Category}.
     * @param oldName nome antigo da categoria.
     */
    public void editCategory(Category category, String oldName){
        try {
            mCategory = category;
            Map < String, Object > editCategory = new HashMap < > ();
            editCategory.putAll(ToHashMap.categoryToHashMap(mCategory));
            firebaseFirestore =  FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("categorias")
                    .document(oldName)
                    .delete();
            firebaseFirestore =  FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("categorias")
                    .document(mCategory.getCategoryName())
                    .set(editCategory).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (listener != null) {
                        listener.onCompleteUpdate(null);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * salva informações no banco de dados.
     * tabela: livros/nome do livro.
     * tabela: categorias/categoria/livros/nome do livro.
     */
    public void editInfo(Livro livro, Category category){
        mLivro = livro;
        mCategory = category;
        try {
            databaseReference = DAO.getFireBase()
                    .child("livros")
                    .child(mLivro.getIdLivro());
            databaseReference.setValue(mLivro).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (listener != null) {
                        listener.onCompleteUpdate(null);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createPd(){
        pd.setCancelable(false);
        pd.setMessage("Carregando ("+ (int)progress+"%)");
        pd.setProgress((int)progress);
        pd.show();
    }

    public interface OnUpdateSuccessListener {void onCompleteUpdate(UploadTask.TaskSnapshot taskSnapshot);}

    public void addOnSuccessListener(OnUpdateSuccessListener listener) {
        this.listener = listener;
    }
}
