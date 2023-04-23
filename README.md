# NotepadApp
A Notepad app developed based on native Android

##  项目介绍

----------------------

在互联网时代把握个人隐私是一件重要的事情，特别是对于记录我们日常活动、隐私想法的记事本尤应如此。因学习过一定程度的 Android 开发，所以可以使用相关技术开发一个都属于自己的简易记事本 app ，在保护个人隐私的同时也有助于巩固学习的知识点。

本简易记事本 app 可实现记事本相关基本功能：新建记事、删除记事、修改记事、查询记事。开发使用到 SQLite 数据库，所有记事都保存在 noteSQLite.db 数据库中。开发使用的 IDE 为：AndroidStudio Arctic Fox2020.3.1 Patch 4 ，您可根据自己的开发环境完成项目的导入。文档按布局实现到功能实现的顺序讲解，如有需要可按目录跳转到对应章节进行阅读。

## 项目布局实现

---------------

项目开发涉及到以下布局实现：在主页使用 RecyclerView 控件实现列表项布局，使每一条记事竖向排列；在布局右下角使用 FloatingActionButton 控件实现一个不随屏幕滚动的添加新记事的按钮；在标题栏添加一个可以伸缩的搜索按钮。以下为其具体实现。
| <img src=".\img\主界面.png" style="zoom:67%;" /> | <img src=".\img\编辑界面.png" style="zoom:67%;" /> | <img src=".\img\长按选择操作.png" style="zoom:67%;" /> |
| ------------------------------------------------ | -------------------------------------------------- | ------------------------------------------------------ |



### 主页实现列表项布局

为实现在主页以列表的形式展示记事，需要使用 RecyclerView 控件完成列表项布局。以下为实现此布局的方式：

+ 在  src/main/res/layout/activity_main.xml  主布局文件下声明 RecyclerView 控件，代码如下：

  ```xml
  <androidx.recyclerview.widget.RecyclerView
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:id="@+id/rlv"
      />
  ```

+ 在 src/main/java/com/example/noteapp/adapter 文件夹下新建 adapter 文件夹并在里面新建一个 MyAdapter.java 文件。在此文件夹里面创建一个 继承于 RecyclerView.Adapter 的 MyAdapter 类 ，并在这个类里面生成以下方法： onCreateViewHolder()、onBindViewHolder() 和 getItemCount() 。需要注意的是，onCreateViewHolder() 方法应该返回一个 ViewHolder 对象，该对象中包含了列表项的布局视图。

+ 接着在 src/main/res/layout 文件夹下面新建一个 list_item_layout.xml 文件。此布局用于实现 RecyclerView 控件列表项内每一项的布局。这个布局按照自己的需要进行，在此我使用3个 TextView 控件分别用于展示记事的标题、内容、创建时间三条信息。
  <img src=".\img\列表项布局小控件.png" style="zoom:67%;" />

+ 在完成上一步后，需要到 src/main/java/com/example/noteapp/adapter/MyAdapter.java 下创建一个继承于 RecyclerView.ViewHolder 的 MyViewHolder 方法，在这个方法里面定义自定义视图 list_item_layout.xml 里面的各个控件。在这个方法的构造方法里通过 findViewById() 方法找到相应的控件，将其保存在成员变量中。如下代码所示：

  ```java
  class MyViewHolder extends RecyclerView.ViewHolder{
          TextView mTvTitle;//声明相关控件，将数据与控件相关联
          ViewGroup rlContainer;
          public MyViewHolder(@NonNull View itemView) {
              super(itemView);
              this.mTvTitle = itemView.findViewById(R.id.tv_title);
              this.rlContainer = itemView.findViewById(R.id.rl_item_container);
          }
      }
  ```

+ 接着在 src/main/java/com/example/noteapp/adapter/MyAdapter.java 下的onBindViewHolder() 方法中，通过 ViewHolder 中的成员变量获取各个控件，并为它们设置值。

  ```java
  public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
          Note note = mBeanList.get(position);
          holder.mTvTitle.setText(note.getTitle());...
  }
  ```

