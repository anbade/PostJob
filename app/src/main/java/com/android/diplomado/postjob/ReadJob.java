package com.android.diplomado.postjob;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.diplomado.postjob.Data.JobPostDbContract;
import com.android.diplomado.postjob.Data.JobsPostDbHelper;

public class ReadJob extends AppCompatActivity {

    private final static String DESCRIPTION = "DESCRIPTION";
    private final static String TITLE = "TITLE";
    private final static String ID = "ID";
    LinearLayout contentContacts;
    TextView titleView, descriptionView, contactsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_job);

        //get intent data
        Intent intent = getIntent();
        int id = intent.getIntExtra(ID, 1);
        String title = intent.getStringExtra(TITLE);
        String description = intent.getStringExtra(DESCRIPTION);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.title_activity_read_job);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        titleView = (TextView)findViewById(R.id.post_title);
        descriptionView = (TextView)findViewById(R.id.post_description);
        contactsTitle = (TextView)findViewById(R.id.contacts_title);
        contentContacts = (LinearLayout)findViewById(R.id.layout_numbers);

        titleView.setText(title);
        descriptionView.setText(description);

        JobsPostDbHelper dbHelper = new JobsPostDbHelper(ReadJob.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ JobPostDbContract.Contact.TABLE_NAME +" WHERE "+ JobPostDbContract.Contact.JOB_POST_ID +" = '"+id+"'", null);
        if (cursor.moveToFirst()) {
            contactsTitle.setVisibility(View.VISIBLE);
            do {
                TextView number = new TextView(this);
                number.setText(cursor.getString(cursor.getColumnIndex(JobPostDbContract.Contact.NUMBER)));
                contentContacts.addView(number);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}
