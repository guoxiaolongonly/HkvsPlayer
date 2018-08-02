package com.standards.libhikvision.activity.widget;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.standards.libhikvision.R;
import com.standards.libhikvision.adapter.PreviewImageAdapter;

import java.util.List;

/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */

public class ImagePreViewDialog extends AlertDialog {
    private ViewPager viewpager;
    private List<String> mUris;
    private int mSelectPosition;
    private PreviewImageAdapter previewImageAdapter;
    private TextView tvPagePosition;

    public ImagePreViewDialog(FragmentActivity context, List<String> uris, int position) {
        super(context, R.style.full_dialog);
        this.mUris = uris;
        mSelectPosition = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_preview);
        init();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    private void init() {
        tvPagePosition = findViewById(R.id.tvPagePosition);
        tvPagePosition.setText((mSelectPosition+1) + "/" + mUris.size());
        viewpager = findViewById(R.id.viewpager);
        initViewPager();
    }

    private void initViewPager() {
        viewpager.setAdapter(previewImageAdapter = new PreviewImageAdapter(getContext()));
        previewImageAdapter.updateData(mUris);
        previewImageAdapter.setOnPhotoItemClickListener(() -> dismiss());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPagePosition.setText((position + 1) + "/" + mUris.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpager.setCurrentItem(mSelectPosition);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return viewpager.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException ignored) {
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return false;
    }
}
