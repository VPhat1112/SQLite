package com.example.slqite_tuan10;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowListCategoryActivity2 extends ListActivity {
    List<InforData> list = new ArrayList();

    public ShowListCategoryActivity2() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.show_data2_layout);
        this.updateUI();
        Button btn = (Button)this.findViewById(R.id.buttonBack);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowListCategoryActivity2.this.finish();
            }
        });
    }

    public void updateUI() {
        SQLiteDatabase database = this.openOrCreateDatabase("Computer.db", MODE_PRIVATE, (SQLiteDatabase.CursorFactory)null);
        if (database != null) {
            Cursor cursor = database.query("tblAuthors", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
            this.startManagingCursor(cursor);
            InforData header = new InforData();
            header.setField1(cursor.getColumnName(0));
            header.setField2(cursor.getColumnName(1));
            header.setField3(cursor.getColumnName(2));
            this.list.add(header);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                InforData data = new InforData();
                data.setField1(cursor.getInt(0));
                data.setField2(cursor.getString(1));
                data.setField3(cursor.getString(2));
                this.list.add(data);
                cursor.moveToNext();
            }

            cursor.close();
            MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, R.layout.listdata_layout, this.list);
            this.setListAdapter(adapter);
        }

    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(this, "View->" + ((InforData)this.list.get(position)).toString(), Toast.LENGTH_LONG).show();
    }
}
