package com.standards.libhikvision.adapter;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.standards.libhikvision.R;
import com.standards.libhikvision.util.Constant;
import com.standards.libhikvision.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * @author linciping
 * @time 2018/5/28
 * @note 列表信息 目前其实就一个列表 所以暂时~~
 */
public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexViewHolder> {

    private Activity activity;

    private List<SubResourceNodeBean> mSource;

    private View.OnClickListener onItemCliclListener;

    public void setOnItemCliclListener(View.OnClickListener onItemCliclListener) {
        this.onItemCliclListener = onItemCliclListener;
    }

    public IndexAdapter(Activity activity, List<SubResourceNodeBean> subResourceNodeBeans) {
        this.activity = activity;
        this.mSource = subResourceNodeBeans;
    }

    @NonNull
    @Override
    public IndexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IndexViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_index, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IndexViewHolder holder, int position) {
        holder.setData(mSource.get(position), position);
    }


    @Override
    public int getItemCount() {
        return mSource.size();
    }

    class IndexViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivContent;
        private TextView tvAreaName;

        public IndexViewHolder(View itemView) {
            super(itemView);
            ivContent = itemView.findViewById(R.id.ivContent);
            tvAreaName = itemView.findViewById(R.id.tvAreaName);
        }

        public void setData(SubResourceNodeBean subResourceNodeBean, int position) {
            tvAreaName.setText(subResourceNodeBean.getName());
            File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "stdCache" + File.separator + subResourceNodeBean.getName() + "/index/" + Constant.SCREEN_DIR);
            if (fileDir.exists() && fileDir.length() > 0) {
                Glide.with(ivContent.getContext()).load(fileDir).into(ivContent);
            } else {
                Glide.with(ivContent.getContext()).load(R.drawable.ic_index).into(ivContent);
            }
            itemView.setOnClickListener(v -> {
                if (onItemCliclListener != null) {
                    v.setTag(subResourceNodeBean);
                    onItemCliclListener.onClick(v);
                }
            });
        }
    }
}
