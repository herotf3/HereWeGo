package com.example.asus.travisor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.travisor.Activity.PlaceDetailActivity;
import com.example.asus.travisor.Interface.BookmarkCallback;
import com.example.asus.travisor.Interface.ItemClickListener;
import com.example.asus.travisor.Model.Common;
import com.example.asus.travisor.Model.Firebase.Place;
import com.example.asus.travisor.R;
import com.example.asus.travisor.ViewHolder.PlaceVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.Serializable;

/**
 * Created by ASUS on 5/16/2018.
 */

public class FavouriteFragment extends Fragment implements BookmarkCallback{
    View rootView;

    FirebaseDatabase db;
    DatabaseReference bookmarkRef;

    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter adapter;
    Query query;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_menu,null);
        initView();
        bindData();
        return rootView;
    }

    private void bindData() {
        db=FirebaseDatabase.getInstance();
        bookmarkRef =db.getReference().child(getString(R.string.DB_Bookmarks))
                        .child(Common.userKey);     //bookmark of this user
        loadList();
    }

    private void initView() {
        recyclerMenu=rootView.findViewById(R.id.rcl_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(getContext(),1);
        recyclerMenu.setLayoutManager(layoutManager);
    }

    private void loadList() {
        final BookmarkCallback callback=this;
        query = bookmarkRef.limitToLast(60);
        FirebaseRecyclerOptions<Place> options =
                new FirebaseRecyclerOptions.Builder<Place>()
                        .setQuery(query, Place.class)
                        .build();
        adapter= new FirebaseRecyclerAdapter<Place, PlaceVH>(options) {
            @Override
            public PlaceVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_place,parent,false);
                return new PlaceVH(itemView,callback);
            }

            @Override
            protected void onBindViewHolder(@NonNull PlaceVH holder, final int position, @NonNull final Place model) {
                //bind data
                holder.bindData(model,getContext(),adapter.getRef(position).getKey());
                holder.removeBookmark();
                holder.setItemListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongclick) {
                        //user click a category
                        //get this category id to send to new ListPlace Activity
                        Intent intent=new Intent(getContext(), PlaceDetailActivity.class);
                        intent.putExtra("Place",(Serializable) model);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerMenu.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void bookmarkPlace(String placeId) {
        //Toast.makeText(this,"Đã thêm vào danh sách yêu thích",Toast.LENGTH_LONG).show();

        DatabaseReference userBookmarks=db.getReference("Bookmarks").child(Common.userKey);
        userBookmarks.child(placeId).setValue("true");
    }
}
