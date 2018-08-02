package com.standards.libhikvision.activity.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.widget.wheel.WheelPicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */
public class DatePickerDialog extends BaseAnimDialog implements View.OnClickListener, WheelPicker.OnItemSelectedListener {
    public static final int TAG_SHOW_DATE_ONLY = 1;
    public static final int TAG_SHOW_TIME_ONLY = 2;
    public static final int TAG_SHOW_BOTH = 3;
    private TextView tvSure;
    private WheelPicker wpYear;
    private WheelPicker wpMonth;
    private WheelPicker wpDay;
    private WheelPicker wpHour;
    private WheelPicker wpMinute;
    private WheelPicker wpSecond;
    private String mCurrentYear;
    private String mCurrentMonth;
    private String mCurrentDay;
    private String mCurrentHour;
    private String mCurrentMinute;
    private String mCurrentSecond;
    private int mMinYear;
    private int mMaxYear;
    private RelativeLayout rlTime;
    private LinearLayout llDate;
    private int showType;

    protected OnDateSelectListener mOnDateSelectListener;

    protected DatePickerDialog(Context context) {
        super(context);
    }

    @Override
    protected View getContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.dialog_date_choose2, null);
    }

    @Override
    protected void initView(View contentView) {
        tvSure = contentView.findViewById(R.id.tvSure);
        TextView tvCancel = contentView.findViewById(R.id.tvCancel);
        wpYear = contentView.findViewById(R.id.wpProvince);
        wpMonth = contentView.findViewById(R.id.wpCity);
        wpDay = contentView.findViewById(R.id.wpArea);
        wpHour = contentView.findViewById(R.id.wpHour);
        wpMinute = contentView.findViewById(R.id.wpMinute);
        wpSecond = contentView.findViewById(R.id.wpSecond);
        rlTime = contentView.findViewById(R.id.rlTime);
        llDate = contentView.findViewById(R.id.llDate);
        setShowType(showType);
        tvSure.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        wpYear.setOnItemSelectedListener(this);
        wpMonth.setOnItemSelectedListener(this);
        wpDay.setOnItemSelectedListener(this);
        wpMinute.setOnItemSelectedListener(this);
        wpHour.setOnItemSelectedListener(this);
        wpSecond.setOnItemSelectedListener(this);
        initData();
    }

    private void initData() {
        List<String> years = new ArrayList<>();
        for (int i = mMinYear; i <= mMaxYear; i++) {
            years.add(i + "");
        }
        wpYear.setData(years);
        wpMonth.setData(getNumberList(1, 12));
        wpDay.setData(generateDays(Integer.valueOf(mCurrentYear), Integer.valueOf(mCurrentMonth)));

        wpHour.setData(getNumberList(0, 23));
        wpMinute.setData(getNumberList(0, 59));
        wpSecond.setData(getNumberList(0, 59));

        wpYear.setSelectedItemData(mCurrentYear);
        wpMonth.setSelectedItemData(mCurrentMonth);
        wpDay.setSelectedItemData(mCurrentDay);

        wpHour.setSelectedItemData(mCurrentHour);
        wpMinute.setSelectedItemData(mCurrentMinute);
        wpSecond.setSelectedItemData(mCurrentSecond);
    }

    @Override
    public void onClick(View view) {
        if(view==tvSure)
        {
            if (mOnDateSelectListener != null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.set(Integer.valueOf(mCurrentYear), Integer.valueOf(mCurrentMonth) - 1,
                        Integer.valueOf(mCurrentDay), Integer.valueOf(mCurrentHour),
                        Integer.valueOf(mCurrentMinute), Integer.valueOf(mCurrentSecond));
                mOnDateSelectListener.date(calendar.getTimeInMillis());
            }
        }
        this.dismiss();
    }

    public void setListener(OnDateSelectListener onDateSelectListener) {
        mOnDateSelectListener = onDateSelectListener;
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        if (picker == wpYear) {
            mCurrentYear = data.toString();
            updateDay(mCurrentYear, mCurrentMonth);
        } else if (picker == wpMonth) {
            mCurrentMonth = Integer.valueOf(data.toString()) + "";
            updateDay(mCurrentYear, mCurrentMonth);
        } else if (picker == wpDay) {
            mCurrentDay = data.toString();
        } else if (picker == wpHour) {
            mCurrentHour = data.toString();
        } else if (picker == wpMinute) {
            mCurrentMinute = data.toString();
        } else if (picker == wpSecond) {
            mCurrentSecond = data.toString();
        }
    }

    private List<String> getNumberList(int start, int size) {
        List<String> months = new ArrayList<>();
        for (int i = start; i <= size; i++) {
            if (i < 10) {
                months.add("0" + i);
            } else {
                months.add(i + "");
            }
        }
        return months;
    }

    private void updateDay(String year, String month) {
        wpDay.setData(generateDays(Integer.valueOf(year), Integer.valueOf(month)));
        wpDay.setSelectedItemData(mCurrentDay);
    }

    public interface OnDateSelectListener {
        void date(Long mills);
    }

    public List<String> generateDays(int year, int month) {
        List<String> days = new ArrayList<>();
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                for (int i = 1; i <= 31; i++) {
                    if (i < 10) {
                        days.add("0" + i);
                    } else {
                        days.add(i + "");
                    }
                }
                return days;
            case 4:
            case 6:
            case 9:
            case 11:
                for (int i = 1; i <= 30; i++) {
                    if (i < 10) {
                        days.add("0" + i);
                    } else {
                        days.add(i + "");
                    }
                }
                return days;
            case 2:
                if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                    for (int i = 1; i <= 29; i++) {
                        if (i < 10) {
                            days.add("0" + i);
                        } else {
                            days.add(i + "");
                        }
                    }
                    return days;
                } else {
                    for (int i = 1; i <= 28; i++) {
                        if (i < 10) {
                            days.add("0" + i);
                        } else {
                            days.add(i + "");
                        }
                    }
                    return days;
                }
        }
        return null;
    }

    public void setShowType(int type) {
        this.showType = type;
        if (rlTime == null && llDate == null) {
            return;
        }
        switch (type) {
            case 3:
                rlTime.setVisibility(View.VISIBLE);
                llDate.setVisibility(View.VISIBLE);
                llDate.setBackground(mContext.getResources().getDrawable(R.mipmap.bg_dialog_top_white));
                rlTime.setBackground(mContext.getResources().getDrawable(R.mipmap.bg_dialog_bottom_gray));
                break;
            case 2:
                rlTime.setVisibility(View.VISIBLE);
                llDate.setVisibility(View.GONE);
                rlTime.setBackground(mContext.getResources().getDrawable(R.mipmap.bg_dialog));
                break;
            case 1:
                llDate.setVisibility(View.VISIBLE);
                rlTime.setVisibility(View.GONE);
                llDate.setBackground(mContext.getResources().getDrawable(R.mipmap.bg_dialog));
            default:
                break;
        }
    }

    public static class Builder {

        private Context context;
        private OnDateSelectListener listener;


        private Date initialDate;
        private int minYear = 1996;
        private int maxYear = 2030;
        private int showType = 1;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setListener(OnDateSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setInitialDate(Date initialDate) {
            this.initialDate = initialDate;
            return this;
        }

        public Builder setInitialDate(Long mills) {
            this.initialDate = new Date(mills);
            return this;
        }

        public Builder setMinYear(int minYear) {
            this.minYear = minYear;
            return this;
        }

        public Builder setShowType(int showType) {
            this.showType = showType;
            return this;
        }

        public Builder setMaxYear(int maxYear) {
            this.maxYear = maxYear;
            return this;
        }

        public DatePickerDialog build() {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context);
            datePickerDialog.setListener(listener);
            datePickerDialog.setInitialDate(initialDate);
            datePickerDialog.setMinYear(minYear);
            datePickerDialog.setMaxYear(maxYear);
            datePickerDialog.setShowType(showType);
            return datePickerDialog;
        }
    }

    public void setInitialDate(Long mills) {
        if (mills < 0) {
            mills = System.currentTimeMillis();
        }
        setInitialDate(new Date(mills));
    }

    public void setInitialDate(Date initialDate) {
        if (initialDate == null) {
            initialDate = new Date();
        }
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(initialDate);
        mCurrentYear = calendar.get(java.util.Calendar.YEAR) + "";
        mCurrentMonth = String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1);
        mCurrentDay = String.format("%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH));
        mCurrentHour = String.format("%02d", calendar.get(java.util.Calendar.HOUR_OF_DAY));
        mCurrentMinute = calendar.get(java.util.Calendar.MINUTE) + "";
        mCurrentSecond = calendar.get(java.util.Calendar.SECOND) + "";
        if (wpYear != null) {
            wpYear.setSelectedItemData(mCurrentYear);
        }
        if (wpMonth != null) {
            wpMonth.setSelectedItemData(mCurrentMonth);
        }
        if (wpDay != null) {
            wpDay.setSelectedItemData(mCurrentDay);
        }
        if (wpHour != null) {
            wpHour.setSelectedItemData(mCurrentHour);
        }
        if (wpMinute != null) {
            wpMinute.setSelectedItemData(mCurrentMinute);
        }
        if (wpSecond != null) {
            wpSecond.setSelectedItemData(mCurrentSecond);
        }
    }

    private void setMinYear(int minYear) {
        mMinYear = minYear;
    }

    private void setMaxYear(int maxYear) {
        mMaxYear = maxYear;
    }


}