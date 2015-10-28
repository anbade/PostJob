package com.android.diplomado.postjob;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.diplomado.postjob.Data.JobsPostDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.android.diplomado.postjob.Data.JobPostDbContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ListView listView;
    CursorAdapterMine adapter;
    private final static int JOB_POST_LOADER_ID = 1;
    private final static String DESCRIPTION = "DESCRIPTION";
    private final static String TITLE = "TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        listView = (ListView)findViewById(R.id.listView);

        /* Bind adapater to my ListView */
        adapter = new CursorAdapterMine(this, null , 0);
        listView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(JOB_POST_LOADER_ID, null, this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ReadJob.class);
                Cursor cursor = (Cursor)adapter.getItem(position);
                String title = cursor.getString(cursor.getColumnIndex(JobPost.TITLE_COLUMN));
                String date = cursor.getString(cursor.getColumnIndex(JobPost.POSTED_DATE_COLUMN));
                String description = cursor.getString(cursor.getColumnIndex(JobPost.DESCRIPTION_COLUMN));
                System.out.println(cursor + " -- " +title);
                intent.putExtra(DESCRIPTION, description);
                intent.putExtra(TITLE, title);
                startActivity(intent);
            }
        });

        /* Syncronice data when user enter this activity */
        syncData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sync) {
            syncData();
            return true;
        }
        if (id == R.id.new_job) {
            Intent intent = new Intent(this, PostJob.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    public void syncData() {
        GetDataAsyncTask asyncTask = new GetDataAsyncTask();
        asyncTask.execute();
    }

    private class GetDataAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String clientInfoJSON = "";
            Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon()
                    .appendPath("work_posts.json").build();
            URL url = null;
            try {
                url = new URL(buildUri.toString());


                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                clientInfoJSON = buffer.toString();

                String response = clientInfoJSON;
                try {

                    JSONArray new_array = new JSONArray(response);
                    JobsPostDbHelper dbHelper = new JobsPostDbHelper(MainActivity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    for (int i = 0, count = new_array.length(); i < count; i++) {
                        try {
                            JSONObject jsonObject = new_array.getJSONObject(i);
                            String title = jsonObject.getString("title").toString();
                            String date = jsonObject.getString("posted_date").toString();
                            int id = jsonObject.getInt("id");
                            String description = jsonObject.getString("description");

                            ContentValues contentValues = new ContentValues();

                            contentValues.put(JobPost._ID, id);
                            contentValues.put(JobPost.TITLE_COLUMN, title);
                            contentValues.put(JobPost.POSTED_DATE_COLUMN, date);
                            contentValues.put(JobPost.DESCRIPTION_COLUMN, description);
                            db.insert(JobPost.TABLE_NAME, null, contentValues);


                            //add Contact data
                            JSONArray contactArray = jsonObject.getJSONArray("contacts");
                            for(int j = 0, count2 = contactArray.length(); j < count2; j++){
                                String number = contactArray.getString(j);
                                ContentValues contentValues2 = new ContentValues();
                                //contentValues2.put(Contact._ID,id);
                                contentValues2.put(Contact.NUMBER, number);
                                contentValues2.put(Contact.JOB_POST_ID,id);
                                db.insert(Contact.TABLE_NAME, null, contentValues2);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    // tv.setText("error2");
                }




            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return clientInfoJSON;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            getSupportLoaderManager().getLoader(JOB_POST_LOADER_ID).onContentChanged();
        }
    }
}

