package hungpt.deverlopment.youtubeanalysis.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.obj.SearchDetail;
import hungpt.deverlopment.youtubeanalysis.utils.ConvertDataYoutube;

public class AdapterListSearch extends RecyclerView.Adapter<AdapterListSearch.ViewHolder> {

    private ArrayList<SearchDetail> arrayList;
    private onItemClick listener;
    private ConvertDataYoutube utils = new ConvertDataYoutube();
    public boolean isClickable = true;

    public AdapterListSearch(ArrayList<SearchDetail> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_search, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SearchDetail detailVideo = arrayList.get(i);
        viewHolder.itemVideoTitleView.setText(detailVideo.getTitle());
        viewHolder.itemRoot.setOnClickListener(v -> listener.onClick(i));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemVideoTitleView;
        public LinearLayout itemRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            itemVideoTitleView = itemView.findViewById(R.id.itemVideoTitleView);
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
