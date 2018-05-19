package com.example.asus.travisor.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.travisor.Interface.ItemClickListener;
import com.example.asus.travisor.Model.Firebase.PlaceCategories;
import com.example.asus.travisor.R;
import com.squareup.picasso.Picasso;


public class CategoryVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvName;
    public ImageView imgView;
    View itemView;

    private ItemClickListener itemListener;

    public CategoryVH(View itemView) {
        super(itemView);
        this.itemView=itemView;
        itemView.setOnClickListener(this);
    }

    public void setItemListener(ItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    @Override
    public void onClick(View view) {
        itemListener.onClick(view,getAdapterPosition(),false);
    }

    public void binData(PlaceCategories model, Context baseContext) {
        tvName=(TextView) itemView.findViewById(R.id.tvCategory);
        imgView=(ImageView) itemView.findViewById(R.id.img_category);
        tvName.setText(model.getName());
        //Typeface face=Typeface.createFromAsset(baseContext.getAssets(),"font/Nabila.ttf");
        //tvName.setTypeface(face);
        Picasso.with(baseContext).load(model.getImage()).into(imgView);
    }

    public void binDataSimplest(PlaceCategories model, Context applicationContext) {
        tvName=itemView.findViewById(R.id.tv_item_category_simplest);
        tvName.setText(model.getName());
    }
}
