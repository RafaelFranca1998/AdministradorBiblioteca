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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import release.saosalvador.com.administradorbiblioteca.config.ToHashMap;
import release.saosalvador.com.administradorbiblioteca.config.MyCustomUtil;
import release.saosalvador.com.administradorbiblioteca.config.DAO;
import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class Insert {
    private OnSuccessInsertListener insertListener;
    private OnSuccessUploadListener uploadListener;
    private Context mContext;
    private Bitmap imagemCapa;
    private ProgressDialog pd;
    private Livro mLivro;
    private Category mCategory;
    private double progress;
    private FirebaseFirestore firebaseFirestore;

    /**
     * Construtor da classe;
     * @param context contexto da aplicação.
     */
    public Insert(@NonNull Context context) {
        this.insertListener = null;
        this.uploadListener = null;
        mContext = context;
    }

    /**
     * salva somente o livro e a imagem do livro no sevidor de arquivos.
     * deverá ser seguido pelo saveInfo.
     */
    public void salvarLivro(Livro livro, String path){
        mLivro = livro;
        try {
            Uri uri = Uri.fromFile(new File(path));
            generateImageFromPdf(uri);

            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("application/pdf").build();
            StorageReference storageReference
                    = DAO.getFirebaseStorage().child(mContext.getString(R.string.child_book)).child(mLivro.getIdLivro());
            mLivro.setLinkDownload( storageReference.toString() );

            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            imagemCapa.compress(Bitmap.CompressFormat.JPEG,40,stream);
            byte[] byteImagem = stream.toByteArray();
            String linkDownload = mLivro.getLinkDownload();
            linkDownload = linkDownload.replace("gs:/","");
            linkDownload = linkDownload.replace("bibliotecasaosalvador.appspot.com/","");
            storageReference = DAO.getFirebaseStorage().child(linkDownload).child("thumbnail-livro");
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
            storageReference = DAO.getFirebaseStorage().child(linkDownload).child("livro.pdf");
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
                        e.printStackTrace();
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
                    if (uploadListener != null) {
                        uploadListener.onCompleteInsert(taskSnapshot);
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


    public void saveInfoFireStore(Livro livro,Category category){
        mLivro = livro;
        mCategory = category;
        firebaseFirestore =  null;
        Map < String, Object > newLivro = new HashMap < > ();
        Map < String, Object > newCategory = new HashMap < > ();
        newCategory.putAll(ToHashMap.hashmapToCategory(mCategory));
        newLivro.putAll(ToHashMap.livroToHashMap(mLivro));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("livros").document(mLivro.getIdLivro()).set(newLivro).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (insertListener != null) {
                        insertListener.onCompleteInsert(null);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map < String, Object > newCategory;
    public void saveCategoryFireStore(Category category){
        mCategory = category;
        newCategory = new HashMap < > ();
        newCategory.putAll(ToHashMap.hashmapToCategory(mCategory));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection(mContext.getString(R.string.child_category))
                    .document(mCategory.getCategoryName()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    assert documentSnapshot != null;
                    if (!documentSnapshot.exists()){
                        firebaseFirestore
                                .collection(mContext.getString(R.string.child_category))
                                .document(mCategory.getCategoryName())
                                .set(newCategory)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (insertListener != null) {
                                            insertListener.onCompleteInsert(null);
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

    private Category saveCategory;
    /**
     * salva a imagem da categoria.
     * @param category objeto do tipo {@link Category}.
     * @param mPath caminho da imagem.
     */
    public void saveCategoryImg(Category category, Uri mPath){
        try {
            saveCategory = category;
            String name = saveCategory.getCategoryName();
            name =  MyCustomUtil.unaccent(name);
            name = MyCustomUtil.removeSpaces(name);
            final StorageReference categoryReference = DAO.getFirebaseStorage()
                    .child("categorias")
                    .child(name)
                    .child(name);
            final Bitmap imagem = MediaStore.Images.Media.getBitmap((mContext).getContentResolver(), mPath);
            // comprimir no formato jpeg
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG,60,stream);
            byte[] byteData = stream.toByteArray();
            UploadTask uploadTask = categoryReference.putBytes(byteData);
            final ProgressDialog pd2 = new ProgressDialog(mContext);
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
                    String parse = categoryReference.toString();
                    saveCategory.setImgDownload(parse);
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
            e.printStackTrace();
        }
    }

    public interface OnSuccessInsertListener {void onCompleteInsert(UploadTask.TaskSnapshot taskSnapshot);}

    public interface OnSuccessUploadListener {void onCompleteInsert(UploadTask.TaskSnapshot taskSnapshot);}

    public void addOnSuccessListener(OnSuccessInsertListener listener) {
        this.insertListener = listener;
    }

    public void addOnSuccessUploadListener(OnSuccessUploadListener listener) {
        this.uploadListener = listener;
    }
}
