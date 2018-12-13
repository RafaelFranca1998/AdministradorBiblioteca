/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.activity.MainActivity;
import release.saosalvador.com.administradorbiblioteca.model.Categorias;

public class AdapterRecyclerViewCategory extends RecyclerView.Adapter<AdapterRecyclerViewCategory.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCategory;
        TextView txtCategory;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.img_category);
            txtCategory = itemView.findViewById(R.id.text_category);
            progressBar = itemView.findViewById(R.id.category_progress_bar);
        }
    }

    private List<Categorias> mCategoriasList;
    private Context mContext;
    private ViewHolder mViewHolder;

    public AdapterRecyclerViewCategory(Context context, List<Categorias> categoriasList) {
        mCategoriasList = categoriasList;
        mContext = context;
    }

    @NonNull
    @Override
    public AdapterRecyclerViewCategory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listView = inflater.inflate(R.layout.category_list, parent, false);
        listView.setOnClickListener(new MainActivity.MyOnClickListener());
        ViewHolder viewHolder = new ViewHolder(listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Categorias categorias = mCategoriasList.get(position);
        mViewHolder = viewHolder;

        TextView textViewCategoria = mViewHolder.txtCategory;
        textViewCategoria.setText(categorias.getCategoryName());
        ImageView imgIcon = mViewHolder.imgCategory;
        String url = categorias.getImgDownload();
        if (mViewHolder.imgCategory == null){
            try {
                mViewHolder.imgCategory.setVisibility(View.GONE);
                mViewHolder.progressBar.setVisibility(View.VISIBLE);
                StorageReference storageReference =
                        FirebaseStorage.getInstance().getReferenceFromUrl(url);
                mViewHolder.progressBar.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                mViewHolder.progressBar.setVisibility(View.GONE);
                                mViewHolder.imgCategory.setVisibility(View.VISIBLE);
                                return false; // important to return false so the error placeholder can be placed
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mViewHolder.progressBar.setVisibility(View.GONE);
                                mViewHolder.imgCategory.setVisibility(View.VISIBLE);
                                return false;
                            }
                        }).into(mViewHolder.imgCategory);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            try {
                if (url == null){
                    viewHolder.imgCategory.setImageResource(R.drawable.ic_add);
                    mViewHolder.progressBar.setVisibility(View.GONE);
                    mViewHolder.imgCategory.setVisibility(View.VISIBLE);
                }else {
                    StorageReference storageReference =
                            FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    mViewHolder.progressBar.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(viewHolder.imgCategory);
                    mViewHolder.progressBar.setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return mCategoriasList.size();
    }
}
