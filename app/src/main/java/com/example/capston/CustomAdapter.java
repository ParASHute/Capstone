package com.example.capston;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<List> dataList;
    private Context context;

    public CustomAdapter(ArrayList<List> arrayList, Context Context) {
        this.dataList = arrayList;
        this.context = Context;
    }

    public void setItems(ArrayList<List> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    //실제 리스트뷰가 어댑터에 연결된 다음에 뷰 홀더를 최초로 만들어낸다.
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView).load(dataList.get(position).getProfile()).into(holder.profile);
        holder.BuildingName.setText(dataList.get(position).getBuild());
        holder.OfficeName.setText(dataList.get(position).getOffice());
        holder.FloorNum.setText(dataList.get(position).getFloor());

        holder.itemView.setOnClickListener(view -> { // item 클릭 시
            String[] destination = new String[] {dataList.get(position).getLon(), dataList.get(position).getLat()};
            // 여기서 해당 건물의 위도, 경도 값을 intent로 NavigationActivity에게 넘겨줘야함.
            Intent intent = new Intent(context, ListToNavActivity.class);
            intent.putExtra("destination", destination);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // 삼항 연산자로 리스트 사이즈 가져 오는것
        return (dataList != null ? dataList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView BuildingName;
        TextView OfficeName;
        TextView FloorNum;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.profile);
            this.BuildingName = itemView.findViewById(R.id.BuildingName);
            this.OfficeName = itemView.findViewById(R.id.OfficeName);
            this.FloorNum = itemView.findViewById(R.id.FloorNum);
        }
    }
}