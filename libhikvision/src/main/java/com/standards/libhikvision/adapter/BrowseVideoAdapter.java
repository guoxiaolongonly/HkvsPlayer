package com.standards.libhikvision.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.standards.libhikvision.R;

import java.io.File;
import java.util.List;


/**
 * @author linciping
 * @time 2018/5/29
 * @note
 */
public class BrowseVideoAdapter extends RecyclerView.Adapter<BrowseVideoAdapter.BrowseVideoViewHolder> {
    private Context mContext;

    private List<File> mFiles;
    private View.OnClickListener mOnItemClickListener;
    private int mLimit = 0;


    public BrowseVideoAdapter(Context context, List<File> files) {
        this.mContext = context;
        this.mFiles = files;
    }


    public void refreshData(List<File> files) {
        mFiles.clear();
        mFiles.addAll(files);
        notifyDataSetChanged();
    }


    @Override
    public BrowseVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BrowseVideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_browse_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseVideoAdapter.BrowseVideoViewHolder holder, int position) {
        holder.setData(mFiles.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mLimit == 0 ? mFiles.size() : mLimit;
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    class BrowseVideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVideoLength;

        private TextView tvTime;

        BrowseVideoViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvVideoLength = itemView.findViewById(R.id.tvVideoLength);
        }

        public void setData(File file, int position) {
            tvTime.setText(file.getName().substring(0, file.getName().length() - 4));
//            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//            mmr.setDataSource(file.getPath());
//            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            tvVideoLength.setText("");
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    v.setTag(file);
                    mOnItemClickListener.onClick(v);
                }
            });
        }
    }
}
