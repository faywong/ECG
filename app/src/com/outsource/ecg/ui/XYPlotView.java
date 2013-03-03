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
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.DatasetRenderingOrder;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.StandardXYItemRenderer;
import org.afree.chart.renderer.xy.XYItemRenderer;
import org.afree.data.xy.IntervalXYDataset;
import org.afree.data.xy.XYSeries;
import org.afree.data.xy.XYSeriesCollection;
import android.content.Context;
import android.util.AttributeSet;

/**
 * XYPlotView
 * Note: After create, a default dataset with an empty XYSeries item is created
 */
public class XYPlotView extends BaseChartView {
	private static final String DEFAULT_USER_NAME = "ECG Chart";
	public static final int DEFAULT_SERIES_INDEX = 0;
	private XYSeriesCollection mSeries;

	/**
	 * constructor
	 * 
	 * @param context
	 */
	public XYPlotView(Context context) {
		super(context);
		final AFreeChart chart = createChart(getDataset());
		setChart(chart);
	}

	public XYPlotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final AFreeChart chart = createChart(getDataset());
		setChart(chart);
	}

	/**
	 * Creates an XY chart.
	 * 
	 * @return The chart.
	 */
	private static AFreeChart createChart(IntervalXYDataset dataset) {
		NumberAxis domainAxis = new NumberAxis("Time");
		domainAxis.setAutoRangeIncludesZero(true);
		// domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
		ValueAxis rangeAxis = new NumberAxis("HBR");

		// create plot...
		// IntervalXYDataset data1 = getDataset();
		// XYItemRenderer renderer1 = new XYBarRenderer(0.20);
		XYItemRenderer renderer1 = new StandardXYItemRenderer();

		XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer1);

		// create subplot 2...
		// XYDataset data2A = createDataset2A();
		// plot.setDataset(1, data2A);
		// XYItemRenderer renderer2A = new StandardXYItemRenderer();
		// plot.setRenderer(1, renderer2A);
		// renderer2A.setSeriesStroke(0, 2.0f);

		/*
		 * XYDataset data2B = createDataset2B(); plot.setDataset(2, data2B);
		 * plot.setRenderer(2, new StandardXYItemRenderer());
		 * plot.getRenderer(2).setSeriesStroke(0, 2.0f);
		 * 
		 * plot.mapDatasetToRangeAxis(2, 1);
		 */
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		plot.setOrientation(PlotOrientation.VERTICAL);

		AFreeChart chart = new AFreeChart("Real-time ECG chart",
				AFreeChart.DEFAULT_TITLE_FONT, plot, true);

		return chart;
	}

	/**
	 * Create a SeriesCollection if not exist, user also can retrieve it to change the Series object in it
	 * 
	 * @return The dataset.
	 */
	public XYSeriesCollection getDataset() {
		if (null == mSeries) {
			XYSeries series1 = new XYSeries(DEFAULT_USER_NAME);

			mSeries = new XYSeriesCollection(series1);
		}
		return mSeries;
	}
}
