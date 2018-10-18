package release.saosalvador.com.administradorbiblioteca.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DAO {
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;
    private static StorageReference storage;


    public DAO() {
    }

    public static DatabaseReference getFireBase(){
        if (referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    public static StorageReference getFirebaseStorage() {
        if (storage == null){
         storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

    //    public void criaEvento(Eventos eventos){
//        referenciaFirebase.child("eventos").child(String.valueOf(Eventos.getDataCriacao())).child(String.valueOf(Eventos.getSerialNumber())).setValue(eventos);
//    }
//
//    public void selecionarEvento(String codigoEvento){
//        firebaseReferencia.child("eventos").child(codigoEvento).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Eventos  eventos = dataSnapshot.getValue(Eventos.class);
//                Log.i("Eventos",eventos.toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.i("Error","The read failed: " + databaseError.getCode());
//            }
//        });
//    }

}
