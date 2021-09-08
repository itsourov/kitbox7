package net.sourov.kitbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class PostDetailsFg extends Fragment {

    public PostDetailsFg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_post_details_fg, container, false);


        String tittle = getArguments().getString("tittle");
        String downloadLink = getArguments().getString("downloadLink");
        String imageLink = getArguments().getString("imageLink");
        String description = getArguments().getString("description");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(tittle);

        TextView tittleTextOnPostDetails =view.findViewById(R.id.tittleTextOnPostDetails);
        tittleTextOnPostDetails.setText(tittle);

        TextView descriptionTextOnPostDetails =view.findViewById(R.id.descriptionTextOnPostDetails);
        descriptionTextOnPostDetails.setText(description);

        ImageView imageViewOnPostDetails = view.findViewById(R.id.imageViewOnPostDetails);

        Glide.with(getContext())
                .load(imageLink)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageViewOnPostDetails);

        view.findViewById(R.id.downloadBtnOnPD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));
                startActivity(browserIntent);
            }
        });



        return view;
    }
}