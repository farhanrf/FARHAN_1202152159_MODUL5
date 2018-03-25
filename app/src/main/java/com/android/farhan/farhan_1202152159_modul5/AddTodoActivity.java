package com.android.farhan.farhan_1202152159_modul5;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTodoActivity extends AppCompatActivity {

    private EditText mNama, mDesc, mPrior;
    private Button mAdd;

    private TodoHelper todoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        //setTitle("Add Todo");

        todoHelper = new TodoHelper(this);

        mAdd=(Button)findViewById(R.id.btnTodoAdd);
        mNama=(EditText)findViewById(R.id.txtTodoName);
        mDesc=(EditText)findViewById(R.id.txtTodoDesc);
        mPrior=(EditText)findViewById(R.id.txtTodoPrior);

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTodoData();
            }
        });
    }


    /*
    */
    public void addTodoData(){
        //Ambil Text
        String nama = mNama.getText().toString();
        String desc = mDesc.getText().toString();
        int prior = Integer.parseInt(mPrior.getText().toString());

        //Query SQL
        String sqlAdd = "INSERT INTO todolist(name, desc, prior) VALUES(?,?,?)";
        SQLiteDatabase db = todoHelper.getWritableDatabase();
        //PreparedStatment: Statment masih berbentuk abstrak (?) sebelum akhirnya di compile
        SQLiteStatement stmt = db.compileStatement(sqlAdd);

        stmt.bindString(1,nama);

        stmt.bindString(2,desc);

        stmt.bindLong(3,prior);


        long rowId = stmt.executeInsert();
        Log.d("SQLITE::DATA","INSERT SUCCESS "+rowId);


        if(rowId!=-1){
            Toast.makeText(this, "Tambah ToDo Berhasil ("+rowId+")", Toast.LENGTH_SHORT).show();
            Intent ini = getIntent();

            ini.putExtra("EXTRA_INSERT_RESULT",rowId);

            setResult(Activity.RESULT_OK,ini);
            finish();
        }else{
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}
