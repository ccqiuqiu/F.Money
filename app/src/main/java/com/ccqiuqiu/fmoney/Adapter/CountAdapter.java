package com.ccqiuqiu.fmoney.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.CountFragment;
import com.ccqiuqiu.fmoney.Model.CountVo;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Utils.MathUtils;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.hookedonplay.decoviewlib.DecoView;

import java.text.DecimalFormat;
import java.util.List;

import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by cc on 2016/2/2.
 */
public class CountAdapter extends RecyclerView.Adapter<CountAdapter.MyViewHolder> {
    private List<CountVo> mItems;
    private CountFragment mCountFragment;
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    private boolean mIsShow = true;
    private PopupMenu mPopupMenu;

    public CountAdapter(CountFragment countFragment, List<CountVo> items) {
        mItems = items;
        mCountFragment = countFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == App.TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_count_header, parent, false);
        } else if (viewType == App.TYPE_LINE_CHART) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_count_linechart, parent, false);
        } else if (viewType == App.TYPE_PIE_CHART) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_count_piechart, parent, false);
        } else if (viewType == App.TYPE_COIUMN_CHART) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_count_columnchart, parent, false);
        } else if (viewType == App.TYPE_PIE_CHART_TWO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_count_piechart_2, parent, false);
        } else if (viewType == App.TYPE_BOTTOM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_count_bottom, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CountAdapter.MyViewHolder holder, int position) {
        CountVo countVo = mItems.get(position);
        int viewType = getItemViewType(position);
        if (viewType == App.TYPE_HEADER) {
            String zichan = holder.itemView.getResources().getString(R.string.zongzichan);
            String fuzhai = holder.itemView.getResources().getString(R.string.fuzhai);
            holder.tv_zichan.setText(zichan + "：" + fm.format(countVo.getZichan()));
            holder.tv_fuzhai.setText(fuzhai + "：" + fm.format(countVo.getFuzhai()));
            Double jingzichan = MathUtils.sub(countVo.getZichan(), countVo.getFuzhai());
            mCountFragment.showColumnChart_bj(holder.column_chart, countVo.getPointValues(),
                    ViewUtils.modifyAlpha(App.colorPrimary, 16));
            holder.tv_jingzichan.setDecimalFormat(fm)
                    .setAnimationDuration(1000)
                    .countAnimation(0, jingzichan.floatValue());
            //holder.tv_jingzichan.setText(fm.format(jingzichan));

            if (jingzichan - 0 >= 0) {
                holder.tv_jingzichan.setTextColor(App.colorShouRu);
            } else {
                holder.tv_jingzichan.setTextColor(App.colorZhiChu);
            }

        } else if (viewType == App.TYPE_LINE_CHART) {
            mCountFragment.showLineChart(holder.line_chart, countVo.getPointValues(),
                    App.colorPrimary);
//            holder.img_more.setColorFilter(App.colorPrimary);
//            holder.img_more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mPopupMenu != null) {
//                        mPopupMenu.dismiss();
//                    }
//                    PopupMenu mPopupMenu = new PopupMenu(holder.itemView.getContext(), v);
//                    mPopupMenu.getMenuInflater().inflate(R.menu.chart_more, mPopupMenu.getMenu());
//                    mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            return false;
//                        }
//                    });
//                    mPopupMenu.show();
//                }
//            });
        } else if (viewType == App.TYPE_PIE_CHART) {
            holder.tv_title.setText(countVo.getTitle());
            mCountFragment.showPieChart(holder.pie_chart, countVo.getPointValues(), countVo.getPieTitle1());
        } else if (viewType == App.TYPE_COIUMN_CHART) {
            holder.tv_title.setText(countVo.getTitle());
            mCountFragment.showColumnChart(holder.column_chart, countVo.getPointValues());
        } else if (viewType == App.TYPE_PIE_CHART_TWO) {
            holder.tv_title.setText(countVo.getTitle());
            mCountFragment.showPieChart(holder.pie_chart, countVo.getPointValues(), countVo.getPieTitle1());
            mCountFragment.showPieChart(holder.pie_chart_2, countVo.getPointValues2(), countVo.getPie2Title1());
        } else if (viewType == App.TYPE_BOTTOM) {
            final String[] tips = mCountFragment.getResources().getStringArray(R.array.tips);
            int i = MathUtils.random(tips.length);
            holder.tips.setText(tips[i]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = MathUtils.random(tips.length);
                    holder.tips.setText(tips[i]);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        com.ccqiuqiu.fmoney.View.CountAnimationTextView tv_jingzichan;
        TextView tv_zichan, tv_fuzhai, tv_title;
        ColumnChartView column_chart;
        LineChartView line_chart;
        PieChartView pie_chart, pie_chart_2;
        ImageView img_more;
        DecoView decoView;
        TextView tips;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_jingzichan = (com.ccqiuqiu.fmoney.View.CountAnimationTextView) itemView.findViewById(R.id.tv_jingzichan);
            tv_zichan = (TextView) itemView.findViewById(R.id.tv_zongzichan);
            tv_fuzhai = (TextView) itemView.findViewById(R.id.tv_fuzhai);
            //img_more = (ImageView) itemView.findViewById(R.id.img_more);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            column_chart = (ColumnChartView) itemView.findViewById(R.id.column_chart);
            line_chart = (LineChartView) itemView.findViewById(R.id.line_chart);
            pie_chart = (PieChartView) itemView.findViewById(R.id.pie_chart);
            pie_chart_2 = (PieChartView) itemView.findViewById(R.id.pie_chart_2);
            tips = (TextView) itemView.findViewById(R.id.tips);

            decoView = (DecoView) itemView.findViewById(R.id.dynamicArcView);
        }
    }
}
