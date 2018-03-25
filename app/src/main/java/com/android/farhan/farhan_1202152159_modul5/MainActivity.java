package com.android.farhan.farhan_1202152159_modul5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView lblNotExist;
    private RecyclerView recyclerView;

    private TodoHelper todoHelper;
    private ArrayList<TodoModel> todos;

    private int shapeColor;
    private int optionShapeColor;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivityForResult(new Intent(getApplicationContext(), AddTodoActivity.class), 201);
            }
        });


        pref = getApplicationContext().getSharedPreferences("pref",0);

        prefEdit=pref.edit();
        Log.d("SharedPreferences::DATA","CardView BgColor: "+pref.getString("shapeColorTXT","#FFFFFF"));


        lblNotExist=(TextView)findViewById(R.id.lblNotExistData);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerviewTodos);



        todoHelper = new TodoHelper(this);

        loadTodoData();

        settingRecyclerBehavior();
    }

    /*
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==201){
            if(resultCode==RESULT_OK){
                loadTodoData();
                int newRowId = data.getIntExtra("EXTRA_INSERT_RESULT",-1);
                Log.d("SQLITE::DATA","INSERT SUCCESS "+newRowId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            //startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), 001);
            changeBgItemColor();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    */
    public void changeBgItemColor(){

        shapeColor = R.color.shapeDefault;

        optionShapeColor = pref.getInt("optionShapeColorSelected",R.id.rShapeColorDefault);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Shape Color");
        final View dialogView = getLayoutInflater().inflate(R.layout.fragment_shapecolor,null);

        final RadioGroup rg =(RadioGroup)dialogView.findViewById(R.id.rgShapeColor);

        rg.check(optionShapeColor);

        dialog.setView(dialogView);

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch(rg.getCheckedRadioButtonId()){

                    case R.id.rShapeColorDefault: optionShapeColor=R.id.rShapeColorDefault; shapeColor=R.color.shapeDefault; break;
                    case R.id.rShapeColorRed: optionShapeColor=R.id.rShapeColorRed; shapeColor=R.color.shapeRed; break;
                    case R.id.rShapeColorBlue: optionShapeColor=R.id.rShapeColorBlue; shapeColor=R.color.shapeBlue; break;
                    case R.id.rShapeColorGreen: optionShapeColor=R.id.rShapeColorGreen; shapeColor=R.color.shapeGreen; break;
                }
                Log.d("SET::COLOR",""+getResources().getString(shapeColor));

                setTodoItemBg();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    /*

    */
    public void setTodoItemBg(){
        String color = getResources().getString(shapeColor);
        //mCardView.setCardBackgroundColor(Color.parseColor("#f5f5f5"));

        prefEdit.putInt("optionShapeColorSelected",optionShapeColor);

        prefEdit.putInt("shapeColor",shapeColor);
        prefEdit.putString("shapeColorTXT",color);
        //prefEdit.putString("shapeColorTXT","#FFFFFF");
        prefEdit.commit();


        Log.d("SharedPreferences::DATA","CardViewSet BgColor: "+pref.getString("shapeColorTXT","#FFFFFF"));

        loadTodoData();
    }


    /*

    */
    public void loadTodoData(){
        /*
        */
        SQLiteDatabase db = todoHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM todolist",null);

        if(res.getCount()<1){
            lblNotExist.setVisibility(View.VISIBLE);
            Log.d("SQLITE::DATA","Tidak Ada Data "+res.getCount());
            return;
        }


        res.moveToFirst();

        todos = new ArrayList<>();
        //for(int c=0;c<cursor.getCount();c++){
        while(res.isAfterLast() == false){
            int id = res.getInt(res.getColumnIndex("id"));
            String nama = res.getString(res.getColumnIndex("name"));
            String desc = res.getString(res.getColumnIndex("desc"));
            int prior = res.getInt(res.getColumnIndex("prior"));

            TodoModel todo = new TodoModel(nama,desc,prior);
            todo.setId(id);
            todos.add(todo);

            res.moveToNext();
        }


        lblNotExist.setVisibility(View.GONE);

        TodoAdapter todoAdapter = new TodoAdapter(todos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(todoAdapter);

        res.close();
    }

    /*
    */
    public void settingRecyclerBehavior(){

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                //Toast.makeText(MainActivity.this, ""+position+"Swiped!", Toast.LENGTH_SHORT).show();
                deleteIt(position);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    /*
    */
    public void deleteIt(int pos){
        /*
        */
        final TodoModel todo = todos.get(pos); // 1 dan 2
        Log.d("SQLITE::DATA","ID "+(todo.getId()));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Hapus Item");
        alertDialog.setMessage("Hapus Item '"+todo.getName()+"' ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //3
                if(todoHelper.deleteTodo(todo.getId())){
                    loadTodoData();
                    Toast.makeText(getApplicationContext(), "Berhasil Hapus "+todo.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loadTodoData();
            }
        });
        alertDialog.show();
    }
}