+ 接着在 MainActivity.java 的 onCreate 方法下声明 initView() 方法、initData() 方法、initEvent() 方法并实现它。在 initView() 方法里面绑定 mRecyclerView 对象，代码如下：

  ```java
  private void initView() {
          mRecyclerView=findViewById(R.id.rlv);
      }
  ```

  在 initEvent() 方法中创建 MyAdapter 对象并将将它绑定给 mRecyclerView 

  ```java
   mMyAdapter=new MyAdapter(this,mNotes);//将adapter创建出来
   mRecyclerView.setAdapter(mMyAdapter);//将mRecyclerView绑定它的适配器
  ```

  在 initData() 方法中创建对象 ArrayList<>() 和 NoteDbOpenHelper(this) 对象，在整理先不用为自定义控件赋于数据，数据会之后在刷新数据的方法里实现。

  > 因为数据刷新的方法不论是刚打开 app 时，还是在新建或修改记事时都会调用以完成向自定义列表控件里面写数据，所以不用在此方法中赋予数据。算是对程序的优化。 

+ 完成以上行为后布局已经完成，现在需要将完成数据的关联。数据关联以及页面的跳转在 src/main/java/com/example/noteapp/adapter/MyAdapter.java 里面的onBindViewHolder() 方法里面完成。通过 Note 对象进行数据的赋予。

  > Note 是根据数据库元素对应创建的对象，在之后的“搭建基本数据库”章节说明其具体实现及含义。

  还需完成在主页点击进入记事、长按选择“修改记事或删除记事”的功能。在点击某条记事后会进入其“修改记事”的界面，在此方法里需要完成：从主页传递数据到修改页功能、点击实现跳转功能。代码如下：

  ```java
   public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {//ViewHolder的绑定
          Note note = mBeanList.get(position);
          holder.mTvTitle.setText(note.getTitle());//...此处省略了其它控件的赋值
          holder.rlContainer.setOnClickListener(new View.OnClickListener() { //当点击该条记事时，跳转到详情界面，intent跳转
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(mContext, EditActivity.class);//跳转到编辑页面
                  intent.putExtra("note", note);//把note实体数据传递到“编辑记事”界面，只要在“编辑记事”界面接受就能展示出来
                  mContext.startActivity(intent);//实现页面跳转到“编辑记事”界面
              }
          });
   }
  ```

  长按记事会弹出 Dialog 弹窗，显示编辑或删除记事。代码如下：

  ```java
  holder.rlContainer.setOnLongClickListener(new View.OnLongClickListener() {
              @Override
              public boolean onLongClick(View v) {//长按该条记事可以删除的方法
                  //长按弹出Dialog弹窗,显示编辑或删除
                  Dialog dialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                  //展示list_item_dialog_layout选择操作界面
                  View dialogView = mLayoutInflater.inflate(R.layout.list_item_dialog_layout, null);
                  TextView tvDelete = dialogView.findViewById(R.id.tv_delete);
                  TextView tvEdit = dialogView.findViewById(R.id.tv_edit);
                  tvDelete.setOnClickListener(new View.OnClickListener() {
                     //选择删除记事方法。（此处省略代码，具体代码请到源代码里看）
                  });
                  tvEdit.setOnClickListener(new View.OnClickListener() {
                      //选择编辑记事方法。（此处省略代码，具体代码请到源代码里看）
                  });
                  dialog.setContentView(dialogView);
                  dialog.setCanceledOnTouchOutside(true);
                  dialog.show();//弹出Dialog弹窗
                  return true;
              }
          });
  ```

### 添加新记事按钮的实现

在主界面右下角添加一个按钮，用于点击后实现新建记事的功能。以下为其具体实现。

