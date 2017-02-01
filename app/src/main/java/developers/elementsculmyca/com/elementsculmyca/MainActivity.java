package developers.elementsculmyca.com.elementsculmyca;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPrefUtil sharedPrefUtil;
    //View Objects

    private Button buttonScan;
    private TextView textViewName, textViewAddress;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.logout_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.main_activity_log_out:
                final AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to Logout")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                sharedPrefUtil.logOut();
                                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        })
                        .create();
                alertDialog.show();
                break;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView mananUrl = (TextView) findViewById(R.id.main_activity_manan_link);
        mananUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.MANAN_LINK));
                startActivity(intent);
            }
        });


        sharedPrefUtil=new SharedPrefUtil(MainActivity.this);
        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {

                String r=result.getContents();
                Toast.makeText(this, r, Toast.LENGTH_LONG).show();
                if(UtilMethods.isNetConnected(MainActivity.this)) {
                    makingcall(r);
                }
                else{
                    UtilMethods.makeAlert("","Check Internet Connection and try again!",MainActivity.this);
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan

        qrScan.initiateScan();
    }
    public void makingcall(String phn){
        String accessCode=sharedPrefUtil.getDetails().getAccessToken();
        String ur="https://elementsculmyca2017.herokuapp.com/api/v1/getQRCodeDetails/"+phn+"/"+accessCode;
        Request request=new Request.Builder()
                .get()
                .url(ur)
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("Failed","fail");
            }
            @Override
            public void onResponse(Response response) throws IOException {

                final String res = response.body().string();
                Log.d("qr:",res);
                try {
                    final JSONObject jsonObject = new JSONObject(res);
                    Log.d("result QRCode",res);
                    String s = jsonObject.names().get(0).toString();
                    final String message=jsonObject.getString(s);

                    if(!s.equals("message")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent i = new Intent(MainActivity.this, Details.class);
                                i.putExtra(Intent.EXTRA_TEXT, res);
                                startActivity(i);

                            }
                        });
                    }
                    else{

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UtilMethods.makeAlert("",message,MainActivity.this);
                            }
                        });

                    }
                }
                catch (JSONException e){

                }
            }
        });
    }


}

