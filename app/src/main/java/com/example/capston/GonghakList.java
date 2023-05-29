package com.example.capston;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GonghakList extends AppCompatActivity {
    private RecyclerView gonghak_recyclearView;
    private CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<List> gonghakList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gonghak_list);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("List");

        Query buildquery = databaseReference.orderByChild("Build").equalTo("공학관 A");

        buildquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gonghakList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    List list = dataSnapshot.getValue(List.class);
                    gonghakList.add(list);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GonghakList", String.valueOf(error.toException()));
            }
        });

        gonghak_recyclearView = findViewById(R.id.gonghak_listview);
        gonghak_recyclearView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        gonghak_recyclearView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(gonghakList, this);
        gonghak_recyclearView.setAdapter(adapter);

    }
}