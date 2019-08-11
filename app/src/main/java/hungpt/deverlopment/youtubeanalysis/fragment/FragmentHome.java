package hungpt.deverlopment.youtubeanalysis.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.commons.DeveloperKey;
import hungpt.deverlopment.youtubeanalysis.fragment.adapter.TabAdaptor;
import hungpt.deverlopment.youtubeanalysis.fragment.tab.FragmentAnalysis;
import hungpt.deverlopment.youtubeanalysis.fragment.tab.FragmentComment;
import hungpt.deverlopment.youtubeanalysis.fragment.tab.FragmentRelatedVideo;
import hungpt.deverlopment.youtubeanalysis.network.HttpGetDataAsyntask;
import hungpt.deverlopment.youtubeanalysis.utils.ConvertDataYoutube;

public class FragmentHome extends Fragment implements View.OnClickListener{
    private View view;
    private ImageView detail_thumbnail_image_view;
    private ImageView detail_toggle_description_view;
    private TextView detail_duration_view;
    private FrameLayout detail_title_root_layout;
    private TextView detail_video_title_view;
    private ProgressBar loading_progress_bar;
    private LinearLayout detail_content_root_hiding;
    private CircleImageView detail_uploader_thumbnail_view;
    private TextView detail_uploader_text_view;
    private TextView detail_view_count_view;
    private TextView detail_thumbs_up_count_view;
    private TextView detail_thumbs_down_count_view;
    private LinearLayout detail_description_root_layout;
    private TextView detail_upload_date_view;
    private TextView detail_description_view;
    private ViewPager viewpager;
    private TabLayout tablayout;
    private LinearLayout error_panel;
    private TextView detail_video_title_view_all;

    private String ID, channelId;
    private DeveloperKey developerKey;
    private String thumbnails, title, likeCount, viewCount, dislikeCount, publishedThumbnails;
    private String channelTitle, duration, description, publishedAt, commentCount;
    private ConvertDataYoutube utils;

    private boolean all = false;

