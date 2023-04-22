package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.noteapp.adapter.MyAdapter;
import com.example.noteapp.bean.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;//RecyclerView控件声明

    private FloatingActionButton mBtnAdd;
    private List<Note> mNotes;//数据
    private MyAdapter mMyAdapter;//适配器

    private NoteDbOpenHelper mNoteDbOpenHelper;//数据库帮助类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();//initview方法
        initData();//数据传递到列表中
        initEvent();//将适配器与recycleView绑定
        
    }

    @Override
    protected void onResume() {//生命周期函数，在这个方法中完成数据刷新的功能
        super.onResume();
        
        refreshDataFromDb();//调用这个方法完成数据刷新
    }

    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);//调用适配器中的方法完成数据刷新，因为适配器是数据与列表的桥梁
    }

    private void initEvent() {
        mMyAdapter=new MyAdapter(this,mNotes);//将adapter创建出来
        mRecyclerView.setAdapter(mMyAdapter);//将mRecyclerView绑定它的适配器
        //设置一个布局的管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);//列表
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void initData() {
        mNotes = new ArrayList<>();//Note信息
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
//        //以下为尝试列表数据是否成功
//        for (int i = 0; i < 30; i++) {
//            Note note = new Note();
//            note.setTitle("这是标题"+i);
//            note.setContent("这是内容"+i);
//            note.setCreatedTime(getCurrentTimeFormat());
//            mNotes.add(note);//没创建一条就将数据加到列表中
//        }
//        mNotes=getDataFromDB();//从数据库中拿数据
//        优化了这条语句，不用从数据库中拿数据，因为代码执行到onresum方法时会刷新数据，避免重复刷新数据
    }

    private List<Note> getDataFromDB() {

        return mNoteDbOpenHelper.queryAllFromDb();
    }

    private String getCurrentTimeFormat() {//用这个方法格式化时间
        //用SimpleDateFormat格式化时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY年MM月dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    private void initView() {
        mRecyclerView=findViewById(R.id.rlv);
    }

    public void add(View view) {//添加按钮
        Intent intent = new Intent(this,AddActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//搜索按钮得有的方法
        getMenuInflater().inflate(R.menu.menu_main, menu);//资源
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();//找到那个searchView

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {//被查询时的监听器
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//每次输入文本被执行方法
                mNotes = mNoteDbOpenHelper.queryFromDbByTitle(newText);//根据标题查询,用mNote接收
                mMyAdapter.refreshData(mNotes);//刷新列表
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//搜索按钮得有的方法
        return super.onOptionsItemSelected(item);
    }
}