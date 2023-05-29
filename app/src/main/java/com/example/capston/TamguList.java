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

public class TamguList extends AppCompatActivity {
    private RecyclerView tamgu_recyclearView;
    private CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<List> tamguiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamgu_list);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("List");

        Query buildquery = databaseReference.orderByChild("Build").equalTo("탐구관");

        buildquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tamguiList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    List list = dataSnapshot.getValue(List.class);
                    tamguiList.add(list);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TamguList", String.valueOf(error.toException()));
            }
        });

        tamgu_recyclearView = findViewById(R.id.tamgu_listview);
        tamgu_recyclearView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        tamgu_recyclearView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(tamguiList, this);
        tamgu_recyclearView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back_intent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivity(back_intent);
    }
}