package com.standards.libhikvision.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.standards.libhikvision.R;

import java.io.File;
import java.util.List;

/**
 * @author linciping
 * @time 2018/5/29
 * @note 预览图
 */
public class BrowsePhotoAdapter extends RecyclerView.Adapter<BrowsePhotoAdapter.BrowsePhotoViewHolder> {

    private Context mContext;

    private List<File> mFiles;
    private View.OnClickListener mOnItemClickListener;
    private int mLimit = -1;


    public BrowsePhotoAdapter(Context context, List<File> files) {
        this.mContext = context;
        this.mFiles = files;
    }

    public BrowsePhotoAdapter(Context context, List<File> files, int limit) {
        this.mContext = context;
        this.mFiles = files;
        mLimit = limit;
    }

    public void refreshData(List<File> files) {
        mFiles.clear();
        mFiles.addAll(files);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrowsePhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (mLimit == -1 ? R.layout.item_browse_photo_span2 : R.layout.item_browse_photo);
        return new BrowsePhotoViewHolder(LayoutInflater.from(mContext).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BrowsePhotoViewHolder holder, int position) {
        holder.setData(mFiles.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mLimit != -1 && mFiles.size() > 3) {
            return mLimit;
        }
        return mFiles.size();

    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    class BrowsePhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;

        private TextView tvTime;

        BrowsePhotoViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        public void setData(File file, int position) {
            Glide.with(mContext)
                    .load(file)
                    .into(ivImage);
            tvTime.setText(file.getName().substring(0, file.getName().length() - 4));
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    v.setTag(file);
                    mOnItemClickListener.onClick(v);
                }
            });
        }
    }
}
