package com.example.asus.travisor.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.travisor.Interface.BookmarkCallback;
import com.example.asus.travisor.Interface.ItemClickListener;
import com.example.asus.travisor.Model.Common;
import com.example.asus.travisor.Model.Firebase.Place;
import com.example.asus.travisor.Model.Firebase.PlaceDetail;
import com.example.asus.travisor.R;
import com.example.asus.travisor.ViewHolder.PlaceVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListPlaces extends AppCompatActivity{
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Place, PlaceVH> adapter;
    Query query;

    //Search functionally
    FirebaseRecyclerAdapter<Place, PlaceVH> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    FirebaseDatabase db;
    DatabaseReference placesTable;
    String categoryId;

    View view;
    TextView tvEmpty;
    FloatingActionButton fabAddPalce;
    public static Location currLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        initView();
        getLocationData();
        //firebase
        db = FirebaseDatabase.getInstance();
        placesTable = db.getReference().child("Places");
        getListData();
        setListener();
    }

    private void setListener() {
        fabAddPalce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),AddPlaceActivity.class);
                intent.putExtra("CategoryId",categoryId);
                startActivity(intent);
            }
        });

    }

    private void getLocationData() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    10);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currLocation==null)
            currLocation=new Location(LocationManager.GPS_PROVIDER);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLocation.setLatitude(location.getLatitude());
                currLocation.setLongitude(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);
    }

    private void initView() {
        recyclerView=findViewById(R.id.rcl_listPlaces);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        view=this.findViewById(R.id.background_listFood);
        tvEmpty=findViewById(R.id.noPlace_message);
        fabAddPalce=findViewById(R.id.fab_addPlace);
    }

    private void getListData() {
        Intent intent=getIntent();
        if (intent!=null){
            categoryId=intent.getStringExtra("CategoryId");
        }
        if (categoryId!=null &&!categoryId.isEmpty() ){
            loadListFood();
        }

        materialSearchBar=findViewById(R.id.searchBar);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(5);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // change suggest list when user type in search bar
                List<String> suggest=new ArrayList<>();
                for (String search:suggestList){
                    if (search.toLowerCase().contains(materialSearchBar.getText().toString().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //search bar close -> restore orinal suggest list
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish -> display suggest list
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void loadListFood() {
        //Select * from placeTable where CategoryId=categoryId
        query = placesTable.orderByChild("categoryId").equalTo(categoryId);
        FirebaseRecyclerOptions<Place> options =
                new FirebaseRecyclerOptions.Builder<Place>()
                        .setQuery(query, Place.class)
                        .build();
        adapter=new FirebaseRecyclerAdapter<Place, PlaceVH>(options) {
            @Override
            public PlaceVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place,parent,false);
                return new PlaceVH(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PlaceVH holder, int position, @NonNull final Place model) {
                holder.bindData(model,getBaseContext(),adapter.getRef(position).getKey());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongclick) {
                        Intent intent=new Intent(getBaseContext(),PlaceDetailActivity.class);
                        intent.putExtra("Place",(Serializable)model);
                        startActivity(intent);
                    }
                });
                if (adapter.getItemCount()==0)
                {
                    view.setBackgroundColor(getResources().getColor(R.color.second_color));
                    tvEmpty.setVisibility(View.VISIBLE);
                }else{
                    view.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
                    tvEmpty.setVisibility(View.GONE);
                }
                holder.setBookmarkCallback(new BookmarkCallback() {
                    @Override
                    public void bookmarkPlace(String placeId) {
                        DatabaseReference bookmRef=db.getReference().child(getString(R.string.DB_Bookmarks)).child(Common.userKey);
                        bookmRef.child(placeId).setValue(model);

                    }
                });
            }


        };
        recyclerView.setAdapter(adapter);
    }

    private void startSearch(CharSequence text) {
        Query query=placesTable.orderByChild("name").equalTo(text.toString());
        FirebaseRecyclerOptions<Place> options=new FirebaseRecyclerOptions.Builder<Place>()
                .setQuery(query,Place.class).build();

        searchAdapter=new FirebaseRecyclerAdapter<Place, PlaceVH>(options) {
            @Override
            public PlaceVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_place,parent,false);
                return new PlaceVH(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull PlaceVH holder, int position, @NonNull final Place model) {
                holder.bindData(model,getBaseContext(),adapter.getRef(position).getKey());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongclick) {
                        Intent intent=new Intent(getBaseContext(), PlaceDetailActivity.class);
                        intent.putExtra("Place",model);
                        startActivity(intent);
                    }
                });
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        placesTable.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Place item=postSnapshot.getValue(Place.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