+ 在 src/main/res/layout/activity_main.xml 主布局文件下声明 FloatingActionButton 控件，并为其设置一个 add 的点击方法用于点击后实现新建记事。为此按钮设置安卓自带的添加按钮的样式。代码如下：

  ```xml
  <com.google.android.material.floatingactionbutton.FloatingActionButton
      //省略了某些属性，具体请到源代码中查看
      android:src="@android:drawable/ic_input_add"//为按钮设置自带的添加样式
      android:onClick="add"
      tools:ignore="OnClick" />
  ```

+ 在 MainActivity.java 文件里面实现 add 方法，实现点击此按钮后跳转到“新建记事”界面。

  ```java
  public void add(View view) {//点击添加按钮的方法
          Intent intent = new Intent(this,AddActivity.class);//通过Intent跳转到“添加记事”界面
          startActivity(intent);
      }
  ```

### 搜索按钮的实现

在标题栏添加一个搜索按钮，点击按钮后可伸长并弹出输入框，键入文字即可实现搜索功能。以下为其具体实现。

+ 在 src/main/res 文件夹下面新建一个 menu 文件夹，在这个文件夹下面新建一个 menu_main.xml 文件。在这个文件里面的菜单资源文件里面设置一个 item搜索项，并设置为始终显示。代码如下：

  ```xml
  <menu xmlns:android="http://schemas.android.com/apk/res/android"//此处省略代码，具体代码请到源代码里看
  <item
      app:showAsAction="always" //设置为始终显示
      android:icon="@drawable/ic_baseline_search_24" //绑定自带的搜索图标
      app:actionViewClass="androidx.appcompat.widget.SearchView" />
  </menu>
  ```

+ 接着在 MainActivity.java 文件里面实现搜索功能的方法 onCreateOptionsMenu() 。先获取到搜索按钮，再为其完成 被查询时的监听器方法 setOnQueryTextListener() 。这个监听器里面主要要完成当搜索栏内数据改变时，立即执行搜索方法，实现即时查询的功能。代码如下：

  ```java
  searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {//被查询时的监听器
              @Override
              public boolean onQueryTextSubmit(String query) { return false;}//这个方法用不到
              @Override
              public boolean onQueryTextChange(String newText) {//每次输入文本被执行方法
                  mNotes = mNoteDbOpenHelper.queryFromDbByTitle(newText);//根据标题查询,用mNote接收
                  mMyAdapter.refreshData(mNotes);//刷新列表
                  return true;
              }
          });
  ```

  搜索功能的实现通过调用数据库帮助类里面的 queryFromDbByTitle() 方法实现对标题里面是否含有关键词进行查询。主要实现查询功能的数据库查询语句如下：

  ```java
   //"title like ?"对标题进行查询。new String[]{"%"+title+"%"}进行模糊匹配查询，只要有相关关键词就能查询到
   Cursor cursor = db.query(TABLE_NAME_NOTE, null, "title like ?", new String[]{"%"+title+"%"}, null, null, null);
  ```

### 新建记事界面和修改记事界面的实现

由分析可知，“新建记事”界面和“修改记事”界面布局是可以共用的，因为不论新建还是修改记事，都是对“标题”、“内容”的改变，所以基本布局是可以共用的。两个界面的不同之处在于“新建记事”界面需要有“添加记事的功能”，“修改记事”界面需要有“保存记事的功能”。
<img src=".\img\新建与修改记事.png" style="zoom:67%;" />

“新建记事”界面点击添加按钮调用的 add() 方法在 src/main/java/com/example/noteapp/AddActivity.java 文件中实现。首先获取到控件的内容并判断标题内容是否为空，若不为空则将获取到的数据赋予给 note 对象，之后调用数据库帮助类里的 insertData() 方法将 note 对象的数据插入到记事本数据库中。若数据添加成功则弹出提示并将本“新建记事”页面关闭回到记事本主页，失败则弹出 Toast 窗口进行提示（ Toast 窗口调用 src/main/java/com/example/noteapp/util/ToastUtil.java 中封装的 Toast 方法，可打开源代码进行查看）。

