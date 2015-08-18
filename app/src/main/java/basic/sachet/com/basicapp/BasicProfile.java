package basic.sachet.com.basicapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import basic.sachet.com.beans.User;
import basic.sachet.com.sqlite.StoreUserDetails;


public class BasicProfile extends Activity {

    Button saveProfileButton;
    Button addMoreButton;
    EditText nameEditText;

    EditText ageEditText;

    private int lastItemInsertedId;
    private RelativeLayout main;
    private int id = 0;
    private HashMap<EditText,EditText> institutesEditText = new HashMap<EditText,EditText>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Test", "Inside onCreate method");
        super.onCreate(savedInstanceState);
        Log.d("Test", "After super");
        setContentView(R.layout.activity_basic_profile);
        Log.d("Test", "After setContent");
        main = (RelativeLayout)findViewById(R.id.relativeLayout);

        setAddMoreButton();
        Log.d("Test", "after setAddMoreButton");
        setSaveProfileButton();
     //   initTextFields();
     //   initInstitutesEditText();
    }

    void initInstitutesEditText()
    {

        EditText editText1 = (EditText)findViewById(R.id.institute);
        editText1.setGravity(Gravity.CENTER_HORIZONTAL);

        EditText editText2 = (EditText)findViewById(R.id.instituteCity);
        editText2.setGravity(Gravity.CENTER_HORIZONTAL);

        lastItemInsertedId = editText2.getId();
        institutesEditText.put(editText1,editText2);
    }

    void setSaveProfileButton()
    {
        saveProfileButton = (Button)findViewById(R.id.saveProfile);
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToast("Save Profile Clicked",Toast.LENGTH_SHORT);
                new RestOperation().execute();
            }
        });
    }

    private class RestOperation extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {

                StoreUserDetails db = new StoreUserDetails(BasicProfile.this);
                Profile profile = Profile.getCurrentProfile();



                User user = new User("User12345",profile.getName(),null,null, profile.getId(), "F");
                Log.d("Insert: ", "Inserting ..");
                db.addUser(user);

                Gson gson = new Gson();
                String json = gson.toJson(user);
                System.out.println(json);


                URL url = new URL("http://192.168.0.102:8080/notificationapp/service/create/user");
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

            /*
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://localhost:8080/AndroidApp/api/login");
            try {
                System.out.print(httpGet.toString());
                Log.d("test", httpGet.getURI().toString());
                HttpResponse response = httpClient.execute()


                        (httpGet, localContext);

                HttpEntity entity = response.getEntity();

                System.out.print(entity.toString());


            } catch (Exception e) {
                Log.d("myerror", e.getMessage());
            }
            */
            return null;
        }
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

    void initTextFields()
    {
        Profile profile = Profile.getCurrentProfile();
        if(profile == null)
            System.out.println("facebook profile is null");
        nameEditText = (EditText)findViewById(R.id.editName);
        nameEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        nameEditText.setText(profile.getName());
        if(main == null)
        {
            nameEditText.setText("Main is null");
        }

        ageEditText = (EditText)findViewById(R.id.editAge);
        ageEditText.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basic_profile, menu);
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

    private void addEditText(int dummy) {
        RelativeLayout editTextLayout = new RelativeLayout(this);
//        editTextLayout.setOrientation(LinearLayout.VERTICAL);
        main.addView(editTextLayout);
        EditText editText1 = new EditText(getBaseContext());

        editTextLayout.addView(editText1);
//        main.addView(editText1);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) editText1.getLayoutParams();
        layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layoutParams.setMargins(0, 200, 0, 0);
        System.out.println("lastItemInsertedId 1 : "+lastItemInsertedId);
        layoutParams.addRule(RelativeLayout.ABOVE,lastItemInsertedId);
        editText1.setHint(R.string.institute+" "+institutesEditText.size());
        editText1.setId(id++);
        lastItemInsertedId = editText1.getId();

        editText1.setGravity(Gravity.CENTER_HORIZONTAL);
        editText1.setLayoutParams(layoutParams);


        EditText editText2 = new EditText(getBaseContext());

        editTextLayout.addView(editText2);
//        main.addView(editText1);
        layoutParams = (RelativeLayout.LayoutParams) editText2.getLayoutParams();

        layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(0, 240, 0, 0);


        editText2.setHint(R.string.instituteCity+" "+institutesEditText.size());
        editText2.setId(id++);
        editText2.setGravity(Gravity.CENTER_HORIZONTAL);
        System.out.println("lastItemInsertedId 2 : "+lastItemInsertedId);
        layoutParams.addRule(RelativeLayout.ABOVE,lastItemInsertedId);
        lastItemInsertedId = editText2.getId();
        editText2.setLayoutParams(layoutParams);
        institutesEditText.put(editText1, editText2);

    }

    private void setLayoutParam(EditText editText, RelativeLayout.LayoutParams layoutParams)
    {

        layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

        //layoutParams.

        layoutParams.setMargins(0, 40, 0, 0);
        layoutParams.addRule(RelativeLayout.BELOW,lastItemInsertedId);
        editText.setLayoutParams(layoutParams);
        lastItemInsertedId = editText.getId();
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
       // RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) main.getLayoutParams();
       /*FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) main.getLayoutParams();

        layoutParams.addRule(RelativeLayout.BELOW,lastItemInsertedId);
*/
        EditText editText1 = getEditText();
        editText1.setHint(getResources().getString(R.string.institute)+" "+institutesEditText.size());



        main.addView(editText1);

        setLayoutParam(editText1,(RelativeLayout.LayoutParams) editText1.getLayoutParams());

        EditText editText2 = getEditText();
        editText2.setHint(getResources().getString(R.string.instituteCity)+" "+institutesEditText.size());

        main.addView(editText2);
        setLayoutParam(editText2,(RelativeLayout.LayoutParams) editText2.getLayoutParams());

        institutesEditText.put(editText1, editText2);
    }




    EditText getEditText()
    {
        EditText editText = new EditText(getBaseContext());

        editText.setId(id++);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        return editText;
    }
    void showToast(String message,int length)
    {
        Toast.makeText(getApplicationContext(), message,
                length).show();
    }
}
