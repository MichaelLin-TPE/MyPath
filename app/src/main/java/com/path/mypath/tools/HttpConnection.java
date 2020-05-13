package com.path.mypath.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection {
    public void startConnection(final String apiUrl, final String jsonStr, final OnPostNotificationListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                StringBuilder response = new StringBuilder();

                try {
                    URL url = new URL(apiUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization","key=AAAAVe9RgPk:APA91bHHhJLZcaCRQiIWUDNyH8K5NZ5Wc0JdAlsckwH4HUV701g5YEus0Nfg3edEWPBTub-PYj1hsALoBHwgDiJJGbrmGicE2KkEbAKnYxNVkKFl7nn7MRObPSECCOzwL70NZcmb4nT6");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
                    bw.write(jsonStr);
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
                    listener.onFail(ex.toString());
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }


    public interface OnPostNotificationListener{
        void onSuccessful(String result);
        void onFail(String exception);
    }

}


