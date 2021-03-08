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
//    private Button btnCancel;
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
//        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSave = (Button) findViewById(R.id.btn_save);

        int position = getIntent().getIntExtra("index", -1);
        if (position != -1) {
            Note note = MainActivity.getNoteByPosition(position);
            txtTitle.setText(note.getTitle());
            txtContent.setText(note.getContent());
            txtTags.setText(note.getTags());
            lblDateModified.setText("Last modified: " + note.getDateModified());
            lblArticle.setText("View or edit your note");
        }
        else {
            lblArticle.setText("Create a new note...");
        }

        originalContent = createJson(txtTitle.getText().toString(), txtContent.getText().toString(),
                txtTags.getText().toString(), false);

//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newContent = createJson(txtTitle.getText().toString(), txtContent.getText().toString(),
                        txtTags.getText().toString(), false);

                if (originalContent.equals(newContent)) {
                    if (!txtTitle.getText().toString().trim().isEmpty() && !txtContent.getText().toString().trim().isEmpty()
                    && !txtTags.getText().toString().trim().isEmpty()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Already saved", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please type first!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else {
                    Boolean isOld = false;
                    String rightNow = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                    String key = new String(rightNow);
                    Map<String, ?> keys = MainActivity.sharedPreferences.getAll();
                    for (Map.Entry<String, ?> entry: keys.entrySet()) {
                        List<String> arrayItems = new Vector<String>();
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
                    if (isOld) {
                        MainActivity.editor.remove(key);
                        MainActivity.editor.commit();
                    }
                    MainActivity.editor.putString(key, createJson(txtTitle.getText().toString(),
                            txtContent.getText().toString(), txtTags.getText().toString(), true));
                    MainActivity.editor.commit();
                    lblDateModified.setText("Last modified: " + rightNow);
                    lblArticle.setText("View or edit your note");
                    Toast toast = Toast.makeText(getApplicationContext(), "Save successfully", Toast.LENGTH_SHORT);
                    toast.show();
                }
                originalContent = newContent;
            }
        });
    }

    private String createJson(String title, String content, String tags, Boolean isIncludedDateTime) {
        List<String> note = new Vector<String>();
        note.add(title);
        note.add(content);
        if (isIncludedDateTime)
            note.add(LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        note.add(tags);
        Gson gson = new Gson();
        String json = gson.toJson(note);

        return json;
    }
}