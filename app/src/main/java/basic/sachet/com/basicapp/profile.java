package basic.sachet.com.basicapp;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.widget.FrameLayout;
import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import basic.sachet.com.beans.Institute;
import basic.sachet.com.beans.User;
import basic.sachet.com.sqlite.StoreUserDetails;


public class profile extends Activity {
    Button saveProfileButton;
    Button addMoreButton;
    EditText nameEditText;
    int fieldCount = 1;
    LinearLayout textFieldLinearLayout;
    private int id = 0;

    EditText ageEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        textFieldLinearLayout = (LinearLayout)findViewById(R.id.textFieldLinearLayout);
        setAddMoreButton();
        setSaveProfileButton();

    }

    void setAddMoreButton()
    {
        addMoreButton = (Button)findViewById(R.id.addMore);
        addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditText();
//                showToast("Add More Clicked : "+institutesEditText.size(),Toast.LENGTH_SHORT);
            }
        });
    }

    private void addEditText() {
 /*///////// Commented by GIRI
        RelativeLayout editTextLayout = new RelativeLayout(this);
//        editTextLayout.setOrientation(LinearLayout.VERTICAL);
        main.addView(editTextLayout);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) editTextLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW,lastItemInsertedId);
//        lastItemInsertedId = editTextLayout.getId();
*/
        // Added by giri
         //RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) main.getLayoutParams();
      //  LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textFieldLinearLayout.getLayoutParams();

       // layoutParams.addRule(LinearLayout.BELOW, lastItemInsertedId);

        EditText editText1 = getEditText();
        editText1.setHint(getResources().getString(R.string.institute) + " " + fieldCount);



        textFieldLinearLayout.addView(editText1);

       // setLayoutParam(editText1,(RelativeLayout.LayoutParams) editText1.getLayoutParams());

        EditText editText2 = getEditText();
        editText2.setHint(getResources().getString(R.string.instituteCity)+" "+fieldCount);

        textFieldLinearLayout.addView(editText2);
       // setLayoutParam(editText2,(RelativeLayout.LayoutParams) editText2.getLayoutParams());

        fieldCount++;
    }

    void setSaveProfileButton()
    {
        saveProfileButton = (Button)findViewById(R.id.saveProfile);
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToast("Save Profile Clicked",Toast.LENGTH_SHORT);
                StoreUserDetails db = new StoreUserDetails(profile.this);
                Profile profile = Profile.getCurrentProfile();

             //   User user = new User("User12345",profile.getName(),null,null,null, profile.getId(), "F");
                User user = getUserDetailsFromUI(profile);
                Log.d("Insert: ", "Inserting ..user");
                db.addUser(user);
                Log.d("Insert: ", "Inserted ..user");
                new RestOperation().execute(user.getAccountId());

            }
        });
    }
    User getUserDetailsFromUI(Profile profile){
        User user = new User();
        if(profile != null)
            Log.d("profile not null","profile not null");
        EditText editText = (EditText)findViewById(R.id.editName);
        String userName = editText.getText().toString();
        user.setUserName(userName);
        user.setUserId("12345");
        user.setAccountId(profile.getId());
        user.setAccountType("F");

        //editText = (EditText)findViewById(R.id.editAge);
        //String age = editText.getText().toString();
        editText = (EditText)findViewById(R.id.institute);
        String instituteName = editText.getText().toString();
        editText = (EditText)findViewById(R.id.instituteCity);
        String instituteCity = editText.getText().toString();
        Institute institute = new Institute(instituteName , instituteCity);

        ArrayList<Institute> institutes = user.getInstituteList();
        institutes.add(institute);
        editText = (EditText)findViewById(0);
        int i = 1;
        while(editText != null) {
            instituteName = editText.getText().toString();
            editText = (EditText)findViewById(i++);
            instituteCity = editText.getText().toString();
            institute = new Institute(instituteName, instituteCity);
            institutes.add(institute);

            editText = (EditText)findViewById(i++);
        }
        user.setInstituteList(institutes);

        return user;
    }

    private class RestOperation extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {

                StoreUserDetails userSQL = new StoreUserDetails(profile.this);
                User user = userSQL.getUserByAccountId(params[0]);

                Gson gson = new Gson();
                String json = gson.toJson(user);
                System.out.println(json);



                URL url = new URL("http://192.168.0.101:8080/notificationapp/service/create/user");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/JSON");

                conn.setDoOutput(true);
                // conn.setDoInput(true);

                // OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                Log.d("comingmsg", "conn established");

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(json);
                out.flush();
                out.close();

                Log.d("outmsg", "send data");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
                in.close();
                conn.disconnect();
            } catch (Exception e) {
                Log.d("myerror", e.getMessage());
            }
            return null;
        }
    }

    EditText getEditText()
    {
        EditText editText = new EditText(getBaseContext());

        editText.setId(id++);
        //editText.setGravity(Gravity.CENTER_HORIZONTAL);
       // EditText name = (EditText)findViewById(R.id.editName);

        //editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setTextColor(Color.BLACK);

        return editText;
    }
    void showToast(String message,int length)
    {
        Toast.makeText(getApplicationContext(), message,
                length).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
