/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class Delete {
    Context mContext;
    Livro mLivro;
    DatabaseReference mDatabaseReference;

    public Delete(Context context,Livro livro,DatabaseReference reference) {
        mContext = context;
        mLivro = livro;
        mDatabaseReference = reference;
    }

    public void deleteBook(){
        StorageReference deleteRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(mLivro.getLinkDownload()+"/"+Base64Custom.renoveSpaces(mLivro.getNome()));
        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteThumbnail();
                Toast.makeText(mContext,R.string.delete_successful,Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,R.string.error_delete+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void deleteThumbnail(){
        StorageReference deleteTumbnailRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(mLivro.getImgDownload());
        deleteTumbnailRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteData();
                Toast.makeText(mContext,R.string.delete_successful+"Imagem deletada",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,R.string.error_delete+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteData(){
        mDatabaseReference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference categoryReference = DAO.getFireBase()
                        .child("categorias")
                        .child(mLivro.getCategoria())
                        .child(mLivro.getIdLivro());
                categoryReference.setValue(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,R.string.error_delete+" "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
