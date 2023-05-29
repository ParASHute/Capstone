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

public class SangSangList extends AppCompatActivity {
    private RecyclerView sangsang_recyclearView;
    private CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<List> sangsangList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sang_sang_list);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("List");

        Query build_query = databaseReference.orderByChild("Build").equalTo("상상관");

        build_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sangsangList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    List list = dataSnapshot.getValue(List.class);
                    sangsangList.add(list);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SangSangList", String.valueOf(error.toException()));
            }
        });

        sangsang_recyclearView = findViewById(R.id.sangsang_listview);
        sangsang_recyclearView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        sangsang_recyclearView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(sangsangList, this);
        sangsang_recyclearView.setAdapter(adapter);
    }
}