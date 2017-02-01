package developers.elementsculmyca.com.elementsculmyca;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Details extends AppCompatActivity {
    TextView phnNo,emailId,fullName,collegeName,eventId,paymentTxId,paymentPhnno,
            qrCode,time,paymentStatus;
    Button backBtn,arrivedBtn;
    SharedPrefUtil sharedPrefUtil;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        phnNo=(TextView)findViewById(R.id.details_phn_output);
        emailId=(TextView)findViewById(R.id.details_email_output);
        fullName=(TextView)findViewById(R.id.details_full_name_output);
        collegeName=(TextView)findViewById(R.id.details_college_output);
        eventId=(TextView)findViewById(R.id.details_event_id_output);
        paymentTxId=(TextView)findViewById(R.id.details_payment_tx_id_output);
        paymentPhnno=(TextView)findViewById(R.id.details_payment_phn_no_output);
        qrCode=(TextView)findViewById(R.id.details_qr_code_output);
        time=(TextView)findViewById(R.id.details_time_output);
        paymentStatus=(TextView)findViewById(R.id.details_payment_status_output);
        arrivedBtn=(Button)findViewById(R.id.dtails_arrived_btn);
        backBtn=(Button)findViewById(R.id.details_back_btn);
        progressDialog=new ProgressDialog(Details.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Marking as arrived!");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);



        //getting details of person
        Intent intent=getIntent();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        sharedPrefUtil=new SharedPrefUtil(Details.this);

        final TextView mananUrl = (TextView) findViewById(R.id.details_manan_link);
        mananUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.MANAN_LINK));
                startActivity(intent);
            }
        });
        arrivedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(UtilMethods.isNetConnected(Details.this)) {
                    progressDialog.show();
                    makingcall(qrCode.getText().toString());
                }
                else{
                    UtilMethods.makeAlert("","Please Connect to Internet and try again!",Details.this);
                }

            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Details.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        Log.d("Details:",sharedText);
        try{
            JSONObject jsonObject=new JSONObject(sharedText);
            phnNo.setText(jsonObject.getString("phoneno"));
            emailId.setText(jsonObject.getString("email"));
            fullName.setText(jsonObject.getString("fullname"));
            collegeName.setText(jsonObject.getString("college"));
            eventId.setText(jsonObject.getString("eventid"));
            paymentTxId.setText(jsonObject.getString("paymenttxnid"));
            paymentPhnno.setText(jsonObject.getString("paymentphoneno"));
            qrCode.setText(jsonObject.getString("qrcode"));
            time.setText(jsonObject.getString("timestamp"));
            paymentStatus.setText(jsonObject.getString("paymentstatus"));



        }catch (JSONException e){

        }




    }
    public void makingcall(String qrcode){
        String accessCode=sharedPrefUtil.getDetails().getAccessToken();
        String ur="https://elementsculmyca2017.herokuapp.com/api/v1/arrived/"+qrcode+"/"+accessCode;
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
                    String s = jsonObject.names().get(0).toString();
                    final String message=jsonObject.getString(s);
                    if(!s.equals("message")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                final AlertDialog alertDialog=new AlertDialog.Builder(Details.this)
                                        .setTitle("")
                                        .setMessage(message)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i=new Intent(Details.this,MainActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                            }
                                        })
                                        .setCancelable(false)
                                        .create();
                                alertDialog.show();

                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                UtilMethods.makeAlert("",message,Details.this);
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
