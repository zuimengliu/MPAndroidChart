
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Matrix;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Fill;
import com.xxmassdeveloper.mpchartexample.custom.XYMarkerView;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarChartActivityMultiDataset extends DemoBase {

    private BarChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        setTitle("BarChartActivityMultiDataset");


        BarChart twoBarChart = findViewById(R.id.chart1);
        twoBarChart.setDrawHighlightEnabled(false);

        /**
         * 需求：绘制12个月份的温度和风极双柱形
         */
        List<Float> temperatures = Arrays.asList(20f, 24f, 30f, 40f, 50f, 60f, 40f, 20f, 80f, 40f, 20f, 80f);
        List<Float> fengs = Arrays.asList(25f, 30f, 40f, 40f, 60f, 60f, 70f, 20f, 30f, 40f, 10f, 80f);
        final List<String> xList = Arrays.asList("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月");

        //设置曲线整体的配置
        twoBarChart.setNoDataText("暂无数据");
        //设置绘制动画
        twoBarChart.animateXY(1000, 1000);
        //隐藏说明
        twoBarChart.getDescription().setEnabled(false);

        //设置X轴
        XAxis xAxis = twoBarChart.getXAxis();
        xAxis.setAxisMinimum(0f); //要设置，否则右侧还有部分图表未展示出来
        xAxis.setAxisMaximum(xList.size()); //要设置，否则右侧还有部分图表未展示出来
        xAxis.setLabelCount(5, false); //要设置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置要不要X轴的网格，就是网格的竖线
        xAxis.setDrawGridLines(false);
        //将X轴的值显示在中央
        xAxis.setCenterAxisLabels(true);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //用(int) Math.abs(value) % xList.size()防止越界
                return xList.get((int) Math.abs(value) % xList.size());
            }
        };

        xAxis.setValueFormatter(xAxisFormatter);

        XYMarkerView mv = new XYMarkerView(getBaseContext(), xAxisFormatter);
        mv.setChartView(twoBarChart); // For bounds control
        twoBarChart.setMarker(mv); // Set the marker to the barChart

        //设置Y轴
        YAxis rightYAxis = twoBarChart.getAxisRight();
        //隐藏右边Y轴
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = twoBarChart.getAxisLeft();
        //设置网格为虚线
//        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        leftYAxis.setDrawGridLines(false);
        List<IBarDataSet> dataSets = new ArrayList<>();
        //设置温度数据
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, temperatures.get(i)));
        }
        BarDataSet barDataSet = new BarDataSet(entries, "温度示例");
        barDataSet.setRadiusPercent(0.2f);
        //设置柱形的颜色
        barDataSet.setColor(ContextCompat.getColor(getBaseContext(), android.R.color.holo_orange_dark));

        dataSets.add(barDataSet);

        //设置风级数据
        List<BarEntry> entries2 = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries2.add(new BarEntry(i, fengs.get(i)));
        }
        BarDataSet barDataSet2 = new BarDataSet(entries2, "风级示例");
        barDataSet2.setRadiusPercent(0.2f);
        //设置柱形的颜色
        barDataSet2.setColor(ContextCompat.getColor(getBaseContext(), android.R.color.holo_green_light));
        dataSets.add(barDataSet2);

        BarData data = new BarData(dataSets);
        //关键
        /**
         * float groupSpace    //柱状图组之间的间距
         * float barSpace   //每条柱状图之间的间距  一组两个柱状图
         * float barWidth     //每条柱状图的宽度     一组两个柱状图
         * (barWidth + barSpace) * barAmount + groupSpace = 1.00
         * 3个数值 加起来 必须等于 1 即100% 按照百分比来计算 组间距 柱状图间距 柱状图宽度
         */
        int barAmount = dataSets.size(); //需要显示柱状图的类别 数量
        //设置组间距占比30% 每条柱状图宽度占比 70% /barAmount  柱状图间距占比 0%
        float groupSpace = 0.3f; //柱状图组之间的间距
        float barSpace = 0.05f;
        float barWidth = (1f - groupSpace) / barAmount - 0.05f;
        //设置柱状图宽度
        data.setBarWidth(barWidth);
        //(起始点、柱状图组间距、柱状图之间间距)
        data.groupBars(0f, groupSpace, barSpace);
        //不画圆柱文字
        data.setDrawValues(false);


        twoBarChart.setData(data);
        Matrix mMatrix = new Matrix();
        float sx = xList.size() * 2 / 10f;
        mMatrix.postScale(sx, 1f);
        twoBarChart.getViewPortHandler().refresh(mMatrix, twoBarChart, false);
        twoBarChart.animateX(500);
    }

    @Override
    protected void saveToGallery() {

    }

}
