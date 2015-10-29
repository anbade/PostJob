package com.android.diplomado.postjob;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.diplomado.postjob.Data.JobPostDbContract;
import com.android.diplomado.postjob.Data.JobsPostDbHelper;

import java.util.ArrayList;

/**
 * Created by Internet on 13/10/2015.
 */
public class MyLoader extends SimpleCursorLoader {

    private  JobsPostDbHelper dbHelper;

    public MyLoader(Context context){
            super(context);
            dbHelper = new JobsPostDbHelper(context);
    }

    @Override
    public Cursor loadInBackground() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = new String[]{JobPostDbContract.JobPost._ID, JobPostDbContract.JobPost.TITLE_COLUMN, JobPostDbContract.JobPost.POSTED_DATE_COLUMN, JobPostDbContract.JobPost.DESCRIPTION_COLUMN};
        Cursor c = db.query(JobPostDbContract.JobPost.TABLE_NAME, columns, null, null, null, null, null);
        return c;
    }
}
