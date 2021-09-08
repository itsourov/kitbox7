package net.sourov.kitbox;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import net.sourov.kitbox.adapter.PostsAdapter;
import net.sourov.kitbox.model.PostsModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFg extends Fragment {
    RecyclerView recyclerView;
    List<PostsModel> postsModelList;
    PostsAdapter mAdapter;

    public RecyclerViewFg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view_fg, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Post lists");

        recyclerView = view.findViewById(R.id.recyclerViewOnRVFG);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        postsModelList =  new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("downloads");
        reference.keepSynced(true);
        reference.orderByChild("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                postsModelList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    PostsModel contacts = ds.getValue(PostsModel.class);
                    postsModelList.add(contacts);
                    mAdapter = new PostsAdapter(getContext(),postsModelList);
                    recyclerView.setAdapter(mAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return view;
    }
}