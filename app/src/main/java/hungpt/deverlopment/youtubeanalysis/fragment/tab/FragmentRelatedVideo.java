package hungpt.deverlopment.youtubeanalysis.fragment.tab;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.activity.MainActivity;
import hungpt.deverlopment.youtubeanalysis.adapter.AdapterListVideo;
import hungpt.deverlopment.youtubeanalysis.commons.DeveloperKey;
import hungpt.deverlopment.youtubeanalysis.network.HttpGetDataAsyntask;
import hungpt.deverlopment.youtubeanalysis.obj.DetailVideo;

public class FragmentRelatedVideo extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private AdapterListVideo adapterListVideo;
    private ArrayList<DetailVideo> arrayList;
    private String ID;
    private DeveloperKey utils;
    private List<String> stringList;
    private int index;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private String listID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_related, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        utils = new DeveloperKey();
        if (getArguments() != null) {
            ID = getArguments().getString("ID");
            getData();
        }

        return view;
    }

    private void getData() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {
                stringList = new ArrayList<>();
                arrayList = new ArrayList<>();
                index = 0;
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void failed() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void success(String result) throws JSONException {
                JSONObject root = new JSONObject(result);
                JSONArray items = root.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject object = items.getJSONObject(i);
                    JSONObject id = object.getJSONObject("id");
                    String videoID = id.getString("videoId");
                    if (listID == null) {
                        listID = videoID;
                    } else {
                        listID = listID + "," + videoID;
                    }
                }
                setData();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, utils.getRelated(ID));
    }

    private void setData() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {

            }

            @Override
            public void failed() {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void success(String result) throws JSONException {
                JSONObject root = new JSONObject(result);
                JSONArray items = root.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject object = items.getJSONObject(i);
                    String id = object.getString("id");

                    JSONObject snippet = object.getJSONObject("snippet");
                    String publishedAt = snippet.getString("publishedAt");
                    String title = snippet.getString("title");
                    String description = snippet.getString("description");

                    JSONObject thumb = snippet.getJSONObject("thumbnails");
                    JSONObject standard;
                    try {
                        standard = thumb.getJSONObject("standard");
                    } catch (Exception e) {
                        standard = thumb.getJSONObject("default");
                    }
                    String thumbnails = standard.getString("url");

                    String channelTitle = snippet.getString("channelTitle");
                    String channelId = snippet.getString("channelId");


                    JSONObject contentDetails = object.getJSONObject("contentDetails");
                    String duration = contentDetails.getString("duration");

                    JSONObject statistics = object.getJSONObject("statistics");

                    String likeCount;
                    String viewCount;
                    String dislikeCount;
                    String commentCount;
                    try {
                        viewCount = statistics.getString("viewCount");
                    } catch (Exception e) {
                        viewCount = "0";
                    }

                    try {
                        likeCount = statistics.getString("likeCount");
                    } catch (Exception e) {
                        likeCount = "0";
                    }

                    try {
                        dislikeCount = statistics.getString("dislikeCount");
                    } catch (Exception e) {
                        dislikeCount = "0";
                    }

                    try {
                        commentCount = statistics.getString("commentCount");
                    } catch (Exception e) {
                        commentCount = "0";
                    }



                    arrayList.add(new DetailVideo(id, publishedAt, title, description, thumbnails, channelTitle, duration, viewCount, likeCount, dislikeCount, commentCount, channelId));
                }
                setList();

                progressBar.setVisibility(View.GONE);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, utils.detailVideoByID(listID));

    }

    private void setList() {
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapterListVideo = new AdapterListVideo(arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterListVideo);
        adapterListVideo.setListenerClick(position -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("channelId", arrayList.get(position).getChannelId());
            intent.putExtra("ID", arrayList.get(position).getId());
            startActivity(intent);
        });
    }

}
