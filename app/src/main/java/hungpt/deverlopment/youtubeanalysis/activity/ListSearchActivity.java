package hungpt.deverlopment.youtubeanalysis.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.adapter.AdapterListVideo;
import hungpt.deverlopment.youtubeanalysis.commons.DeveloperKey;
import hungpt.deverlopment.youtubeanalysis.network.HttpGetDataAsyntask;
import hungpt.deverlopment.youtubeanalysis.obj.DetailVideo;

public class ListSearchActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ImageView img_nointernet;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<DetailVideo> arrayList;
    private RecyclerView recyclerView;
    private AdapterListVideo adapter;
    private LinearLayoutManager layoutManager;
    private String key;
    private DeveloperKey developerKey;
    private List<String> stringList;
    private int index;
    private String listID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_search);
        initView();
        setView();
        try {
            Bundle bundle = getIntent().getExtras();
            key = bundle.getString("KEY");

        } catch (Exception e) {
            key = "";
        }


        refreshLayout.setOnRefreshListener(() -> {
            getData();
        });


    }

    private void getData() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {
                refreshLayout.setRefreshing(true);
                arrayList = new ArrayList<>();
                stringList = new ArrayList<>();
                index = 0;
            }

            @Override
            public void failed() {
                setList();
                img_nointernet.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void success(String result) throws JSONException {
                Log.d("////////////", result);

                JSONObject root = new JSONObject(result);
                JSONArray items = root.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject object = items.getJSONObject(i);
                    JSONObject objectID = object.getJSONObject("id");
                    String id = objectID.getString("videoId");
                    if (listID == null) {
                        listID = id;
                    } else {
                        listID = listID + "," + id;
                    }
                }

                setData();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, developerKey.listSearchByID(key));


    }

    private void setData() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {
            }

            @Override
            public void failed() {
                setList();
                recyclerView.setVisibility(View.GONE);
                img_nointernet.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void success(String result) throws JSONException {
                if (index == stringList.size() - 1) {
                    img_nointernet.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                }

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

                    setList();
                }

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, developerKey.detailVideoByID(listID));

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        img_nointernet = findViewById(R.id.img_nointernet);
        refreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = findViewById(R.id.recyclerView);
        developerKey = new DeveloperKey();
    }


    private void setView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        getData();
    }

    private void setList() {
        layoutManager = new LinearLayoutManager(ListSearchActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter = new AdapterListVideo(arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListenerClick(position -> {
            Intent intent = new Intent(ListSearchActivity.this, MainActivity.class);
            intent.putExtra("channelId", arrayList.get(position).getChannelId());
            intent.putExtra("ID", arrayList.get(position).getId());
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(ListSearchActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
