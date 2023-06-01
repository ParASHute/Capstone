package com.example.capston;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchingKey extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<List> OriginalDataList = new ArrayList<>();
    private ArrayList<List> searchList = new ArrayList<>();
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_searchingkey);

        // originalList = new ArrayList<>(); //List의 객체를 담을 리스트 (어뎁터 쪽으로 쏴줄꺼임)

        database = FirebaseDatabase.getInstance(); // 파베 DB연동

        databaseReference = database.getReference("List"); // DB의 리스트

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //DB에서 값 가지고 오는 역할
                OriginalDataList.clear(); // 기존에 값이 있다면 그것을 초기화 시킴
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 리스트 추출
                    List list = snapshot.getValue(List.class); // DB에서 가지고 온 데이터를 List객체에 넣음
                    OriginalDataList.add(list);// 위에 담은걸 베열에 넣어 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //DB에서 데이터를 가지고 오는 중 오류 발생시
                Log.e("SearchingKey", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList.clear();

                if(newText.equals("")) {
                    adapter.setItems(OriginalDataList);
                } else {
                    for(int i = 0; i < OriginalDataList.size(); i++) {
                        if(OriginalDataList.get(i).getOffice() == null)
                            Log.e("afterTextChanged", "OriginalDataList is null");
                        else {
                            if(OriginalDataList.get(i).getOffice().toLowerCase().contains(newText.toLowerCase())) {
                                searchList.add(OriginalDataList.get(i));
                            }
                        }
                    }
                    adapter.setItems(searchList);
                }
                return true;
            }
        });

        /*editText = findViewById(R.id.searchView);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editText.getText().toString();
                searchList.clear();

                if(searchText.equals("")) {
                    adapter.setItems(OriginalDataList);
                } else {
                    for(int i = 0; i < OriginalDataList.size(); i++) {
                        if(OriginalDataList.get(i).getOffice() == null)
                            Log.e("afterTextChanged", "OriginalDataList is null");
                        else {
                            if(OriginalDataList.get(i).getOffice().toLowerCase().contains(searchText.toLowerCase())) {
                                searchList.add(OriginalDataList.get(i));
                            }
                        }
                    }
                    adapter.setItems(searchList);
                }
            }
        });*/

        recyclerView = findViewById(R.id.searchingkey_listview); //메인에 있는 리사이클러뷰랑 연결
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(OriginalDataList, this);
        recyclerView.setAdapter(adapter);
    }
}
