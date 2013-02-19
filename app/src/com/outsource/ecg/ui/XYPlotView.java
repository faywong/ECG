/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------
 * XYPlotView.java
 * -----------------
 * (C) Copyright 2011, by ICOMSYSTECH Co.,Ltd.
 *
 * Original Author:  Yamakami Souichirou (for ICOMSYSTECH Co.,Ltd);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Oct-2011 : Added new sample code (SY);
 */

package com.outsource.ecg.ui;

import org.afree.chart.AFreeChart;
import org.afree.chart.axis.DateAxis;
import org.afree.chart.axis.DateTickMarkPosition;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.DatasetRenderingOrder;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.StandardXYItemRenderer;
import org.afree.chart.renderer.xy.XYBarRenderer;
import org.afree.chart.renderer.xy.XYItemRenderer;
import org.afree.data.time.Day;
import org.afree.data.time.TimePeriodAnchor;
import org.afree.data.time.TimeSeries;
import org.afree.data.time.TimeSeriesCollection;
import org.afree.data.xy.IntervalXYDataset;
import org.afree.data.xy.XYDataset;
import org.afree.date.MonthConstants;

import android.content.Context;

/**
 * XYPlotView
 */
public class XYPlotView extends BaseChartView {

    /**
     * constructor
     * @param context
     */
    public XYPlotView(Context context) {
        super(context);

        final AFreeChart chart = createChart();

        setChart(chart);
    }

    /**
     * Creates an overlaid chart.
     * @return The chart.
     */
    private static AFreeChart createChart() {

        DateAxis domainAxis = new DateAxis("Date");
        domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        ValueAxis rangeAxis = new NumberAxis("HBR");

        // create plot...
        IntervalXYDataset data1 = createDataset1();
        //XYItemRenderer renderer1 = new XYBarRenderer(0.20);
        XYItemRenderer renderer1 = new StandardXYItemRenderer();

        XYPlot plot = new XYPlot(data1, domainAxis, rangeAxis, renderer1);

        ValueAxis rangeAxis2 = new NumberAxis("HBR");
        plot.setRangeAxis(1, rangeAxis2);

        // create subplot 2...
        //XYDataset data2A = createDataset2A();
        //plot.setDataset(1, data2A);
        //XYItemRenderer renderer2A = new StandardXYItemRenderer();
        //plot.setRenderer(1, renderer2A);
        //renderer2A.setSeriesStroke(0, 2.0f);

        /*
        XYDataset data2B = createDataset2B();
        plot.setDataset(2, data2B);
        plot.setRenderer(2, new StandardXYItemRenderer());
        plot.getRenderer(2).setSeriesStroke(0, 2.0f);

        plot.mapDatasetToRangeAxis(2, 1);
        */
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        plot.setOrientation(PlotOrientation.VERTICAL);

        AFreeChart chart = new AFreeChart(
                "XYPlot Demo 02",
                AFreeChart.DEFAULT_TITLE_FONT,
                plot,
                true);
        
        return chart;
    }

    /**
     * Creates a sample dataset.
     * @return The dataset.
     */
    private static IntervalXYDataset createDataset1() {

        TimeSeries series1 = new TimeSeries("Series 1");

        for(int i = 1; i <= 10; i++) {
        	series1.add(new Day(i, MonthConstants.OCTOBER, 2011),
        			Math.random() * 7000 + 11000);
        }

        TimeSeriesCollection result = new TimeSeriesCollection(series1);
        return result;
    }

    /**
     * Creates a sample dataset.
     * @return The dataset.
     */
    private static XYDataset createDataset2A() {

        TimeSeries series2 = new TimeSeries("Series 2-A");

        for(int i = 1; i <= 20; i++) {
        	series2.add(new Day(i, MonthConstants.OCTOBER, 2011),
        			Math.random() * 8000 + 12000);
        }

        TimeSeriesCollection result = new TimeSeriesCollection(series2);
        result.setXPosition(TimePeriodAnchor.MIDDLE);
        return result;
    }

    /**
     * Creates a sample dataset.
     * @return The dataset.
     */
    private static XYDataset createDataset2B() {

        TimeSeries series2 = new TimeSeries("Series 2-B");

        for(int i = 1; i <= 20; i++) {
        	series2.add(new Day(i, MonthConstants.OCTOBER, 2011),
        			Math.random() * 70 + 25);
        }

        TimeSeriesCollection result = new TimeSeriesCollection(series2);
        result.setXPosition(TimePeriodAnchor.MIDDLE);
        return result;
    }

}
