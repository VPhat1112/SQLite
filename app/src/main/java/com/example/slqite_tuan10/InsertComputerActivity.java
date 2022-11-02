package com.example.slqite_tuan10;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InsertComputerActivity extends Activity {
    SQLiteDatabase database = null;
    List<InforData> listComputer = null;
    List<InforData> listCategory = null;
    InforData CategoryData = null;
    MySimpleArrayAdapter adapter = null;

    public InsertComputerActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.insert_computer);
        Spinner pinner = (Spinner)this.findViewById(R.id.spinner1);
        this.listCategory = new ArrayList();
        InforData d1 = new InforData();
        d1.setField1("_");
        d1.setField2("Show All");
        d1.setField3("_");
        this.listCategory.add(d1);
        this.database = this.openOrCreateDatabase("Computer.db", Context.MODE_PRIVATE,(SQLiteDatabase.CursorFactory)null);
        if (this.database != null) {
            Cursor cursor = this.database.query("Category", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                InforData d = new InforData();
                d.setField1(cursor.getInt(0));
                d.setField2(cursor.getString(1));
                d.setField3(cursor.getString(2));
                this.listCategory.add(d);
                cursor.moveToNext();
            }

            cursor.close();
        }

        this.adapter = new MySimpleArrayAdapter(this,R.layout.listdata_layout, this.listCategory);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinner.setAdapter(adapter);
        pinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == 0) {
                    InsertComputerActivity.this.CategoryData = null;
                    InsertComputerActivity.this.loadAllListBook();
                } else {
                    InsertComputerActivity.this.CategoryData = (InforData)InsertComputerActivity.this.listCategory.get(arg2);
                    InsertComputerActivity.this.loadListBookByAuthor(InsertComputerActivity.this.CategoryData.getField1().toString());
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                InsertComputerActivity.this.CategoryData = null;
            }
        });


        Button btnInsertBook = (Button)this.findViewById(R.id.buttonInsertComputer);
        btnInsertBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (InsertComputerActivity.this.CategoryData == null) {
                    Toast.makeText(InsertComputerActivity.this, "Please choose an Category to insert", Toast.LENGTH_LONG).show();
                } else {
                    EditText txtTitle = (EditText)InsertComputerActivity.this.findViewById(R.id.editTextTitle);
                    ContentValues values = new ContentValues();
                    values.put("title", txtTitle.getText().toString());
                    values.put("Categoryid", InsertComputerActivity.this.CategoryData.getField1().toString());
                    long bId = InsertComputerActivity.this.database.insert("tblBooks", (String)null, values);
                    if (bId > 0L) {
                        Toast.makeText(InsertComputerActivity.this, "Insert Book OK", Toast.LENGTH_LONG).show();
                        InsertComputerActivity.this.loadListBookByAuthor(InsertComputerActivity.this.CategoryData.getField1().toString());
                    } else {
                        Toast.makeText(InsertComputerActivity.this, "Insert Book Failed", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    public void loadAllListBook() {
        Cursor cur = this.database.query("tblBooks", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
        cur.moveToFirst();
        this.listComputer = new ArrayList();

        while(!cur.isAfterLast()) {
            InforData d = new InforData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            this.listComputer.add(d);
            cur.moveToNext();
        }

        cur.close();
        this.adapter = new MySimpleArrayAdapter(this, 2130903045, this.listComputer);
        ListView lv = (ListView)this.findViewById(R.id.listViewComputer);
        lv.setAdapter(this.adapter);
    }

    public void loadListBookByAuthor(String authorid) {
        Cursor cur = this.database.query("tblBooks", (String[])null, "authorid=?", new String[]{authorid}, (String)null, (String)null, (String)null);
        cur.moveToFirst();
        this.listComputer = new ArrayList();

        while(!cur.isAfterLast()) {
            InforData d = new InforData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            this.listComputer.add(d);
            cur.moveToNext();
        }

        cur.close();
        this.adapter = new MySimpleArrayAdapter(this, 2130903045, this.listComputer);
        ListView lv = (ListView)this.findViewById(R.id.listViewComputer);
        lv.setAdapter(this.adapter);
    }





    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.insert_computer, menu);
        return true;
    }
}
