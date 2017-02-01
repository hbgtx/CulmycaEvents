package developers.elementsculmyca.com.elementsculmyca;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class SplashScreen extends AppCompatActivity {
    SharedPrefUtil sharedPrefUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPrefUtil=new SharedPrefUtil(SplashScreen.this);
        if(sharedPrefUtil.getDetails().getUsername()!=null&&sharedPrefUtil.getDetails().getPassword()!=null&&sharedPrefUtil.getDetails().getAccessToken()!=null){
            Toast.makeText(SplashScreen.this,"Already Logged in",Toast.LENGTH_SHORT);
            if(UtilMethods.isNetConnected(SplashScreen.this)){
                tryLogin();
            }
            else{
                showDialog();
            }


        }
        else{
            Handler handler=new Handler();
            Runnable r= new Runnable() {
                @Override
                public void run() {

                    Intent i=new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(i);
                }
            };
            handler.postDelayed(r,500);

        }



    }
    public  void tryLogin(){
        final String username=sharedPrefUtil.getDetails().getUsername();
        final String password=sharedPrefUtil.getDetails().getPassword();
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
                                SharedPrefUtil sharedPrefUtil=new SharedPrefUtil(SplashScreen.this);
                                sharedPrefUtil.savedata(username,password,jsonObject.getString(s));
                                Intent i=new Intent(SplashScreen.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                Log.d("value:", jsonObject.getString(s));
                            }
                            else{

                                //UtilMethods.makeAlert("Error:","Username/Password is incorrect",SplashScreen.this);
                                Intent i=new Intent(SplashScreen.this,LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
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


    public void showDialog(){

        final AlertDialog alertDialog=new AlertDialog.Builder(SplashScreen.this)
                .setTitle("")
                .setMessage("You are not connected to Internet! Please Connect and try Again!")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(UtilMethods.isNetConnected(SplashScreen.this)){
                            tryLogin();
                        }
                        else{
                            showDialog();
                        }
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();




    }
}
