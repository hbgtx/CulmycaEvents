package developers.elementsculmyca.com.elementsculmyca;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    EditText usernamInput,passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernamInput=(EditText)findViewById(R.id.username_input);
        passwordInput=(EditText)findViewById(R.id.password_input);
        loginBtn=(Button)findViewById(R.id.login_btn);
        View.OnClickListener loginOnclick=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        final TextView mananUrl = (TextView) findViewById(R.id.manan_);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        usernamInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    passwordInput.requestFocus();

                }
                return true;
            }
        });
        passwordInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String username=usernamInput.getText().toString();
                    String password=passwordInput.getText().toString();

                    if (UtilMethods.isNetConnected(LoginActivity.this)) {
                        makingcall(username, password);
                    }
                    else{
                        UtilMethods.makeAlert("Not Connected","Please Connect to Internet and try again!",LoginActivity.this);
                    }
                }

                return true;
            }
        });

        mananUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.MANAN_LINK));
                startActivity(intent);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernamInput.getText().toString();
                String password=passwordInput.getText().toString();

                if (UtilMethods.isNetConnected(LoginActivity.this)) {
                    makingcall(username, password);
                }
                else{
                    UtilMethods.makeAlert("Not Connected","Please Connect to Internet and try again!",LoginActivity.this);
                }

            }
        });
    }
    public void makingcall(final String username, final String password){
        final ProgressDialog progress;
        progress = new ProgressDialog(LoginActivity.this);
        progress.setTitle("");
        progress.setMessage("Logging you in!");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        /***********************************/
        String ur="https://elementsculmyca2017.herokuapp.com/api/v1/getAccessToken";
        RequestBody requestBody=new FormEncodingBuilder()
                .add("username",username)
                .add("password",password)
                .build();
        Request request=new Request.Builder()
                .post(requestBody)
                .url(ur)
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }
            @Override
            public void onResponse(Response response) throws IOException {
                //{"token":"a6ee242d725498e288854f589fa3391b41dfce6e"}
                final String res = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(res);

                            String s = jsonObject.names().get(0).toString();
                            if (s.equals("token")) {
                                SharedPrefUtil sharedPrefUtil=new SharedPrefUtil(LoginActivity.this);
                                sharedPrefUtil.savedata(username,password,jsonObject.getString(s));

                                Toast.makeText(LoginActivity.this,jsonObject.getString(s),Toast.LENGTH_SHORT);
                                progress.cancel();
                                Intent i=new Intent(LoginActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                Log.d("value:", jsonObject.getString(s));
                            }
                            else{
                                progress.cancel();
                                UtilMethods.makeAlert("","Username/Password is incorrect",LoginActivity.this);
                                Log.d("Error:",jsonObject.getString(s));
                            }
                        }catch(JSONException e){

                        }
                    }
                });
                Log.d("Json:",res)
                ;
            }
        });
    }



}


