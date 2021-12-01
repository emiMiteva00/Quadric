package com.example.projecte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RESTactivity extends AppCompatActivity {
    protected  String urlAddress = "http://gp.gpashev.com:93/testTels/service.php";


    public String getPostDataString(HashMap<String, String> params) throws Exception{
        StringBuilder feedBack = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String,String> entry: params.entrySet()){
            if(first)
                first = false;
            else
                feedBack.append("&");
            feedBack.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            feedBack.append("=");
            feedBack.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return  feedBack.toString();
    }

    public  String postData(String methodName, String userName, String fileJSON) throws  Exception{
        String result = "";
        HashMap<String ,String> params = new HashMap<String ,String >();
        params.put("methodName" , methodName);
        params.put("userName" , userName);
        params.put("fileJSON" , fileJSON);
        URL url = new URL(urlAddress);
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        client.setRequestMethod("POST");
        client.setRequestProperty("multipart/from-data", urlAddress + ";charset = UTF-8");
        client.setDoInput(true);
        client.setDoOutput(true);

        OutputStream os = client.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8")
        );
        String a = getPostDataString(params);
        writer.write(a);
        writer.close();
        os.close();
        int ResponseCode = client.getResponseCode();
        if (ResponseCode == HttpURLConnection.HTTP_OK){
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            String line = "";
            while ((line = br.readLine())!=null){
                result += line+"\n";
            }
            br.close();
        }else{
            throw  new Exception("HTTP ERROR Response Code: " + ResponseCode);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restactivity);
    }
}