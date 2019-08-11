package hungpt.deverlopment.youtubeanalysis.network;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpGetDataAsyntask extends AsyncTask<String, String, String> {

    public interface OnResponse {
        void start();

        void failed();

        void success(String result) throws JSONException;
    }

    private OkHttpClient client = new OkHttpClient();
    private OnResponse onResponse;

    public HttpGetDataAsyntask(OnResponse onResponse) {
        this.onResponse = onResponse;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (onResponse != null) {
            onResponse.start();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        Request.Builder builder = new Request.Builder();
        builder.url(params[0]);
        Request request = builder.build();
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
                try {
                    onResponse.success(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            if (onResponse != null) {
                onResponse.failed();
            }
        }

    }


}


