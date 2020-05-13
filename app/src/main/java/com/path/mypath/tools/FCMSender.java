package com.path.mypath.tools;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FCMSender extends AsyncTask<String,Void,String> {

    private  OnPostNotificationListener listener;

    private static final String KEY = "ffB0uOlbFBw:APA91bHwZGlCFT7qeJLwSaylWiwJEf6jMCtqrByjTyw0fw7ggcCS6aEWxKVMPW1Twfq18MiEv1EFUyviaAv0OVHDkri1RcSM-C_wd9-HnWhUbso0y5DQ-q1fPgvP_qPHdEnta5xsapAj";

    public void setOnPostNotificationListener(OnPostNotificationListener listener){
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpURLConnection conn = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization","key="+KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            bw.write(strings[0]);
            bw.flush();
            bw.close();
            //Get Response
            InputStream is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            listener.onSuccessful(response.toString());
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            response.append(ex.toString());
            listener.onFail("error"+response.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.startsWith("error")){
            listener.onFail(result);
        }else {
            listener.onSuccessful(result);
        }
    }

    public interface OnPostNotificationListener{
        void onSuccessful(String result);
        void onFail(String exception);
    }
}
