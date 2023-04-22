package com.example.noteapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.noteapp.bean.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDbOpenHelper extends SQLiteOpenHelper {//生成两个方法，再写一个构造函数
    //以下为相关常量定义
    private static final String DB_NAME = "noteSQLite.db";//数据库名字
    private static final String TABLE_NAME_NOTE = "note";//数据库表的名字
    //以下为创建表的SQL语句，id、标题等属性也一并创建
    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME_NOTE + " (id integer primary key autoincrement, title text, content text, create_time text)";


    public NoteDbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }//构造方法

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);//在这调用上面那条SQL语句，以创建表

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertData(Note note) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("create_time", note.getCreatedTime());

        return db.insert(TABLE_NAME_NOTE, null, values);
    }

    public int deleteFromDbById(String id) {//点击删除弹窗调用这个删除数据库中数据的方法
        SQLiteDatabase db = getWritableDatabase();
//        return db.delete(TABLE_NAME_NOTE, "id = ?", new String[]{id});
//        return db.delete(TABLE_NAME_NOTE, "id is ?", new String[]{id});
        //通过id删除
        return db.delete(TABLE_NAME_NOTE, "id like ?", new String[]{id});
    }

    public int updateData(Note note) {//修改界面调用更新数据方法

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();//以下为新的值
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("create_time", note.getCreatedTime());
        //"id like ?"用来更新指定那条id的记事
        return db.update(TABLE_NAME_NOTE, values, "id like ?", new String[]{note.getId()});
    }

    public List<Note> queryAllFromDb() {//查询数据库中所有数据

        SQLiteDatabase db = getWritableDatabase();
        List<Note> noteList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_NOTE, null, null, null, null, null, null);
        if (cursor != null) {//不为空则查询下一条，以遍历所有数据
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                //查询出数据后构建note实体
                Note note = new Note();
                note.setId(id);
                note.setTitle(title);
                note.setContent(content);
                note.setCreatedTime(createTime);

                noteList.add(note);//把note放到notelite列表中
            }
            cursor.close();//关闭游标
        }
        return noteList;//返回列表
    }

    public List<Note> queryFromDbByTitle(String title) {//根据标题查询内容
        if (TextUtils.isEmpty(title)) {//title传进来后进行判空,什么都不输入时默认查询所有数据
            return queryAllFromDb();//调用queryAllFromDb()方法查询所有数据
        }

        SQLiteDatabase db = getWritableDatabase();
        List<Note> noteList = new ArrayList<>();

        //"title like ?"对标题进行查询。new String[]{"%"+title+"%"}进行模糊匹配查询，只要有相关关键词就能查询到
        Cursor cursor = db.query(TABLE_NAME_NOTE, null, "title like ?", new String[]{"%"+title+"%"}, null, null, null);

        if (cursor != null) {

            while (cursor.moveToNext()) {//那些惯用的查询
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title2 = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));

                Note note = new Note();
                note.setId(id);
                note.setTitle(title2);
                note.setContent(content);
                note.setCreatedTime(createTime);
                noteList.add(note);//将数据放notelnoteist中
            }
            cursor.close();
        }
        return noteList;
    }
}