```java
public void add(View view) { //此处存在代码省略，具体代码请到源代码里看
        String title = etTitle.getText().toString();...//获取控件内容
        Note note = new Note();
        note.setTitle(title);... //id不需要设置，它在数据库会自增
        long row =mNoteDbOpenHelper.insertData(note);//调用数据库帮助类的方法新建数据
        if (row != -1) {//添加数据成功
            ToastUtil.toastShort(this,"添加成功！");
            this.finish();//结束当前界面，回到上一界面
        }else {//添加数据失败
            ToastUtil.toastShort(this,"添加失败！");
        }
    }
```

“修改记事”界面首先需要接收从主界面传输过来的数据，功能在src/main/java/com/example/noteapp/EditActivity.java 文件内 initData() 方法里面实现，若判断接收到的数据不为空则设置到相对应的标题、内容控件里面。代码如下所示：

```java
private void initData() {
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");//接受传递过来的Intent
        if (note != null) {//不为空则将数据设置到控件中显示
            etTitle.setText(note.getTitle());  etContent.setText(note.getContent());
        }
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);//初始化帮助类
    }
```

之后需要实现点击“修改记事”界面里面的保存按钮实现对修改后数据的保存，功能在 EditActivity.java 文件内 save() 方法实现。此方法和“修改记事”界面的保存方法一样，只不过调用了数据库帮助类里面的更新数据方法 updateData() 。

```java
long rowId = mNoteDbOpenHelper.updateData(note);//调用更新数据的方法
```

## 项目功能实现

--------------------------

此简易记事本 APP 主要功能有：新建记事、修改记事、删除记事、查询记事，能够实现对记事本数据库的增删改查操作。

### 数据库连接

完成以上步骤后接下来要实现与 SQLlite 数据库进行数据的绑定与交互。首先在 src/main/java/com/example/noteapp/util 文件夹下新建一个 NoteDbOpenHelper.java 文件，在此文件内实现数据库帮助类。接着在 src/main/java/com/example/noteapp 文件夹下面新建一个 bean 文件夹，在这个文件夹里面新建一个 note.java 文件，在此文件里面实现数据模型 Note 类，将数据库中的每一项数据与这个类的属性进行类似于绑定的操作，这个类需要包含所有要查询和展示的字段。接着回到数据库帮助类 NoteDbOpenHelper 里面，在此方法内可实现对于数据库信息的查询、添加等操作。文档前面部分已对 RecycleView 布局获取数据做出了说明，此处不再说明。以下为使用数据库创建语句新建数据库表单：

```sqlite
//以下为创建表的SQL语句，id、标题等属性也一并创建
    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME_NOTE + " (id integer primary key autoincrement, title text, content text, create_time text)";
```

### 新建记事功能实现

添加记事功能实现由数据库帮助类里面的 insertData(note) 方法实现，在“新建记事”界面点击添加按钮后调用。此方法先创建数据库对象，并将从“新建记事”界面传递过来 note 对象的每项数据赋值给 values 对象对应的属性，之后使用数据库的插入数据方法向数据库中插入新的数据。代码如下所示：

```java
public long insertData(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("create_time", note.getCreatedTime());
        return db.insert(TABLE_NAME_NOTE, null, values);
    }
```

往数据库内插入新数据后，需要刷新主界面控件展示的数据。通过在 MainActivity.java 文件里面声明生命周期函数 onResume() ，在此函数里面调用本文件下的 refreshDataFromDb() 方法完成数据的刷新。在 refreshDataFromDb() 方法中通过调用适配器 mMyAdapter 中的 refreshData(mNotes) 方法完成数据的刷新。

> 需要说明的是，因为 MainActivity.java 的生命周期函数在每次从其它界面完成操作返回主界面时都会进行。也就是说，完成操作后无需主动调用某个数据刷新的方法，生命周期函数会自动调用数据刷新方法 refreshDataFromDb() 完成数据的刷新操作。新建记事、修改记事、删除记事、查询记事完成后主界面的生命周期函数会主动调用数据刷新函数进行数据刷新。

