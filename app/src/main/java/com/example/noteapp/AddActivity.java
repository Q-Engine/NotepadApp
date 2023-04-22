package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.noteapp.bean.Note;
import com.example.noteapp.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private EditText etTitle,etContent;
    private NoteDbOpenHelper mNoteDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);

    }

    public void add(View view) {
        String title = etTitle.getText().toString();//获取控件内容
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title)) {//标题要求不能为空
            ToastUtil.toastShort(this, "标题不能为空！");//调用util中的ToastUtil工具类,弹窗
            return;
        }

        Note note = new Note();
        //id不需要设置，它在数据库会自增
        note.setTitle(title);
        note.setContent(content);
        note.setCreatedTime(getCurrentTimeFormat());
        long row =mNoteDbOpenHelper.insertData(note);//调用数据库帮助类的方法新建数据
        if (row != -1) {//添加数据成功
            ToastUtil.toastShort(this,"添加成功！");
            this.finish();//结束当前界面，回到上一界面
        }else {//添加数据失败
            ToastUtil.toastShort(this,"添加失败！");
        }

    }

    private String getCurrentTimeFormat() {//用这个方法格式化时间
        //用SimpleDateFormat格式化时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY年MM月dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

}