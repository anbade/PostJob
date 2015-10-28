package com.android.diplomado.postjob;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_new_job);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_read);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        name = (EditText)findViewById(R.id.name_post);
        description = (EditText)findViewById(R.id.description_post);
        contact = (EditText)findViewById(R.id.contact_post);

        View logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });

    }

    static View getToolbarLogoIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    public void onPost(View view) {
        namePost = name.getText().toString();
        descriptionPost = description.getText().toString();
        contactPost = contact.getText().toString();
        SetDataAsyncTask asyncTask = new SetDataAsyncTask();
        asyncTask.execute();
    }

    private class SetDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
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
                contactsArray.put(contactPost);
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


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(printout != null){
                    try {
                        int response = urlConnection.getResponseCode();
                        printout.close ();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }
    }
}
