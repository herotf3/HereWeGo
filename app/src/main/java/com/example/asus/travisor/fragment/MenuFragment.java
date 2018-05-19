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
import android.widget.Toast;

import com.example.asus.travisor.Activity.ListPlaces;
import com.example.asus.travisor.Interface.ItemClickListener;
import com.example.asus.travisor.Model.Firebase.PlaceCategories;
import com.example.asus.travisor.R;
import com.example.asus.travisor.ViewHolder.CategoryVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by ASUS on 5/14/2018.
 */

public class MenuFragment extends Fragment {
    View rootView;

    FirebaseDatabase db;
    DatabaseReference categoryTable;

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
        categoryTable=db.getReference().child(getString(R.string.DB_Categories));
        loadMenu();
    }

    private void initView() {
        recyclerMenu=rootView.findViewById(R.id.rcl_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(getContext(),2);
        recyclerMenu.setLayoutManager(layoutManager);
    }

    private void loadMenu() {

        query = categoryTable.limitToLast(50);
        FirebaseRecyclerOptions<PlaceCategories> options =
                new FirebaseRecyclerOptions.Builder<PlaceCategories>()
                        .setQuery(query, PlaceCategories.class)
                        .build();
        adapter= new FirebaseRecyclerAdapter<PlaceCategories, CategoryVH>(options) {
            @Override
            public CategoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_category,parent,false);
                return new CategoryVH(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryVH holder, final int position, @NonNull PlaceCategories model) {
                //bind data
                holder.binData(model,getContext());

                final PlaceCategories clickItem=model;
                holder.setItemListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongclick) {
                        //user click a category
                        //get this category id to send to new ListPlace Activity
                        Intent intent=new Intent(getContext(), ListPlaces.class);
                        intent.putExtra("CategoryId",adapter.getRef(pos).getKey());
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
}