    private TabAdaptor pageAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        getDatBundle();
        onClick();
        return view;
    }

    private void initView(View view) {
        detail_thumbnail_image_view = view.findViewById(R.id.detail_thumbnail_image_view);
        detail_duration_view = view.findViewById(R.id.detail_duration_view);
        detail_title_root_layout = view.findViewById(R.id.detail_title_root_layout);
        detail_video_title_view = view.findViewById(R.id.detail_video_title_view);
        loading_progress_bar = view.findViewById(R.id.loading_progress_bar);
        detail_content_root_hiding = view.findViewById(R.id.detail_content_root_hiding);
        detail_uploader_text_view = view.findViewById(R.id.detail_uploader_text_view);
        detail_uploader_thumbnail_view = view.findViewById(R.id.detail_uploader_thumbnail_view);
        detail_view_count_view = view.findViewById(R.id.detail_view_count_view);

        detail_thumbs_up_count_view = view.findViewById(R.id.detail_thumbs_up_count_view);
        detail_thumbs_down_count_view = view.findViewById(R.id.detail_thumbs_down_count_view);
        detail_description_root_layout = view.findViewById(R.id.detail_description_root_layout);
        detail_upload_date_view = view.findViewById(R.id.detail_upload_date_view);
        detail_description_view = view.findViewById(R.id.detail_description_view);
        viewpager = view.findViewById(R.id.viewpager);
        tablayout = view.findViewById(R.id.tablayout);
        error_panel = view.findViewById(R.id.error_panel);
        detail_toggle_description_view = view.findViewById(R.id.detail_toggle_description_view);
        detail_video_title_view_all = view.findViewById(R.id.detail_video_title_view_all);



        developerKey = new DeveloperKey();
        utils = new ConvertDataYoutube();


    }

    private void getDatBundle() {
        if (getArguments() != null) {
            ID = getArguments().getString("ID");
            channelId = getArguments().getString("channelId");


            getData();

            pageAdapter = new TabAdaptor(getChildFragmentManager());
            viewpager.setAdapter(pageAdapter);
            viewpager.setOffscreenPageLimit(2);
            tablayout.setupWithViewPager(viewpager);

        }
    }

    private void initTabs() {
        pageAdapter.clearAllItems();
        pageAdapter.addFragment(new FragmentComment(), ID, Integer.parseInt(commentCount));


        pageAdapter.addFragment(new FragmentRelatedVideo(), ID, Integer.parseInt(commentCount));


        pageAdapter.addFragment(new FragmentAnalysis(), ID, Integer.parseInt(commentCount));


        pageAdapter.notifyDataSetUpdate();

    }

    private void getData() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {
                loading_progress_bar.setVisibility(View.VISIBLE);
                error_panel.setVisibility(View.GONE);
            }

            @Override
            public void failed() {
                loading_progress_bar.setVisibility(View.GONE);
                error_panel.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(String result) throws JSONException {
                JSONObject root = new JSONObject(result);
                JSONArray items = root.getJSONArray("items");
                JSONObject object = items.getJSONObject(0);

                JSONObject snippet = object.getJSONObject("snippet");
                publishedAt = snippet.getString("publishedAt");
                title = snippet.getString("title");
                description = snippet.getString("description");

                JSONObject thumb = snippet.getJSONObject("thumbnails");
                JSONObject standard;
                try {
                    standard = thumb.getJSONObject("standard");
                } catch (Exception e) {
                    standard = thumb.getJSONObject("default");
                }

                thumbnails = standard.getString("url");

                channelTitle = snippet.getString("channelTitle");

                JSONObject contentDetails = object.getJSONObject("contentDetails");
                duration = contentDetails.getString("duration");

                JSONObject statistics = object.getJSONObject("statistics");

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



                Picasso.get().load(thumbnails).into(detail_thumbnail_image_view);
                detail_video_title_view.setText(title);
                detail_video_title_view_all.setText(title);
                detail_uploader_text_view.setText(channelTitle);
                detail_view_count_view.setText(utils.formatNumber(viewCount));
                detail_thumbs_up_count_view.setText(utils.format(Long.valueOf(likeCount)));
                detail_thumbs_down_count_view.setText(utils.format(Long.valueOf(dislikeCount)));
                detail_upload_date_view.setText("Published on " + utils.convertDate(publishedAt));
                detail_description_view.setText(description);
                detail_duration_view.setText(utils.convertTime(duration));
                getDataChannel();

                initTabs();


            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, developerKey.detailVideoByID(ID));
    }

    private void getDataChannel() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {

            }

            @Override
            public void failed() {
                loading_progress_bar.setVisibility(View.GONE);
                error_panel.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(String result) throws JSONException {
                JSONObject root = new JSONObject(result);
                JSONArray items = root.getJSONArray("items");
                JSONObject object = items.getJSONObject(0);
                JSONObject snippet = object.getJSONObject("snippet");
                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                JSONObject defaults = thumbnails.getJSONObject("default");
                publishedThumbnails = defaults.getString("url");

                Picasso.get().load(publishedThumbnails).into(detail_uploader_thumbnail_view);
                detail_content_root_hiding.setVisibility(View.VISIBLE);
                loading_progress_bar.setVisibility(View.GONE);

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, developerKey.getDataChannel(channelId));
    }



    private void onClick() {
        detail_title_root_layout.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_title_root_layout:
                if (all) {
                    detail_video_title_view_all.setVisibility(View.GONE);
                    detail_video_title_view.setVisibility(View.VISIBLE);
                    detail_description_root_layout.setVisibility(View.GONE);
                    detail_toggle_description_view.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    all = false;
                } else {
                    detail_video_title_view_all.setVisibility(View.VISIBLE);
                    detail_video_title_view.setVisibility(View.GONE);
                    detail_description_root_layout.setVisibility(View.VISIBLE);
                    detail_toggle_description_view.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                    all = true;
                }
                break;
        }
    }


}
