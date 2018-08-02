package com.standards.libhikvision.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.standards.libhikvision.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */
public class PreviewImageAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;
    private List<String> mImageUrls;
    private OnPhotoItemClickListener mOnPhotoItemClickListener;

    public PreviewImageAdapter(Context context) {
        this.context = context;
        views = new ArrayList<>();
        mImageUrls = new ArrayList<>();
    }

    public void updateData(List<String> imageUrls) {
        this.mImageUrls = imageUrls;
        views = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    public void setOnPhotoItemClickListener(OnPhotoItemClickListener onPhotoItemClickListener) {
        mOnPhotoItemClickListener = onPhotoItemClickListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_viewpage_image,container,false);
        PhotoView ivLargeImage =  contentView.findViewById(R.id.ivLargeImage);
        if (mOnPhotoItemClickListener != null) {
            ivLargeImage.setOnViewTapListener((view, x, y) -> mOnPhotoItemClickListener.onPhotoItemClick());
        }
        if (mImageUrls != null && mImageUrls.get(position) != null) {
            Glide.with(context).load(mImageUrls.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivLargeImage);
        }
        views.add(contentView);
        container.addView(contentView);
        return contentView;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public interface OnPhotoItemClickListener {
        void onPhotoItemClick();
    }
}
