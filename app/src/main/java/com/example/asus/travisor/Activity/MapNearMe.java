package com.example.asus.travisor.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.asus.travisor.CustomView.MyInfoWindow;
import com.example.asus.travisor.Interface.ItemClickListener;
import com.example.asus.travisor.Model.Firebase.Place;
import com.example.asus.travisor.Model.Firebase.PlaceCategories;
import com.example.asus.travisor.R;
import com.example.asus.travisor.ViewHolder.CategoryVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MapNearMe extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int WATING_TIME = 60; //in second
    GoogleMap map;
    GoogleApiClient ggApi;
    LatLng currentLocation;
    View mapView;
    RecyclerView rclCategories;

    FirebaseDatabase db;
    FirebaseRecyclerAdapter<PlaceCategories, CategoryVH> adapter;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_near_me);
        initView();
        loadData();

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        mapView=fragment.getView();
        fragment.getMapAsync(this);

        if (ggApi == null) {
            ggApi = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

    }

    private void loadData() {
        db=FirebaseDatabase.getInstance();
        DatabaseReference categoriesRef= db.getReference().child(getString(R.string.DB_Categories));
        Query query=categoriesRef.limitToLast(50);

        FirebaseRecyclerOptions<PlaceCategories> options =
                new FirebaseRecyclerOptions.Builder<PlaceCategories>()
                        .setQuery(query, PlaceCategories.class)
                        .build();
        adapter= new FirebaseRecyclerAdapter<PlaceCategories, CategoryVH>(options) {
            @Override
            public CategoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_category_text,parent,false);
                return new CategoryVH(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryVH holder, final int position, @NonNull final PlaceCategories model) {
                //bind data
                holder.binDataSimplest(model,getBaseContext());
                holder.setItemListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongclick) {
                        //user click a category
                        loadMarkers(adapter.getRef(pos).getKey());
                        Snackbar.make(view, "Tìm kiếm địa điểm "+model.getName()+"...", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        };
        rclCategories.setAdapter(adapter);
    }

    private void loadMarkers(String categoryId) {
        map.clear();
        DatabaseReference placesRef=db.getReference(getString(R.string.DB_Places));
        //get places with categoryId
        Query query=placesRef.orderByChild("categoryId").equalTo(categoryId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                count=0;
                for (DataSnapshot placeSnapshot: snapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    //create maker for each place
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(new LatLng(place.getLat(),place.getLng()));
                    Marker marker=map.addMarker(markerOptions);
                    //set the data of place to the marker
                    marker.setTag(place);
                    count++;
                    if (count==1){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        rclCategories=findViewById(R.id.rcl_map_categories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rclCategories.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setInfoWindowAdapter(new MyInfoWindow(this));

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);

            layoutParams.setMargins(0, 0, 0, 50);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Thiếu quyền truy cập vị trí",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        map.setOnInfoWindowClickListener(this);
        map.setMyLocationEnabled(true);
        updateCurrentLocation(map);
    }

    void updateCurrentLocation(GoogleMap map) {

    }

    private void startLocationUpdate() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(ggApi, locationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(ggApi,this);
    }

    @Override
    protected void onStart() {
        ggApi.connect();
        adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        ggApi.disconnect();
        adapter.stopListening();
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ggApi.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        map.moveCamera(CameraUpdateFactory.
                newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }
}
