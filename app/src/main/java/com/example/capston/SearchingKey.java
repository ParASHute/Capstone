package com.example.capston;

import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

public class SearchingKey extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<List> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_searchingkey);

        recyclerView = findViewById(R.id.recyclerView); //메인에 있는 리사이클러뷰랑 연결
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //List의 객체를 담을 리스트 (어뎁터 쪽으로 쏴줄꺼임)

        database = FirebaseDatabase.getInstance(); // 파베 DB연동

        databaseReference = database.getReference("List"); // DB의 리스트 이름
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //DB에서 값 가지고 오는 역할
                arrayList.clear(); // 기존에 값이 있다면 그것을 초기화 시킴
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 리스트 추출
                    List list = snapshot.getValue(List.class); // DB에서 가지고 온 데이터를 List객체에 넣음
                    arrayList.add(list);// 위에 담은걸 베열에 넣어 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //DB에서 데이터를 가지고 오는 중 오류 발생시
                Log.e("SearchingKey", String.valueOf(databaseError.toException())); // 에러문 출력

            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
    }

    private void firebaseUserSearch() {
        /* FirebaseRecyclerAdapter<>*/
    }
}