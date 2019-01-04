package com.example.robin.smartplug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.model.ChannelFeed;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ControlView extends PageView implements CompoundButton.OnCheckedChangeListener {

    public View view;
    public Switch switchPower;
    public int switchCount = 0;
    public int TALKBACK_ID = 30134;
    public String API_Key = "VCBK2KBMJLQUJCEM";
    public TextView txt_temp, txt_humi, txt_power;
    public double power = 100;

    public ControlView(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.page_control, null);
        txt_temp = view.findViewById(R.id.txt_temp);
        txt_humi = view.findViewById(R.id.txt_humi);
        txt_power = view.findViewById(R.id.txt_power);
        switchPower = view.findViewById(R.id.switch_power);
//        HttpGetAsyncTask lastCommand = new HttpGetAsyncTask("http://api.thingspeak.com/talkbacks/"+TALKBACK_ID+"/commands/last.json?api_key=VCBK2KBMJLQUJCEM");
//        lastCommand.execute();
        updateRealtimeData();
        switchPower.setOnCheckedChangeListener(this);
        addView(view);
    }

    @Override
    public void refreshView() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.switch_power:
                if(switchCount!=0){
                    if(buttonView.isChecked()){
                        HttpPostAsyncTask execCommand = new HttpPostAsyncTask("https://api.thingspeak.com/talkbacks/"+TALKBACK_ID+"/commands/execute.json");
                        execCommand.execute();
                        HttpPostAsyncTask updateCommand = new HttpPostAsyncTask("https://api.thingspeak.com/talkbacks/"+TALKBACK_ID+"/commands.json", "ON");
                        updateCommand.execute();
                    }
                    else{
                        HttpPostAsyncTask execCommand = new HttpPostAsyncTask("https://api.thingspeak.com/talkbacks/"+TALKBACK_ID+"/commands/execute.json");
                        execCommand.execute();
                        HttpPostAsyncTask updateCommand = new HttpPostAsyncTask("https://api.thingspeak.com/talkbacks/"+TALKBACK_ID+"/commands.json", "OFF");
                        updateCommand.execute();
                    }
                }
                break;

        }
    }

    private void updateRealtimeData(){
        @SuppressLint("HandlerLeak") Handler mTimeHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    RealtimeAsyncTask lastData = new RealtimeAsyncTask("https://api.thingspeak.com/channels/634553/feeds/last.json?api_key=NYO04RPPTYDE58B6");
                    lastData.execute();
                    sendEmptyMessageDelayed(0, 1000);
                }
            }
        };
        mTimeHandler.sendEmptyMessageDelayed(0, 1000);

    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public class RealtimeAsyncTask extends AsyncTask<String, Void, String>
    {
        String url;
        String sb;

        public RealtimeAsyncTask(String url){
            this.url = url;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(this.url);
                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int statusCode = urlConnection.getResponseCode();
                switch (statusCode) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        this.sb = sb.toString();
                        System.out.print("this.sb:"+this.sb);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this.sb;
        }

        protected void onPostExecute(String result)
        {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
            String f1 = String.valueOf(jsonObject.get("field1"));
            String f2 = String.valueOf(jsonObject.get("field2"));
            String f3 = String.valueOf(jsonObject.get("field3"));
            if(!f1.equals("null") && !f2.equals("null") && !f3.equals("null")){
                txt_temp.setText(f1.substring(1,f1.length()-1));
                txt_humi.setText(f2.substring(1,f2.length()-1));
                txt_power.setText(f3.substring(1,f3.length()-1));
                power = Double.parseDouble(f3.substring(1,f3.length()-1));
                if(switchCount<1){
                    if(power>100) {
                        switchPower.setChecked(Boolean.TRUE);
                    }else{
                        switchPower.setChecked(Boolean.FALSE);
                    }
                    switchCount++;
                }

            }
        }
    }

//    public class HttpGetAsyncTask extends AsyncTask<String, Void, String>
//    {
//        String url;
//        String sb;
//
//        public HttpGetAsyncTask(String url){
//            this.url = url;
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            URL url = null;
//            try {
//                url = new URL(this.url);
//                // Create the urlConnection
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//                int statusCode = urlConnection.getResponseCode();
//                switch (statusCode) {
//                    case 200:
//                    case 201:
//                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
//                        StringBuilder sb = new StringBuilder();
//                        String line;
//                        while ((line = br.readLine()) != null) {
//                            sb.append(line + "\n");
//                        }
//                        br.close();
//                        this.sb = sb.toString();
//                        System.out.print("this.sb:"+this.sb);
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return this.sb;
//        }
//        protected void onPostExecute(String result)
//        {
//            JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
//            if(jsonObject.get("command_string").equals("ON")){
//                switchState = Boolean.TRUE;
//            }else{
//                switchState = Boolean.FALSE;
//            }
//        }
//
//    }

    public class HttpPostAsyncTask extends AsyncTask<String, Void, Void>
    {
        JSONObject postData;
        String url;

        public HttpPostAsyncTask(String url){
            this.url = url;
            postData = new JSONObject();
            try {
                postData.put("api_key", API_Key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public HttpPostAsyncTask(String url, String command_string){
            this.url = url;
            if(command_string != null){
                postData = new JSONObject();
                try {
                    postData.put("api_key", API_Key);
                    postData.put("command_string", command_string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL(this.url);
                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);
                    Log.d("response", response);
                    // From here you can convert the string to JSON with whatever JSON parser you like to use

                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
