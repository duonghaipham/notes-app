package com.example.mynote;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class EachNoteActivity extends AppCompatActivity {
    private EditText txtTitle;
    private EditText txtContent;
    private EditText txtTags;
    private TextView lblArticle;
    private TextView lblDateModified;
    private Button btnSave;
    private String originalContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_note);

        txtTitle = (EditText) findViewById(R.id.txt_title);
        txtContent = (EditText) findViewById(R.id.txt_content);
        txtTags = (EditText) findViewById(R.id.txt_tags);
        lblArticle = (TextView) findViewById(R.id.lbl_article);
        lblDateModified = (TextView) findViewById(R.id.lbl_date_modified);
        btnSave = (Button) findViewById(R.id.btn_save);

        int position = getIntent().getIntExtra("index", -1);
        if (position != -1) {   // position != 1 means there is a specific position passed through intent
            Note note = MainActivity.getNoteByPosition(position);
            txtTitle.setText(note.getTitle());
            txtContent.setText(note.getContent());
            txtTags.setText(note.getTags());
            lblDateModified.setText(getString(R.string.last_modified, note.getDateModified()));
            lblArticle.setText(R.string.view_edit_note);
        }
        else {
            lblArticle.setText(R.string.new_note);
        }

        originalContent = createJson(txtTitle.getText().toString(), txtContent.getText().toString(),
                txtTags.getText().toString(), false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newContent = createJson(txtTitle.getText().toString(), txtContent.getText().toString(),
                        txtTags.getText().toString(), false);   // the same originalContent, but is created when tapping the button
//                originalContent is created as soon as starting the activity, it does not include created/modified date time
//                because it is compared to newContent to check the note whether is changed
                if (originalContent.equals(newContent)) {
                    if (!txtTitle.getText().toString().trim().isEmpty() && !txtContent.getText().toString().trim().isEmpty()
                    && !txtTags.getText().toString().trim().isEmpty()) {    // there is no change
                        Toast toast = Toast.makeText(getApplicationContext(), "Already saved", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {  // all fields in the note are empty
                        Toast toast = Toast.makeText(getApplicationContext(), "Please type first!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else {
                    boolean isOld = false;
                    String rightNow = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                    String key = new String(rightNow);  // key is representative for both created or modified date time, but rightNow just modified one
                    Map<String, ?> keys = MainActivity.sharedPreferences.getAll();
                    for (Map.Entry<String, ?> entry: keys.entrySet()) {
                        List<String> arrayItems;
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<String>>(){}.getType();
                        arrayItems = gson.fromJson(entry.getValue().toString(), type);
                        if (originalContent.equals(createJson(arrayItems.get(0), arrayItems.get(1),
                                arrayItems.get(3), false))) {
                            key = entry.getKey();
                            isOld = true;
                            break;
                        }
                    }
                    if (isOld) {    // remove the note firstly, then created a new one (there is no editing/updating feature in sharedpreferences)
                        MainActivity.editor.remove(key);
                        MainActivity.editor.commit();
                    }
                    MainActivity.editor.putString(key, createJson(txtTitle.getText().toString(),
                            txtContent.getText().toString(), txtTags.getText().toString(), true));
                    MainActivity.editor.commit();
                    lblDateModified.setText(getString(R.string.last_modified, rightNow));
                    lblArticle.setText(R.string.view_edit_note);
                    Toast toast = Toast.makeText(getApplicationContext(), "Save successfully", Toast.LENGTH_SHORT);
                    toast.show();
                }
                originalContent = newContent;
            }
        });
    }

    private String createJson(String title, String content, String tags, boolean isIncludedDateTime) {
        List<String> note = new Vector<>();
        note.add(title);
        note.add(content);
        if (isIncludedDateTime) // to check a note whether is existed
            note.add(LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        note.add(tags);
        Gson gson = new Gson();
        
        return gson.toJson(note);
    }
}