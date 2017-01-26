package com.ccqiuqiu.fmoney.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.MainActivity;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Utils.DateUtils;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.devspark.progressfragment.ProgressFragment;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.DummyColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.DummyLineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.DummyPieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by cc on 2015/12/17.
 */
public class BaseFragment extends ProgressFragment {

    public MainActivity mMainActivity;
    public View mContentView;
    public int[] mSchemeColors = new int[]{Color.parseColor("#e91e63"), Color.parseColor("#e91e63"),//d500f9
            Color.parseColor("#304ffe"), Color.parseColor("#00bcd4"), Color.parseColor("#0a8f08"),
            Color.parseColor("#aeea00"), Color.parseColor("#ef6c00"), Color.parseColor("#f4511e")};

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == App.WHAT_FRAGMENT_LOAD) {
                loaded();
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //设置布局文件
        setContentView(mContentView);
        //设置为空的时候的文本
        setEmptyText(R.string.data_empty);//没有数据时候调用setContentEmpty(true);
        //初始化控件
        initView();
        //开启新线程执行耗时操作
        startSync();
    }

    public void startSync() {
        //显示loading
        setContentShownNoAnimation(false);
        new Thread() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(1000);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                asyncLoadData();
                mHandler.sendEmptyMessage(App.WHAT_FRAGMENT_LOAD);

            }
        }.start();
    }

    public void loaded() {
        setContentShownNoAnimation(true);
    }

    public void reData() {
        startSync();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void initFab(FloatingActionButton mFab) {
        mFab.hide(true);
    }

    public enum DetailsState {
        Closed, Opening, Opened, Closing
    }

    //    private boolean injected = false;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        injected = true;
//        return x.view().inject(this, inflater, container);
//    }
//
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (!injected) {
//            x.view().inject(this, this.getView());
//        }
        mMainActivity = (MainActivity) getActivity();
    }

    public void asyncLoadData() {
    }

    public void initView() {
    }

    public void onSearchExit() {
    }

    public void onSearchTermChanged() {
    }

    public void onSearch(String string) {
    }

    public void onSearchCleared() {
    }

    public boolean backPressed() {
        return false;
    }

    public void initEmptyLineChart(LineChartView chart, float top, float bottom, int color) {
        List<Line> lines = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < 12; i++) {
            float num = 0;
            values.add(new PointValue(i, num));
            String ym = DateUtils.DateToString(DateUtils.addMonth(date, i - 11), "yyMM");
            //只显示月份，小屏幕上不会太拥挤
            if (i > 0 && !ym.endsWith("01")) {
                ym = ym.substring(2);
            }
            axisValues.add(new AxisValue(i).setLabel(ym));
        }
        Line line = new Line(values);
        line.setColor(color);
        line.setFilled(true);
        line.setHasLines(true);
        //line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        lines.add(line);

        LineChartData data = new LineChartData(lines);

        data.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        data.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.TRANSPARENT));

        //data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

        Viewport v = new Viewport(0, top, 11, bottom);
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
        chart.setValueSelectionEnabled(true);
        chart.setZoomType(ZoomType.VERTICAL);
    }

    public void showColumnChart_bj(ColumnChartView chart, List<PointValue> pointValues, int color) {
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 0; i < pointValues.size(); i++) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(pointValues.get(i).getY(), color));
            Column column = new Column(values);
            //column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }
        ColumnChartData data = new ColumnChartData(columns);
        data.setAxisXBottom(null);
        data.setAxisYLeft(null);
        chart.setClickable(false);
        chart.setColumnChartData(data);
    }

    public void showColumnChart(ColumnChartView chart, final List<PointValue> pointValues) {
        List<String> colors = getColors();
        List<Column> columns = new ArrayList<>();
        final List<AxisValue> axisValues = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 0; i < pointValues.size(); i++) {
            PointValue value = pointValues.get(i);
            String[] labelSrr = new String(value.getLabelAsChars()).split(App.FENGEFU);
            int index = (int) (Math.random() * (colors.size() - 1));
            if (colors.size() == 0) {
                colors = getColors();
            }
            String color = colors.remove(index);
            values = new ArrayList<>();
            values.add(new SubcolumnValue(value.getY(), Color.parseColor(color))
                    .setLabel(labelSrr[1]));
            Column column = new Column(values);
            column.setHasLabels(true);
            //column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
            String label = labelSrr[0].length() > 4 ? labelSrr[0].substring(0, 4) : labelSrr[0];
            axisValues.add(new AxisValue(i).setLabel(label));
        }
        ColumnChartData data = new ColumnChartData(columns);
        data.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        data.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.TRANSPARENT));
        chart.setClickable(true);
        chart.setColumnChartData(data);
        chart.setOnValueTouchListener(new DummyColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                if (mMainActivity.mGuillotineOpened) {
                    return;
                }
                ViewUtils.snackbar(getContext(), new String(pointValues.get(columnIndex)
                        .getLabelAsChars()).split(App.FENGEFU)[0]
                        + "：" + new String(value.getLabelAsChars()));
            }
        });
    }

    private List<String> getColors() {
        List<String> colors = new ArrayList<>();
        String[] colorsStrings = getResources().getStringArray(R.array.colors);
        for (String c : colorsStrings) {
            colors.add(c);
        }
        return colors;
    }

    public void showLineChart(LineChartView chart, final List<PointValue> pointValues, int color) {
        float top = 0, bottom = 0;
        List<Line> lines = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < pointValues.size(); i++) {
            PointValue value = pointValues.get(i);
            values.add(new PointValue(value.getX(), value.getY())
                    .setLabel(new String(value.getLabelAsChars()).split(App.FENGEFU)[1]));
            float num = 0;
            //pointValues.add(new PointValue(i, num));
            String ym = DateUtils.DateToString(DateUtils.addMonth(date, i - 11), "yyMM");
            //只显示月份，小屏幕上不会太拥挤
            if (i > 0 && !ym.endsWith("01")) {
                ym = ym.substring(2);
            }
            axisValues.add(new AxisValue(i).setLabel(ym));

            if (i == 0) {
                top = pointValues.get(i).getY();
                bottom = pointValues.get(i).getY();
            }
            if (pointValues.get(i).getY() > top) {
                top = pointValues.get(i).getY();
            }
            if (pointValues.get(i).getY() < bottom) {
                bottom = pointValues.get(i).getY();
            }
        }
        Line line = new Line(values);
        line.setColor(color);
        line.setFilled(true);
        line.setHasLines(true);
        //line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        lines.add(line);

        LineChartData data = new LineChartData(lines);


        data.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        data.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.TRANSPARENT));
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

        chart.setValueSelectionEnabled(true);
        chart.setZoomType(ZoomType.VERTICAL);
        chart.setOnValueTouchListener(new DummyLineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                if (mMainActivity.mGuillotineOpened) {
                    return;
                }
                String text = new String(pointValues.get(pointIndex).getLabelAsChars())
                        .replace(App.FENGEFU, "：");
                ViewUtils.snackbar(getContext(), text);
            }
        });
    }

    public void showPieChart(final PieChartView chart, final List<PointValue> pointValues, String title1) {
        List<String> colors = getColors();
        List<SliceValue> values = new ArrayList<>();
        for (PointValue value : pointValues) {
            String[] labelSrr = new String(value.getLabelAsChars()).split(App.FENGEFU);
            int index = (int) (Math.random() * (colors.size() - 1));
            if (colors.size() == 0) {
                colors = getColors();
            }
            String color = colors.remove(index);
            SliceValue sliceValue = new SliceValue(value.getY(), Color.parseColor(color))
                    .setLabel(labelSrr[2]);
            values.add(sliceValue);
        }
        PieChartData data = new PieChartData(values);
        //data.setHasLabels(true);
        data.setHasLabelsOnlyForSelected(true);
        data.setHasCenterCircle(true);
        data.setCenterText1(title1);
        data.setCenterText1Color(getResources().getColor(R.color.text_color_hei));
        data.setCenterText1FontSize(14);
        //chart.setValueSelectionEnabled(true);
        chart.setPieChartData(data);
        chart.setOnValueTouchListener(new DummyPieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                if (mMainActivity.mGuillotineOpened) {
                    return;
                }
                String[] textArr = new String(pointValues.get(arcIndex).getLabelAsChars()).split(App.FENGEFU);
                String text = textArr[0] + "\n" +
                        getString(R.string.input_sum_liushui) + "：" + textArr[1] + "  " +
                        getString(R.string.baifenbi) + "：" + textArr[2];

                ViewUtils.snackbar(getContext(), text);
            }
        });
    }

    public Float getChartTop(Float top) {
        Float re;
        if (top <= 100000f) {//小于10w  ，设置为10w
            re = top + 10000;
        } else if (top <= 1000000f) {//小于100w  加10w
            re = ((int) (top / 100000) + 1) * 100000f;
        } else {//加50w
            re = ((int) (top / 1000000)) * 1000000f + 200000f;
        }
        return re;
    }
}
