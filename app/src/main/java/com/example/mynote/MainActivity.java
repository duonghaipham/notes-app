package com.example.mynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {
    private ListView listNotes;
    private Button btnAdd;
    private TextView lblAmountNotes;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        loadListNotes();
        registerForContextMenu(listNotes);  // register context menu for list of notes

        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EachNoteActivity.class);
                startActivity(intent);
            }
        });

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EachNoteActivity.class);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {   // delete a note by touch-and-keep and choose delete
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        String currentKey = getKeyByPosition(position);

        if (!currentKey.isEmpty()) {
            editor.remove(currentKey);
            editor.commit();
            loadListNotes();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {    // from each note activity, updating list note
        super.onResume();
        loadListNotes();
    }

    private void loadListNotes() {  // load list of notes whenever initializing or updating
        Map<String, ?> keys = sharedPreferences.getAll();
        List<Note> notes = new Vector<>();

        for (int i = 0; i < keys.size(); i++) {
            List<String> wholeNote = getList(getKeyByPosition(i));
            Note note = new Note(wholeNote.get(0), wholeNote.get(1), wholeNote.get(2), wholeNote.get(3));
            notes.add(note);
        }

        listNotes = (ListView) findViewById(R.id.lv_notes);
        DataAdapter adapter = new DataAdapter(this, notes);
        listNotes.setAdapter(adapter);

        lblAmountNotes = (TextView) findViewById(R.id.lbl_amount_notes);
        //String amountSuffix = (keys.size() <= 1) ? " note" : " notes";
        lblAmountNotes.setText(getResources().getQuantityString(R.plurals.amount_notes, keys.size(),  keys.size()));
    }

    public static List<String> getList(String key) {    // convert a 'string note' to a note
        List<String> arrayItems = new Vector<>();
        String serializedObject = sharedPreferences.getString(key, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return arrayItems;
    }

    public static String getKeyByPosition(int position) {
        String key = "";
        int index = 0;
        Map<String, ?> keys = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (index == position) {
                key = entry.getKey();
                break;
            }
            else
                index++;
        }
        return key;
    }

    public static Note getNoteByPosition(int position) {
        Note note = new Note();
        String key = getKeyByPosition(position);

        note.setTitle(getList(key).get(0));
        note.setContent(getList(key).get(1));
        note.setDateModified(getList(key).get(2));
        note.setTags(getList(key).get(3));
        return note;
    }
}