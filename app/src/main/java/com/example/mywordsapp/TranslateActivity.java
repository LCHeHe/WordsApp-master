package com.example.mywordsapp;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslateActivity extends AppCompatActivity {
    private String transContent;
    //有道key
    private String apiUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=ghyghyghy&key=1853216072&type=data&doctype=json&version=1.1&q=";
    private EditText editText;
    private Button button;
    private TextView textView;
    private String tvMsg=null;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what==0){
                String responses =(String) msg.obj;
                textView.setText(responses);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        editText=(EditText)findViewById(R.id.id_EditText);
        button=(Button)findViewById(R.id.id_transButton);
        textView=(TextView)findViewById(R.id.id_TextView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.id_transButton){
                    sendHttpURLConnection();
                }
            }
        });
    }
    private void sendHttpURLConnection() {
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                HttpURLConnection connection = null;
                transContent=editText.getText().toString();
                if(transContent.equals("")||transContent==null)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TranslateActivity.this,"输入为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                try {
                    //加上要翻译的词
                    URL url = new URL(apiUrl+ URLEncoder.encode(transContent,"utf8"));

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
                    StringBuilder response = new StringBuilder();
                    String line=null;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    JSONObject transJSON = new JSONObject(response.toString());

                    String errorCode = transJSON.getString("errorCode");

                    if(errorCode.equals("0")){
                        String query = transJSON.getString("query");
                        JSONObject basic = transJSON.getJSONObject("basic");

                        JSONArray explains = basic.getJSONArray("explains");
                        tvMsg="原文："+query;
                        String explainStr="\n\n释意：";
                        for(int j = 1,s=0;s<explains.length();s++,j++){
                            explainStr+="\n"+j+". "+explains.getString(s);
                        }
                        tvMsg+=explainStr;
                    }
                    Message message = new Message();
                    message.what = 0;
                    message.obj=tvMsg.toString();
                    handler.sendMessage(message);
                }
                catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }).start();
    }

}

