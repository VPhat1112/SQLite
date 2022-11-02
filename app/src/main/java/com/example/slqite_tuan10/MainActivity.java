package com.example.slqite_tuan10;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {
    Button btnCreateDatabase = null;
    Button btnInsertCategory = null;
    Button btnShowCategoryList = null;
    Button btnShowCategoryList2 = null;
    Button btnTransaction = null;
    Button btnShowDetail = null;
    Button btnInsertComputer = null;
    public static final int OPEN_CATEGORY_DIALOG = 1;
    public static final int SEND_DATA_FROM_CATEGORY_ACTIVITY = 2;
    SQLiteDatabase database = null;

    public MainActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.btnInsertCategory = (Button)this.findViewById(R.id.btnInsertCategory);
        this.btnInsertCategory.setOnClickListener(new MyEvent());
        this.btnShowCategoryList = (Button)this.findViewById(R.id.buttonShowCategoryList);
        this.btnShowCategoryList.setOnClickListener(new MyEvent());
        this.btnInsertComputer = (Button)this.findViewById(R.id.buttonInsertComputer);
        this.btnInsertComputer.setOnClickListener(new MyEvent());
        this.getDatabase();
    }

    public boolean isTableExists(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", (String[])null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }

            cursor.close();
        }

        return false;
    }

    public SQLiteDatabase getDatabase() {
        try {
            this.database = this.openOrCreateDatabase("Computer.db", MODE_PRIVATE, (SQLiteDatabase.CursorFactory)null);
            if (this.database != null) {
                if (this.isTableExists(this.database, "Category")) {
                    return this.database;
                }

                this.database.setLocale(Locale.getDefault());
                this.database.setVersion(1);
                String sqlCategory = "create table Category (id integer primary key autoincrement,nameCategory text, Kind text)";
                this.database.execSQL(sqlCategory);
                String sqlComputer = "create table Computer (id integer primary key autoincrement,title text,Categoryid integer not null constraint Categoryid references Category(id) on delete cascade)";
                this.database.execSQL(sqlComputer);
                String sqlTrigger = "create trigger fk_insert_Computer before insert on Computer  for each row  begin  \tselect raise(rollback,'them du lieu tren bang Computer bi sai')  \twhere (select id from Category where id=new.Categoryid) is null ; end;";
                this.database.execSQL(sqlTrigger);
                Toast.makeText(this, "OK OK", Toast.LENGTH_LONG).show();
            }
        } catch (Exception var4) {
            Toast.makeText(this, var4.toString(), Toast.LENGTH_LONG).show();
        }

        return this.database;
    }

    public void createDatabaseAndTrigger() {
        if (this.database == null) {
            this.getDatabase();
            Toast.makeText(this, "OK OK", Toast.LENGTH_LONG).show();
        }

    }

    public void showInsertAuthorDialog() {
        Intent intent=new Intent(MainActivity.this, CreateCategoryActivity.class);
        startActivityForResult(intent, OPEN_CATEGORY_DIALOG);
    }

    public void showAuthorList1() {
        Intent intent = new Intent(this, ShowListCategoryActivity.class);
        this.startActivity(intent);
    }

    public void showAuthorList2() {
        Intent intent = new Intent(this, ShowListCategoryActivity2.class);
        this.startActivity(intent);
    }

    public void interactDBWithTransaction()
    {
        if(database!=null)
        {
            database.beginTransaction();
            try
            {
                //làm cái gì đó tùm lum ở đây,
                //chỉ cần có lỗi sảy ra thì sẽ kết thúc transaction
                ContentValues values=new ContentValues();
                values.put("nameCategory", "xx");
                values.put("Kind", "yyy");
                database.insert("Category", null, values);
                database.delete("Category", "ma=?", new String[]{"x"});
                //Khi nào hàm này được gọi thì các thao tác bên trên mới thực hiện được
                //Nếu nó không được gọi thì mọi thao tác bên trên đều bị hủy
                database.setTransactionSuccessful();
            }
            catch(Exception ex)
            {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally
            {
                database.endTransaction();
            }
        }
    }
    /**
     * hàm xử lý kết quả trả về
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==SEND_DATA_FROM_CATEGORY_ACTIVITY)
        {
            Bundle bundle= data.getBundleExtra("DATA_CATEGORY");
            String nameCategory=bundle.getString("nameCategory");
            String Kind=bundle.getString("Kind");
            ContentValues content=new ContentValues();
            content.put("nameCategory", nameCategory);
            content.put("Kind", Kind);
            if(database!=null)
            {
                long Categoryid=database.insert("Category", null, content);
                if(Categoryid==-1)
                {
                    Toast.makeText(MainActivity.this,Categoryid+" - "+ nameCategory +" - "+Kind +" ==> insert error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, Categoryid+" - "+nameCategory +" - "+Kind +" ==>insert OK!", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    /**
     * class xử lý sự kiện
     * @author drthanh
     *
     */
    private class MyEvent implements OnClickListener
    {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(v.getId()==R.id.btnInsertCategory)
            {
                showInsertAuthorDialog();
            }
            else if(v.getId()==R.id.buttonShowCategoryList)
            {
                showAuthorList1();
            }

            else if(v.getId()==R.id.buttonInsertBook)
            {
                Intent intent=new Intent(MainActivity.this, InsertComputerActivity.class);
                startActivity(intent);
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_database, menu);
        return true;
    }
}