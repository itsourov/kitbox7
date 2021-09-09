package net.sourov.kitbox.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.NotNull;

import net.sourov.kitbox.PostDetailsFg;
import net.sourov.kitbox.R;
import net.sourov.kitbox.model.PostsModel;

import java.util.List;

public class PostsAdapter extends   RecyclerView.Adapter<PostsAdapter.PostHolder>{

    Context context;
    List<PostsModel> postsModelList;


    public PostsAdapter(Context context, List<PostsModel> postsModelList) {
        this.context = context;
        this.postsModelList = postsModelList;
    }

    @NonNull
    @NotNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_post_design, parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostHolder holder, int position) {


        PostsModel postsModel = postsModelList.get(position);
        holder.tittleOnSPD.setText(postsModel.getTitle());
        Glide.with(context)
                .load(postsModel.getImageLink())
                .centerCrop()
                .placeholder(R.drawable.loading).error(R.drawable.image_not_found)
                .into(holder.imageOnSPD);
        holder.SDPContainer.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("tittle", postsModel.getTitle());
            bundle.putString("downloadLink", postsModel.getDownloadLink());
            bundle.putString("imageLink", postsModel.getImageLink());
            bundle.putString("description", postsModel.getDescription());

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            PostDetailsFg myFragment = new PostDetailsFg();
            myFragment.setArguments(bundle);

            activity.getSupportFragmentManager().beginTransaction().
                    replace(R.id.frameLayoutOnPostContainer, myFragment)
                    .addToBackStack(null).commit();

        });

    }


    @Override
    public int getItemCount() {
        return postsModelList.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder{


        TextView tittleOnSPD;
        ImageView imageOnSPD;
        CardView SDPContainer;


        public PostHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tittleOnSPD = itemView.findViewById(R.id.tittleOnSPD);
            imageOnSPD = itemView.findViewById(R.id.imageOnSPD);
            SDPContainer = itemView.findViewById(R.id.SDPContainer);


        }
    }
}
