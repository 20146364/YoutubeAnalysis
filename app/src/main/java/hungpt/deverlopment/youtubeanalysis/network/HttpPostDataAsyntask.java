package hungpt.deverlopment.youtubeanalysis.network;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpPostDataAsyntask extends AsyncTask<String, String, String> {

    public interface OnResponse {

        void failed();

        void success(String result);
    }

    private OkHttpClient client;
    private OnResponse onResponse;
    String data;

    public HttpPostDataAsyntask(String data, OnResponse onResponse) {
        this.onResponse = onResponse;
        this.data = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .build();
    }

    @Override
    protected String doInBackground(String... params) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", data)
                .build();
        Request request = new Request.Builder()
                .url(params[0])
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null && !s.isEmpty()) {
            if (onResponse != null) {
                onResponse.success(s);
            }

        } else {
            if (onResponse != null) {
                onResponse.failed();
            }
        }

    }
}

