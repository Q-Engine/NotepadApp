package com.example.noteapp.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.EditActivity;
import com.example.noteapp.NoteDbOpenHelper;
import com.example.noteapp.R;
import com.example.noteapp.bean.Note;
import com.example.noteapp.util.ToastUtil;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Note> mBeanList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private NoteDbOpenHelper mNoteDbOpenHelper;

    public MyAdapter(Context context, List<Note> mBeanList){//构造方法
        this.mBeanList = mBeanList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mNoteDbOpenHelper = new NoteDbOpenHelper(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//生成以下三个方法
        View view=mLayoutInflater.inflate(R.layout.list_item_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;//ViewHolder的创建,生成可以在ReceycleView中调用的识图view
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {//ViewHolder的绑定
        Note note = mBeanList.get(position);
        holder.mTvTitle.setText(note.getTitle());
        holder.mTvContent.setText(note.getContent());
        holder.mTvTime.setText(note.getCreatedTime());
        //当点击该条记事时，跳转到详情界面，intent跳转
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditActivity.class);//跳转到编辑页面
                intent.putExtra("note", note);//把note实体数据传递过去，只要在这个界面接受就能展示出来
                mContext.startActivity(intent);
            }
        });

        holder.rlContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {//长按该条记事可以删除的方法
                //长按弹出Dialog弹窗,显示编辑或删除
                Dialog dialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                //展示list_item_dialog_layout选择操作界面
                View dialogView = mLayoutInflater.inflate(R.layout.list_item_dialog_layout, null);

                TextView tvDelete = dialogView.findViewById(R.id.tv_delete);
                TextView tvEdit = dialogView.findViewById(R.id.tv_edit);

                tvDelete.setOnClickListener(new View.OnClickListener() {//选择删除
                    @Override
                    public void onClick(View v) {
                        int row = mNoteDbOpenHelper.deleteFromDbById(note.getId());//对数据库里面删除
                        if (row > 0) {
                            removeData(position);
                            ToastUtil.toastShort(mContext,"删除成功！");
                        }else{
                            ToastUtil.toastShort(mContext,"删除失败！");
                        }
                        dialog.dismiss();
                    }
                });

                tvEdit.setOnClickListener(new View.OnClickListener() {//选择编辑功能
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EditActivity.class);
                        intent.putExtra("note", note);//只用跳转到编辑界面就行，和之前写的逻辑一样
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(dialogView);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    public void refreshData(List<Note> notes) {//数据刷新方法,如添加记事后返回主界面后需要调用
        this.mBeanList = notes;
        notifyDataSetChanged();//通知数据集改变的方法，是固有数据刷新的方法
    }

    public void removeData(int pos) {//删除数据
        mBeanList.remove(pos);//从列表移除
        notifyItemRemoved(pos);//固有方法
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTvTitle;//声明相关控件，将数据与控件相关联
        TextView mTvContent;
        TextView mTvTime;
        ViewGroup rlContainer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvTitle = itemView.findViewById(R.id.tv_title);
            this.mTvContent = itemView.findViewById(R.id.tv_content);
            this.mTvTime = itemView.findViewById(R.id.tv_time);
            this.rlContainer = itemView.findViewById(R.id.rl_item_container);
        }
    }
}
