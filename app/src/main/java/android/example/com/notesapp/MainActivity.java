package android.example.com.notesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_TAG = "android.example.com.notesapp";
    private static final String NOTES_TAG = "MyNotes";
    private static final int ADD_NOTE_ACTIVITY_REQUEST_CODE = 0;


    ArrayList<Note> myNotes = new ArrayList<>();
    SharedPreferences sharedPreferences;
    ListView notesList;
    ArrayAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intializing variables
        sharedPreferences = this.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        notesList = findViewById(R.id.listView);

        myNotes = getNotesFromSharedPrefs();

        if(myNotes != null){
            startListView();
        } else {
            myNotes = new ArrayList<>();
            startListView();
        }
    }

    private ArrayList<Note> getNotesFromSharedPrefs(){
        Gson gson = new Gson();
        ArrayList<Note> notesFromSp;

        String json = sharedPreferences.getString(NOTES_TAG, "");

        Type type = new TypeToken<ArrayList<Note>>() {}.getType();
        notesFromSp = gson.fromJson(json, type);

        return notesFromSp;
    }

    public void setList(ArrayList<Note> list){
        Gson gson = new Gson();
        String json = gson.toJson(list);

        sharedPreferences.edit().putString(NOTES_TAG,json).apply();
    }

    public void startListView () {
        myListAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,myNotes);
        notesList.setAdapter(myListAdapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note n = myNotes.get(position);
                goToEditNote(n.getTitle(),n.getText(), position);
            }
        });

        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ShowDeleteDialog(position);
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.addNote:
                goToAddNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToAddNote(){
        Intent i = new Intent(getApplicationContext(), AddNoteActivity.class);
        startActivityForResult(i,ADD_NOTE_ACTIVITY_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToEditNote(String title, String body,int position){
        Intent i = new Intent(getApplicationContext(), AddNoteActivity.class);

        Bundle b = new Bundle();
        b.putString("title",title);
        b.putString("body",body);
        b.putInt("pos",position);
        i.putExtras(b);

        startActivityForResult(i,ADD_NOTE_ACTIVITY_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void ShowDeleteDialog(final int position){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("You want to delete this note?")
                .setMessage("Clicking yes will delete this note")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myNotes.remove(myNotes.get(position));

                        setList(myNotes);

                        myListAdapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bundle b = data.getExtras();

                String title = b.getString("title");
                String body  = b.getString("body");
                int pos      = b.getInt("pos");
                boolean editing = b.getBoolean("editing");

                Log.i("title",title);
                Log.i("body",body);
                Log.i("pos",String.valueOf(pos));

                Note n = new Note(title,body);

                if(editing){
                    myNotes.set(pos,n);
                } else {
                    myNotes.add(n);
                }

                myListAdapter.notifyDataSetChanged();

                setList(myNotes);
            }
        }
    }
}
