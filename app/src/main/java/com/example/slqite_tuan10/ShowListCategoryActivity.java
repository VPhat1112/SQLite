package com.example.slqite_tuan10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.AlertDialog.Builder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowListCategoryActivity extends Activity {
    List<InforData> list = new ArrayList();
    InforData dataClick = null;
    SQLiteDatabase database = null;
    MySimpleArrayAdapter adapter = null;

    public void ShowListAuthorActivity() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.show_data_layout);
        this.updateUI();
        Button btn = (Button)this.findViewById(R.id.buttonBack);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowListCategoryActivity.this.finish();
            }
        });
    }

    public void updateUI() {
        this.database = this.openOrCreateDatabase("Computer.db", Context.MODE_PRIVATE, (SQLiteDatabase.CursorFactory)null);
        if (this.database != null) {
            Cursor cursor = this.database.query("Category", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
            this.startManagingCursor(cursor);
            InforData header = new InforData();
            header.setField1("STT");
            header.setField2("Mã Computer");
            header.setField3("Tên Computer");
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
            this.adapter = new MySimpleArrayAdapter(this, R.layout.listdata_layout, this.list);
            ListView lv = (ListView)this.findViewById(R.id.listViewComputer);
            lv.setAdapter(this.adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Toast.makeText(ShowListCategoryActivity.this, "View -->" + ((InforData)ShowListCategoryActivity.this.list.get(arg2)).toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ShowListCategoryActivity.this, CreateCategoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("KEY", 1);
                    bundle.putString("getField1", ((InforData)ShowListCategoryActivity.this.list.get(arg2)).getField1().toString());
                    bundle.putString("getField2", ((InforData)ShowListCategoryActivity.this.list.get(arg2)).getField2().toString());
                    intent.putExtra("DATA", bundle);
                    ShowListCategoryActivity.this.dataClick = (InforData)ShowListCategoryActivity.this.list.get(arg2);
                    ShowListCategoryActivity.this.startActivityForResult(intent, 1);
                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                    final InforData data = (InforData)ShowListCategoryActivity.this.list.get(arg2);
                    Toast.makeText(ShowListCategoryActivity.this, "Edit-->" + data.toString(), Toast.LENGTH_LONG).show();
                    AlertDialog.Builder b=new Builder(ShowListCategoryActivity.this);
                    b.setTitle("Remove");
                    b.setMessage("Xóa [" + data.getField2() + " - " + data.getField3() + "] hả?");
                    b.setPositiveButton("Có", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int n = ShowListCategoryActivity.this.database.delete("Category", "id=?", new String[]{data.getField1().toString()});
                            if (n > 0) {
                                Toast.makeText(ShowListCategoryActivity.this, "Remove ok", Toast.LENGTH_LONG).show();
                                ShowListCategoryActivity.this.list.remove(arg2);
                                ShowListCategoryActivity.this.adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ShowListCategoryActivity.this, "Remove not ok", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    b.setNegativeButton("Không", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    b.show();
                    return false;
                }
            });
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            Bundle bundle = data.getBundleExtra("DATA_Category");
            String f2 = bundle.getString("nameCategory");
            String f3 = bundle.getString("Kind");
            String f1 = this.dataClick.getField1().toString();
            ContentValues values = new ContentValues();
            values.put("nameCategory", f2);
            values.put("Kind", f3);
            if (this.database != null) {
                int n = this.database.update("Category", values, "id=?", new String[]{f1});
                if (n > 0) {
                    Toast.makeText(this, "update ok ok ok ", Toast.LENGTH_LONG).show();
                    this.dataClick.setField2(f2);
                    this.dataClick.setField3(f3);
                    if (this.adapter != null) {
                        this.adapter.notifyDataSetChanged();
                    }
                }
            }
        }

    }
}
