package hungpt.deverlopment.youtubeanalysis.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.obj.Comments;
import hungpt.deverlopment.youtubeanalysis.utils.ConvertDataYoutube;

public class AdapterListComent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Comments> arrayList;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ConvertDataYoutube utils = new ConvertDataYoutube();

    public AdapterListComent(ArrayList<Comments> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == VIEW_TYPE_ITEM) {
            RelativeLayout view = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_comments_item, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof ViewHolder) {
            showView((ViewHolder) viewHolder, i);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemThumbnailView;
        private TextView itemTitleView;
        private TextView itemCommentContentView;
        private TextView detail_thumbs_up_count_view;
        private TextView itemPublishedTime;
        private RelativeLayout itemRoot;

        private ViewHolder(View itemView) {
            super(itemView);
            itemThumbnailView = itemView.findViewById(R.id.itemThumbnailView);
            itemTitleView = itemView.findViewById(R.id.itemTitleView);
            itemCommentContentView = itemView.findViewById(R.id.itemCommentContentView);
            detail_thumbs_up_count_view = itemView.findViewById(R.id.detail_thumbs_up_count_view);
            itemPublishedTime = itemView.findViewById(R.id.itemPublishedTime);
            itemRoot = itemView.findViewById(R.id.itemRoot);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        private LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    private void showView(ViewHolder viewHolder, int pos) {
        Comments detailVideo = arrayList.get(pos);
        viewHolder.itemTitleView.setText(detailVideo.getAuthorDisplayName());
        viewHolder.itemCommentContentView.setText(detailVideo.getTextDisplay());
        viewHolder.detail_thumbs_up_count_view.setText(utils.format(Long.valueOf(detailVideo.getLikeCount())));
        viewHolder.itemPublishedTime.setText(utils.getTimeUpload(detailVideo.getPublishedAt()));
        Picasso.get().load(detailVideo.getAuthorProfileImageUrl()).into(viewHolder.itemThumbnailView);
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


}
