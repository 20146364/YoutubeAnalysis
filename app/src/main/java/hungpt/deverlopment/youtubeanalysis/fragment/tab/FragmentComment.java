package hungpt.deverlopment.youtubeanalysis.fragment.tab;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.adapter.AdapterListComent;
import hungpt.deverlopment.youtubeanalysis.commons.DeveloperKey;
import hungpt.deverlopment.youtubeanalysis.network.HttpGetDataAsyntask;
import hungpt.deverlopment.youtubeanalysis.obj.Comments;

public class FragmentComment extends Fragment {
    private View view;
    private RecyclerView items_list;
    private LinearLayoutManager layoutManager;
    private AdapterListComent adapter;
    private ProgressBar loading_progress_bar;
    private LinearLayout error_panel;
    private DeveloperKey developerKey;
    private ArrayList<Comments> arrayList;
    private String ID;
    private int number;
    private LinearLayout empty_state_view;
    private Gson gson;
    private String nextPageToken = null;
    boolean isLoading = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        setView(view);
        if (getArguments() != null) {
            ID = getArguments().getString("ID");
            getDataComment();
            initScrollListener();

        }
        return view;
    }

    private void setView(View view) {
        items_list = view.findViewById(R.id.items_list);
        loading_progress_bar = view.findViewById(R.id.loading_progress_bar);
        error_panel = view.findViewById(R.id.error_panel);
        items_list = view.findViewById(R.id.items_list);
        items_list = view.findViewById(R.id.items_list);
        empty_state_view = view.findViewById(R.id.empty_state_view);
        developerKey = new DeveloperKey();
        gson = new Gson();
    }

    private void getDataComment() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {
                loading_progress_bar.setVisibility(View.VISIBLE);
                error_panel.setVisibility(View.GONE);
                if (nextPageToken == null) {
                    arrayList = new ArrayList<>();
                }

            }

            @Override
            public void failed() {
                loading_progress_bar.setVisibility(View.GONE);
                error_panel.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(String result) throws JSONException {
                JSONObject root = new JSONObject(result);
                try {
                    nextPageToken = root.getString("nextPageToken");
                } catch (Exception e) {
                    nextPageToken = "";
                }

                JSONArray items = root.getJSONArray("items");
                if (items.length() > 0) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject object = items.getJSONObject(i);

                        JSONObject obj = object.getJSONObject("snippet");
                        JSONObject topLevelComment = obj.getJSONObject("topLevelComment");
                        JSONObject snippet = topLevelComment.getJSONObject("snippet");
                        String authorDisplayName = snippet.getString("authorDisplayName");
                        String authorProfileImageUrl = snippet.getString("authorProfileImageUrl");
                        String textDisplay = snippet.getString("textDisplay");
                        String likeCount = snippet.getString("likeCount");
                        String publishedAt = snippet.getString("publishedAt");
                        arrayList.add(new Comments(authorDisplayName, authorProfileImageUrl, textDisplay, likeCount, publishedAt));
                    }
                    setList();
                } else {
                    empty_state_view.setVisibility(View.VISIBLE);
                }

                isLoading = false;
                loading_progress_bar.setVisibility(View.GONE);

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, developerKey.getListCommentByID(ID, nextPageToken));
    }

    private void setList() {
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new AdapterListComent(arrayList);
        items_list.setLayoutManager(layoutManager);
        items_list.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        FragmentAnalysis analysis = new FragmentAnalysis();
        Bundle bundle = new Bundle();
        String dataComment = gson.toJson(arrayList);
        bundle.putString("arrayList", dataComment);
        analysis.setArguments(bundle);


    }


    private void initScrollListener() {
        items_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == arrayList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });

    }


    private void loadMore() {
        arrayList.add(null);
        adapter.notifyItemInserted(arrayList.size() - 1);
        getDataComment();
    }

}
