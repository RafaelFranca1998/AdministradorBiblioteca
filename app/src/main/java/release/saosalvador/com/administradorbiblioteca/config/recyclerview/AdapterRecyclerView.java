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

import release.saosalvador.com.administradorbiblioteca.MainActivity;
import release.saosalvador.com.administradorbiblioteca.R;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ImageView imgIcon;
        TextView txtCategoria;
        TextView txtNomeLivro;
        ProgressBar progressBar;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
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

    // Store a member variable for the contacts
    private List<Livro> mLivros;
    private Context mContext;
    private String url;
    private ViewHolder mViewHolder;

    // Pass in the contact array into the constructor
    public AdapterRecyclerView(Context context,List<Livro> livro) {
        mLivros = livro;
        mContext = context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public AdapterRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View listView = inflater.inflate(R.layout.recycler_cell, parent, false);
        listView.setOnClickListener(new MainActivity.MyOnClickListener());
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(listView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Livro livro = mLivros.get(position);
        mViewHolder = viewHolder;

        // Set item views based on your views and data model
        TextView textViewCategoria = mViewHolder.txtCategoria;
        textViewCategoria.setText(livro.getCategoria());
        TextView textViewNome = mViewHolder.txtNomeLivro;
        textViewNome.setText(livro.getNome());
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

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mLivros.size();
    }
}
