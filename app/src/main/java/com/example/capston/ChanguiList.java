package com.example.capston;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChanguiList extends AppCompatActivity {
    private RecyclerView changui_recyclearView;
    private CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<List> changuiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changui_list);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("List");

        Query buildquery = databaseReference.orderByChild("Build").equalTo("창의관");

        buildquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                changuiList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    List list = dataSnapshot.getValue(List.class);
                    changuiList.add(list);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChanuiList", String.valueOf(error.toException()));
            }
        });

        changui_recyclearView = findViewById(R.id.changui_listview);
        changui_recyclearView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        changui_recyclearView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(changuiList, this);
        changui_recyclearView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back_intent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivity(back_intent);
    }
}