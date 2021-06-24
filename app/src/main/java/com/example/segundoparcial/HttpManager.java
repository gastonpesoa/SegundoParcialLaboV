package com.example.segundoparcial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpManager {

    private String url;
    private HttpURLConnection conn;

    public HttpManager(String url) {
        this.conn = crearHttpUrlConn(url);
    }

    private HttpURLConnection crearHttpUrlConn(String strUrl) {
        URL url = null;
        try {
            url = new URL(strUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            return urlConnection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isReady(){
        return conn != null;
    }

    public byte[] getBytesDataByGET() throws IOException {
        conn.setRequestMethod("GET");
        conn.connect();
        int response = conn.getResponseCode();
        if(response == 200) {
            InputStream is = conn.getInputStream();
            return readFully(is);
        }
        else
            throw new IOException();
    }

    private byte[] readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1){
            baos.write(buffer, 0, length);
        }
        is.close();
        return baos.toByteArray();
    }

    public String getStrDataByGET() throws IOException {
        byte[] bytes = getBytesDataByGET();
        return new String(bytes, "UTF-8");
    }
}
