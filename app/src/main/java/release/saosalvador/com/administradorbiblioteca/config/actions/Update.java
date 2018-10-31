/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.actions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import release.saosalvador.com.administradorbiblioteca.config.ToHashMap;
import release.saosalvador.com.administradorbiblioteca.model.Category;

public class Update {

    private OnUpdateSuccessListener listener;

    public Update() {
    }
    /**
     * salva a categoria após a edição.
     * @param category objeto do tipo {@link Category}.
     * @param oldName nome antigo da categoria.
     */
    public void editCategory(Category category, String oldName){
        try {
            Map < String, Object > editCategory = new HashMap < > ();
            editCategory.putAll(ToHashMap.categoryToHashMap(category));
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("categorias")
                    .document(oldName)
                    .delete();
            firebaseFirestore =  FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("categorias")
                    .document(category.getCategoryName())
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

    public interface OnUpdateSuccessListener {void onCompleteUpdate(UploadTask.TaskSnapshot taskSnapshot);}

    public void addOnSuccessListener(OnUpdateSuccessListener listener) {
        this.listener = listener;
    }
}
