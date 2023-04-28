package com.example.capston;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.security.AccessControlContext;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<List> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<List> arrayList, Context Context) {
        this.arrayList = arrayList;
        this.context = Context;
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
        holder.BuildingName.setText(arrayList.get(position).getBuild());
        holder.OfficeName.setText(arrayList.get(position).getOffice());
        holder.FloorNum.setText(arrayList.get(position).getFloor());
    }

    @Override
    public int getItemCount() {
        // 삼항 연산자로 리스트 사이즈 가져 오는것
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class  CustomViewHolder extends RecyclerView.ViewHolder {
        TextView BuildingName;
        TextView OfficeName;
        TextView FloorNum;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.BuildingName = itemView.findViewById(R.id.BuildingName);
            this.OfficeName = itemView.findViewById(R.id.OfficeName);
            this.FloorNum = itemView.findViewById(R.id.FloorNum);
        }
    }
}
