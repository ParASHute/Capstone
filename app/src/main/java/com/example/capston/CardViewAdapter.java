package com.example.capston;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class CardViewAdapter extends PagerAdapter {
    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;


    public CardViewAdapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public float getPageWidth(int position) {
        return (0.9f);
      //  return super.getPageWidth(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.viewpager_item, container, false);

        ImageView imageView;
        TextView title;

        imageView = view.findViewById(R.id.card_image);
        title = view.findViewById(R.id.card_title);

        imageView.setImageResource(models.get(position).getImage());
        title.setText(models.get(position).getTitle());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0) {
                   /* Intent intent = new Intent(context, TargetActivity.class);
                    //intent.putExtra("param", models.get(position).getTitle());
                    context.startActivity(intent);*/
                }
                else if(position==1){
                }
                else if(position==2){
                }
                else if(position==3){
                }
            }

        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
