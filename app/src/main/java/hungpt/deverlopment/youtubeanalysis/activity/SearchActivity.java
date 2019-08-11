package hungpt.deverlopment.youtubeanalysis.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.adapter.AdapterListSearch;
import hungpt.deverlopment.youtubeanalysis.commons.DeveloperKey;
import hungpt.deverlopment.youtubeanalysis.network.HttpGetDataAsyntask;
import hungpt.deverlopment.youtubeanalysis.obj.SearchDetail;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DeveloperKey developerKey;
    private ArrayList<SearchDetail> arrayList;
    private AdapterListSearch adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        setView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        developerKey = new DeveloperKey();

    }

    private void setView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setFocusable(true);
        mSearchView.onActionViewExpanded();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String key;
                if (query == null || query.trim().equals("")) {
                    key = "";
                } else {
                    key = query;
                }
                Intent intent = new Intent(SearchActivity.this, ListSearchActivity.class);
                intent.putExtra("KEY", key);
                startActivity(intent);
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getDataSearch(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void getDataSearch(String key) {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {
                arrayList = new ArrayList<>();
                adapter = new AdapterListSearch(arrayList);
                adapter.isClickable = false;
            }

            @Override
            public void failed() {
                adapter.isClickable = false;
            }

            @Override
            public void success(String result) throws JSONException {
                JSONObject root = new JSONObject(result);
                JSONArray items = root.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject object = items.getJSONObject(i);
                    JSONObject id = object.getJSONObject("id");
                    String videoID = id.getString("videoId");
                    JSONObject snippet = object.getJSONObject("snippet");
                    String title = snippet.getString("title");
                    String channelId = snippet.getString("channelId");
                    arrayList.add(new SearchDetail(videoID, title, channelId));
                }

                setList();

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, developerKey.listSearch(key));
    }

    private void setList() {
        layoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter = new AdapterListSearch(arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.isClickable = true;
        adapter.setListenerClick(position -> {
            if (adapter.isClickable) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("channelId", arrayList.get(position).getChannelId());
                intent.putExtra("ID", arrayList.get(position).getId());
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
