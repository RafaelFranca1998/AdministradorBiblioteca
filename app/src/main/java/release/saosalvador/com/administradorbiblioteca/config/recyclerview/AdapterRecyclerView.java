/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
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

import release.saosalvador.com.administradorbiblioteca.activity.MainActivity;
import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.config.MyCustomUtil;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView imgIcon;
        TextView txtCategoria;
        TextView txtNomeLivro;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            imgIcon = itemView.findViewById(R.id.imagemview_list);
            txtCategoria = itemView.findViewById(R.id.text_categoria);
            txtNomeLivro = itemView.findViewById(R.id.text_nome_livro_list);
            progressBar = itemView.findViewById(R.id.progressBarlistview);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");
            menu.add(0, 0, 0, "Ver Info.");
            menu.add(0, 1, 1, "Abrir");
            menu.add(0, 2, 2, "Editar");
            menu.add(0, 3, 3, "deletar");
        }
    }

    private List<Livro> mLivros;
    private Context mContext;
    private String url;
    private ViewHolder mViewHolder;

    public AdapterRecyclerView(Context context,List<Livro> livro) {
        mLivros = livro;
        mContext = context;
    }

    @Override
    public AdapterRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listView = inflater.inflate(R.layout.recycler_cell, parent, false);
        listView.setOnClickListener(new MainActivity.MyOnClickListener());
        ViewHolder viewHolder = new ViewHolder(listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Livro livro = mLivros.get(position);
        mViewHolder = viewHolder;

        TextView textViewCategoria = mViewHolder.txtCategoria;
        textViewCategoria.setText(livro.getCategoria());
        TextView textViewNome = mViewHolder.txtNomeLivro;
        textViewNome.setText(MyCustomUtil.removeLines(livro.getNome()));
        ImageView imgIcon = mViewHolder.imgIcon;
        url = livro.getImgDownload();
        if (mViewHolder.imgIcon == null){
            mViewHolder.imgIcon.setVisibility(View.GONE);
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
                            mViewHolder.imgIcon.setVisibility(View.VISIBLE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mViewHolder.progressBar.setVisibility(View.GONE);
                            mViewHolder.imgIcon.setVisibility(View.VISIBLE);
                            return false;
                        }
                    }).into(mViewHolder.imgIcon);
        } else {
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(url);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(viewHolder.imgIcon);
            mViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mLivros.size();
    }
}
