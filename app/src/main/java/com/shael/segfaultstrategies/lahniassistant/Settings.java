package com.shael.segfaultstrategies.lahniassistant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class Settings extends Activity {

    private EditText nameEditText;
    private EditText numberEditText;
    private Button saveButton;

    private String name;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        numberEditText = (EditText) findViewById(R.id.numberEditText);
        saveButton = (Button) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString() != null) {
                    if (numberEditText.getText().toString() != null) {
                        name = nameEditText.getText().toString();
                        number = numberEditText.getText().toString();

                        Intent intent = new Intent();
                        intent.putExtra("Name", name);
                        intent.putExtra("Number", number);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }
}
