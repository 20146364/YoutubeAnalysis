package hungpt.deverlopment.youtubeanalysis.activity;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.fragment.FragmentHome;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String ID,channelId, commentCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setData();
        initView();
        setView();
        initFragment();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
    }

    private void setView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setData(){
        Bundle bundle = getIntent().getExtras();
        ID = bundle.getString("ID");
        channelId = bundle.getString("channelId");
        commentCount = bundle.getString("Comment");
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FragmentHome fragment = new FragmentHome();
        Bundle bundle = new Bundle();
        bundle.putString("channelId", channelId);
        bundle.putString("ID", ID);
        bundle.putString("Comment", commentCount);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_home, fragment);
        fragmentTransaction.commit();
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
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
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



}
