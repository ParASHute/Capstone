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

public class MiraeList extends AppCompatActivity {
    private RecyclerView mirae_recyclearView;
    private CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<List> miraeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirae_list);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("List");

        Query buildquery = databaseReference.orderByChild("Build").equalTo("미래관");

        buildquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                miraeList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    List list = dataSnapshot.getValue(List.class);
                    miraeList.add(list);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MiraeList", String.valueOf(error.toException()));
            }
        });

        mirae_recyclearView = findViewById(R.id.mirae_listview);
        mirae_recyclearView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mirae_recyclearView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(miraeList, this);
        mirae_recyclearView.setAdapter(adapter);

    }
}