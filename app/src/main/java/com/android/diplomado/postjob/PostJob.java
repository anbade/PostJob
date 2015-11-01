package com.android.diplomado.postjob;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anbade on 26-10-15.
 */
public class PostJob extends AppCompatActivity {

    EditText name, description, contact;
    String namePost, descriptionPost, contactPost;
    LinearLayout mainLayout;
    ArrayList<String> contactsPost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_new_job);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.post_item);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (LinearLayout)findViewById(R.id.main_layout);
        name = (EditText)findViewById(R.id.name_post);
        description = (EditText)findViewById(R.id.description_post);
        contact = (EditText)findViewById(R.id.contact_post);
    }

    public void onPost(View view) {
        contactsPost = new ArrayList<>();
        namePost = name.getText().toString();
        descriptionPost = description.getText().toString();
        int count = mainLayout.getChildCount();
        for (int i=0; i<count;i++){
            LinearLayout elem = (LinearLayout)mainLayout.getChildAt(i);
            EditText text = (EditText)elem.getChildAt(0);
            contactsPost.add(text.getText().toString());
        }
        SetDataAsyncTask asyncTask = new SetDataAsyncTask();
        asyncTask.execute();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addContactView(View view) {
        final LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linear.setLayoutParams(paramsLayout);

        final EditText edit = new EditText(this);
        LinearLayout.LayoutParams paramsEdit = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsEdit.weight = 2.0f;
        edit.setLayoutParams(paramsEdit);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();

        final Button button = new Button(this);
        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(100, 70);
        paramsButton.weight = 1.0f;
        button.setBackground(getDrawable(android.R.drawable.ic_delete));
        button.setLayoutParams(paramsButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.removeView(linear);
            }
        });
        linear.addView(edit);
        linear.addView(button);

        mainLayout.addView(linear);
    }

    private class SetDataAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int response = 0;
            HttpURLConnection urlConnection = null;
            DataOutputStream printout = null;
            Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon()
                    .appendPath("work_posts.json").build();
            URL url = null;
            try {
                url = new URL(buildUri.toString());


                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();

                //{"work_post":{"title":"Title of the Job Post","description":"Text with the description","contacts":["Phone1", "Phone2", ..., "PhoneN"]}}
                JSONObject workPostData = new JSONObject();
                JSONObject subContent = new JSONObject();
                JSONArray contactsArray = new JSONArray();
                for(int i=0; i<contactsPost.size(); i++){
                    contactsArray.put(contactsPost.get(i));
                }
                subContent.put("title", namePost);
                subContent.put("description",descriptionPost);
                subContent.put("contacts",contactsArray);
                workPostData.put("work_post",subContent);

                // Send POST output.
                printout = new DataOutputStream(urlConnection.getOutputStream());

                String str = workPostData.toString();
                byte[] data=str.getBytes("UTF-8");

                printout.write(data);
                printout.flush();
                response = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(printout != null){
                    try {
                        printout.close ();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(Integer response) {
            super.onPostExecute(response);
            int duration = Toast.LENGTH_SHORT;
            if(response == 201){
                CharSequence text = getText(R.string.message_post);
                Toast.makeText(getApplicationContext(), text, duration).show();
                name.setText("");
                description.setText("");
                contact.setText("");
                int count = mainLayout.getChildCount();
                for(int i = 0; i<count; i++){
                    mainLayout.removeView(mainLayout.getChildAt(1));
                }

            }else{
                CharSequence text = getText(R.string.error_message_post);
                Toast.makeText(getApplicationContext(), text, duration).show();
            }

        }
    }
}
