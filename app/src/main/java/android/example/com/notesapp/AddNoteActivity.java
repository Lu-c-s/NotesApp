package android.example.com.notesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNoteActivity extends AppCompatActivity {
    EditText title;
    EditText body;
    Button addNote;

    Boolean editing = false;
    int pos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        title = findViewById(R.id.editTextTitle);
        body = findViewById(R.id.editTextBody);
        addNote = findViewById(R.id.button);

        addNote.setOnClickListener(addHandle);

        Bundle b = getIntent().getExtras();

        if(b != null){
            String titleText = b.getString("title");
            String bodyText = b.getString("body");
            pos = b.getInt("pos");
            editing = true;

            this.setTitle(titleText);
            addNote.setText(R.string.edit_note);

            if(titleText != null|| bodyText != null ){
                String textT = titleText == null ? " " : titleText;
                String textB = bodyText == null ? " " : bodyText;

                title.setText(textT);
                body.setText(textB);
            }

        }

    }

    View.OnClickListener addHandle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String titleText =  title.getText().toString();
            String bodyText = body.getText().toString();

            if(titleText.equals("") || bodyText.equals("")){
                Toast.makeText(getApplicationContext(),"Title or note body can't be empty.", Toast.LENGTH_SHORT).show();
            } else {
                if(!editing)
                    sendNote(titleText, bodyText);
                else
                    editNote(titleText,bodyText,pos);
            }
        }
    };

    public void sendNote (String titleT, String bodyT) {

        Intent i = new Intent();

        Bundle b = new Bundle();
        b.putString("title",titleT);
        b.putString("body",bodyT);
        b.putBoolean("editing",editing);

        i.putExtras(b);

        setResult(RESULT_OK,i);
        finish();
    }

    public void editNote (String titleT, String bodyT,int cPos) {
        Intent i = new Intent();

        Bundle b = new Bundle();
        b.putString("title",titleT);
        b.putString("body",bodyT);
        b.putBoolean("editing",editing);
        b.putInt("pos",cPos);

        i.putExtras(b);

        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
