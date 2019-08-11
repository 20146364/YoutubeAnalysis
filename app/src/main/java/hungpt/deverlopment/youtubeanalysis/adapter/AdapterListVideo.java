package hungpt.deverlopment.youtubeanalysis.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.obj.DetailVideo;
import hungpt.deverlopment.youtubeanalysis.utils.ConvertDataYoutube;

public class AdapterListVideo extends RecyclerView.Adapter<AdapterListVideo.ViewHolder> {

    private ArrayList<DetailVideo> arrayList;
    private onItemClick listener;
    private ConvertDataYoutube utils = new ConvertDataYoutube();

    public AdapterListVideo(ArrayList<DetailVideo> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_video, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DetailVideo detailVideo = arrayList.get(i);
        viewHolder.itemVideoTitleView.setText(detailVideo.getTitle());
        viewHolder.itemUploaderView.setText(detailVideo.getChannelTitle());
        viewHolder.itemDurationView.setText(utils.convertTime(detailVideo.getDuration()));
        String x = utils.convertNumber(detailVideo.getViewCount()) + " lượt xem - " + utils.getTimeUpload(detailVideo.getPublishedAt());
        viewHolder.countView.setText(x);
        viewHolder.itemRoot.setOnClickListener(v -> listener.onClick(i));
        Picasso.get().load(detailVideo.getThumbnails()).into(viewHolder.itemThumbnailView);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemThumbnailView;
        public TextView itemVideoTitleView;
        public TextView itemUploaderView;
        public TextView countView;
        public TextView itemDurationView;
        public RelativeLayout itemRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            itemThumbnailView = itemView.findViewById(R.id.itemThumbnailView);
            itemVideoTitleView = itemView.findViewById(R.id.itemVideoTitleView);
            itemUploaderView = itemView.findViewById(R.id.itemUploaderView);
            countView = itemView.findViewById(R.id.countView);
            itemDurationView = itemView.findViewById(R.id.itemDurationView);
            itemRoot = itemView.findViewById(R.id.itemRoot);
        }
    }

    public void setListenerClick(onItemClick listener) {
        this.listener = listener;
    }

    public interface onItemClick {
        void onClick(int position);
    }
}
