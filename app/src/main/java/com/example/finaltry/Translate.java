package com.example.finaltry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Translate extends Activity implements AdapterView.OnItemSelectedListener {
    TextView text;
    Spinner spin;
    Button search;
    String searchvalue,item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        searchvalue = getIntent().getStringExtra("edittext");
        text = findViewById(R.id.text);
        text.setText(searchvalue);
        spin = findViewById(R.id.spinner);
        search= findViewById(R.id.button);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchgoogle();
            }
        });
    }



    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id)
    {

        item = parent.getItemAtPosition(pos).toString();
        Toast.makeText(parent.getContext(),"selected: "+item, Toast.LENGTH_SHORT).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    private void searchgoogle() {
        String q=searchvalue+ ", translate to"+item+"language";
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.googlequicksearchbox.SearchActivity");
        intent.putExtra("query", q);
        startActivity(intent);


    }
}