```java
@Override
    protected void onResume() {//生命周期函数，在这个方法中完成数据刷新的功能
        super.onResume();
        refreshDataFromDb();//调用这个方法完成数据刷新
    }
    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);//调用适配器中的方法完成数据刷新，因为适配器是数据与列表的桥梁
    }
```

在 MyAdapter.java 文件下的refreshData(mNotes) 方法通过通知数据集改变的方法完成数据的刷新，具体实现如下：

```java
public void refreshData(List<Note> notes) {//数据刷新方法,如添加记事后返回主界面后需要调用
        this.mBeanList = notes;
        notifyDataSetChanged();//通知数据集改变的方法，是固有数据刷新的方法
    }
```

### 修改记事功能实现

修改记事功能实现由数据库帮助类里面的 updateData(note) 方法实现，在“修改记事”界面点击保存按钮后调用。此方法先创建数据库对象，并将从“修改记事”界面传递过来 note 对象的每项数据赋值给 values 对象对应的属性，之后使用数据库的更新数据方法对数据库中对应 id 的那一行的数据进行全部更新。完成操作后主界面会进行数据属性操作，上个章节中已说明。代码如下所示：

```java
public int updateData(Note note) {//修改界面调用更新数据方法
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();//以下为新的值
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("create_time", note.getCreatedTime());
        //"id like ?"用来更新指定那条id的记事
        return db.update(TABLE_NAME_NOTE, values, "id like ?", new String[]{note.getId()});
    }
```

### 删除记事实现

删除记事功能实现由数据库帮助类里面的 deleteFromDbById(id) 方法实现，在主界面长按记事并选择删除记事时调用。此方法先创建数据库对象，再通过数据库删除数据方法将数据库中与传递过来的对应记事的 id 相同的记事删除。完成操作后主界面会进行数据属性操作，上个章节中已说明。代码如下所示：

```java
public int deleteFromDbById(String id) {//点击删除弹窗调用这个删除数据库中数据的方法
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME_NOTE, "id like ?", new String[]{id});  //通过id删除
    }
```

### 查询记事实现

删除记事功能实现由数据库帮助类里面的 queryFromDbByTitle(newText) 方法实现，在主界面的标题栏点击所示按钮时调用。刚点击搜索栏时输入框内未输入内容，此时默认数据库中所有的记事。当输入框监听到搜索栏内的内容发生改变时便会更新搜索内容，搜索通过数据库查询语句对输入的内容与数据库中已有记事的标题进行关键词比较完成搜索。这种通过关键词比较进行的搜索在某种程度上可以说是模糊搜索。完成搜索后需要主动调用 refreshData() 属性刷新数据函数进行数据的刷新，因为搜索的方法是在主界面内实现的，所以生命周期函数不再主动调用数据刷新函数进行数据的刷新操作。实现查询功能的代码语句如下所示：

```sqlite
//"title like ?"对标题进行查询。new String[]{"%"+title+"%"}进行模糊匹配查询，只要有相关关键词就能查询到
        Cursor cursor = db.query(TABLE_NAME_NOTE, null, "title like ?", new String[]{"%"+title+"%"}, null, null, null);
```

## 项目总结

----------------------------

此简易记事本 APP 开发使用的是原生安卓开发技术，使用 SQLite 数据库进行数据的存储。在开发过程中也遇到了一些困难：对数据库完成数据的各种操作是开发的重难点，因为记事本基础功能的实现与数据库数据的操作息息相关；使用体验优化：优化app的交互和界面设计，以便能够更好的使用记事本功能。不过最终还是成功地开发出了一个功能简单但实用的记事本app。在这个项目中，我们也学到了很多关于移动应用开发的知识和经验。

## 贡献

---------

如果您对该项目感兴趣，请随时提交您的建议和意见。无论想是改进代码还是文档均可提交相关请求，让我们共同将此项目打造成一个功能更丰富、体验更优良的程序！
