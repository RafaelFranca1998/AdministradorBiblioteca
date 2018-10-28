/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.actions;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.ToHashMap;
import release.saosalvador.com.administradorbiblioteca.config.Base64Custom;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class Insert {
    private OnSuccessInsertListener listener;
    private OnSuccessSendListener sendListener;
    private Context mContext;
    private Bitmap imagemCapa;
    private ProgressDialog pd;
    private Livro mLivro;
    private Category mCategory;
    private StorageReference storageReference;
    private String nomelivro;
    private String mPath;
    private double progress;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;

    /**
     * Construtor da classe;
     * @param context contexto da aplicação.
     */
    public Insert(@NonNull Context context) {
        this.listener = null;
        this.sendListener = null;
        mContext = context;
    }

    /**
     * salva somente o livro e a imagem do livro no sevidor de arquivos.
     * deverá ser seguido pelo saveInfo.
     */
    public void saveBook(Livro livro,String path){
        mPath =  path;
        mLivro = livro;
        try {
            Uri uri = Uri.fromFile(new File(mPath));
            generateImageFromPdf(uri);

            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("application/pdf").build();
            nomelivro = Base64Custom.renoveSpaces(mLivro.getNome());
            storageReference = DAO.getFirebaseStorage().child(mContext.getString(R.string.child_book)).child(mLivro.getIdLivro()).child(nomelivro);
            mLivro.setLinkDownload( storageReference.toString() );
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            imagemCapa.compress(Bitmap.CompressFormat.JPEG,40,stream);
            byte[] byteImagem = stream.toByteArray();
            String linkDownload = mLivro.getLinkDownload();
            linkDownload = linkDownload.replace("gs:/","");
            linkDownload = linkDownload.replace("bibliotecasaosalvador.appspot.com/","");
            storageReference = null;
            storageReference = DAO.getFirebaseStorage().child(linkDownload).child("thumbnail-"+nomelivro);
            mLivro.setImgDownload(storageReference.toString());
            UploadTask uploadTask2 = storageReference.putBytes(byteImagem);
            uploadTask2.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(mContext,R.string.successful_image_upload,Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    e.getMessage();
                }
            });
            storageReference = null;
            storageReference = DAO.getFirebaseStorage().child(linkDownload).child(nomelivro);
            UploadTask uploadTask = storageReference.putFile(uri, metadata);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    try {
                        progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        pd = new ProgressDialog(mContext);
                        pd.setMessage("Carregando (" + (int) progress + "%)");
                        pd.setProgress((int) progress);
                        if (!pd.isShowing()) {
                            pd.setCancelable(false);
                            pd.show();
                        }
                        System.out.println("Upload is " + progress + "% done");
                    }catch (Exception e){

                    }
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    pd.dismiss();
                    Toast.makeText(mContext,R.string.error_image_upload,Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    if (sendListener != null) {
                        sendListener.onCompleteInsert(taskSnapshot);
                    }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    pd.dismiss();
                }
            });

        } catch (NullPointerException e){
            Toast.makeText(mContext,"É necessário selecionar um arquivo! "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,R.string.unknow_error+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    /**
     * salva informações no banco de dados.
     * tabela: livros/nome do livro.
     * tabela: categorias/categoria/livros/nome do livro.
     */
    public void saveInfo(Livro livro,Category category){
        mLivro = livro;
        mCategory = category;
        try {
            databaseReference = DAO.getFireBase()
                    .child("livros")
                    .child(mLivro.getIdLivro());
            databaseReference.setValue(mLivro);
//            databaseReference = null;
//            databaseReference = DAO.getFireBase()
//                    .child("categorias")
//                    .child(mLivro.getCategoria())
//                    .child("livros")
//                    .child(mLivro.getIdLivro());
//            databaseReference.setValue(mLivro).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    if (listener != null) {
//                        listener.onCompleteInsert(null);
//                    }
//                }
//            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                pd.dismiss();
            }catch (NullPointerException e){
                Log.i("Debug: ","progressDialog null");
            }

        }
    }

    public void saveInfoFireStore(Livro livro,Category category){
        mLivro = livro;
        mCategory = category;
        firebaseFirestore =  null;
        Map < String, Object > newLivro = new HashMap < > ();
        Map < String, Object > newCategory = new HashMap < > ();
        newCategory.putAll(ToHashMap.categoryToHashMap(mCategory));
        newLivro.putAll(ToHashMap.livroToHashMap(mLivro));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("livros").document(mLivro.getIdLivro()).set(newLivro).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (listener != null) {
                        listener.onCompleteInsert(null);
                    }
                }
            });
            //mFirebaseFirestore.collection("categorias/"+mLivro.getCategoria()+"/").document(mLivro.getIdLivro()).set(newLivro);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map < String, Object > newContact;
    public void saveCategoryFireStore(Category category){
        mCategory =  category;
        newContact = new HashMap < > ();
        newContact.putAll(ToHashMap.categoryToHashMap(mCategory));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection(mContext.getString(R.string.child_category))
                    .document(mCategory.getCategoryName()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (!documentSnapshot.exists()){
                        firebaseFirestore
                                .collection(mContext.getString(R.string.child_category))
                                .document(mCategory.getCategoryName())
                                .set(newContact)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (listener != null) {
                                            listener.onCompleteInsert(null);
                                        }
                                    }
                                });
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    Category saveCategory;
    /**
     * salva a imagem da categoria.
     * @param category objeto do tipo {@link Category}.
     * @param mPath caminho da imagem.
     */
    public void saveCategoryImg(Category category, Uri mPath){
        try {
            saveCategory = category;
            final StorageReference categoryReference = DAO.getFirebaseStorage()
                    .child("categorias")
                    .child(saveCategory.getCategoryName())
                    .child(saveCategory.getCategoryName());
            final Bitmap imagem = MediaStore.Images.Media.getBitmap((mContext).getContentResolver(), mPath);
            // comprimir no formato jpeg
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG,60,stream);
            byte[] byteData = stream.toByteArray();
            UploadTask uploadTask = categoryReference.putBytes(byteData);
            final ProgressDialog pd2 = new ProgressDialog(mContext);
            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    if (pd2.isShowing()){
                        pd2.setCancelable(false);
                        pd2.setMessage("Carregando ("+ (int)progress+"%)");
                        pd2.show();
                    }
                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    pd2.setProgress((int)progress);
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                    pd2.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(mContext,"Falha ao carregar a imagem",Toast.LENGTH_SHORT).show();
                    pd2.dismiss();
                    exception.printStackTrace();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    saveCategory.setImgDownload(categoryReference.toString());
                    saveCategoryFireStore(saveCategory);
                    pd2.dismiss();
                    Toast.makeText(mContext,"Imagem carregada!",Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void generateImageFromPdf(Uri pdfUri) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(mContext);
        try {
            ParcelFileDescriptor fd = mContext.getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            imagemCapa = bmp;
            pdfiumCore.closeDocument(pdfDocument);
        } catch(Exception e) {
            //todo with exception
        }
    }

    public interface OnSuccessInsertListener {void onCompleteInsert(UploadTask.TaskSnapshot taskSnapshot);}

    public interface OnSuccessSendListener {void onCompleteInsert(UploadTask.TaskSnapshot taskSnapshot);}

    public void addOnSuccessListener(OnSuccessInsertListener listener) {
        this.listener = listener;
    }

    public void addOnSuccessSendListener(OnSuccessSendListener listener) {
        this.sendListener = listener;
    }
}
