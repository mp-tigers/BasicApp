package basic.sachet.com.basicapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

//import java.security.Signature;
//import com.facebook.FacebookSdk

public class LoginPage extends Activity {

    Button facebookBtn;
    Button googleBtn;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        printKeyHash(this);
        setLoginCallback();
        setContentView(R.layout.activity_login_page);

        facebookBtn = (Button)findViewById(R.id.facebook_button);
        googleBtn = (Button)(findViewById(R.id.google_button));
        setFacebookBtn();
        setGoogleBtn();
    }

    void setGoogleBtn()
    {
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Google button clicked",Toast.LENGTH_SHORT);
            }
        });
    }

    void setFacebookBtn()
    {
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Facebook button clicked",Toast.LENGTH_SHORT);
                loginWithReadPermissions();
            }
        });
    }

    private void loginWithReadPermissions()
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    void showToast(String message,int length)
    {
        Toast.makeText(getApplicationContext(), message,
                length).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_page, menu);
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

    private void setLoginCallback()
    {

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        onLoginSuccessful();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        showToast("Login Cancel ",Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        showToast("Login Exception ",Toast.LENGTH_SHORT);
                        exception.printStackTrace();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public static void debug(Object object){
        System.out.println(object);
    }

    private void onLoginSuccessful()
    {

//        Profile profile = Profile.getCurrentProfile();
//        showToast("Login Successful "+profile.getName(),Toast.LENGTH_SHORT);
        Intent intent = new Intent(this,profile.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
