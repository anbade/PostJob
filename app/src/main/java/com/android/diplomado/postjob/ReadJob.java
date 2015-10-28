package com.android.diplomado.postjob;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ReadJob extends AppCompatActivity {

    private final static String DESCRIPTION = "DESCRIPTION";
    private final static String TITLE = "TITLE";
    TextView titleView, descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_job);

        //get intent data
        Intent intent = getIntent();
        String title = intent.getStringExtra(TITLE);
        String description = intent.getStringExtra(DESCRIPTION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_read);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        titleView = (TextView)findViewById(R.id.post_title);
        descriptionView = (TextView)findViewById(R.id.post_description);

        titleView.setText(title);
        descriptionView.setText(description);

    }

}
