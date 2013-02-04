package com.outsource.ecg.ui;

import android.graphics.Paint;
import android.util.Log;

public class LineRenderer extends Renderer {
	private static final String TAG = "LineRenderer";
	private static final boolean DEBUG = true;
	/**
	 * points container for every field/frame
	 */
	protected float[] mPoints;
	private Paint mPaint;
	int mLinesPerPage = 20;

	/**
	 * Renders the audio data onto a line. The line flashes on prominent beats
	 * 
	 * @param canvas
	 * @param paint
	 *            - Paint to draw lines with
	 * @param paint
	 *            - Paint to draw flash with
	 * @param cycleColor
	 *            - If true the color will change on each frame
	 */
	public LineRenderer(Paint paint) {
		mPaint = paint;
	}

	@Override
	public boolean init(RenderCfg cfg) {
		mRenderCfg = cfg;
		// verify input parameters
		if (mRenderCfg == null || mRenderCfg.region == null
				|| mRenderCfg.region.length != 4 || mRenderCfg.cellSize == null
				|| mRenderCfg.cellSize.length != 2
				|| mRenderCfg.density == null || mRenderCfg.density.length != 2
				|| mRenderCfg.gridSize == null
				|| mRenderCfg.gridSize.length != 2) {
			return false;
		} else {
			Double d = Double
					.valueOf((mRenderCfg.gridSize[0] * 1.0 / mRenderCfg.xVelocity));
			mLinesPerPage = d.intValue();
			Log.d(TAG, "The mLinesPerPage is " + mLinesPerPage);
			mPoints = new float[mLinesPerPage * 4];
			return true;
		}
	}

	@Override
	public void onRender(RawData rawData) {
		if (mPaint == null) {
			return;
		}

		Log.d(TAG, "faywong checkpoint 2");

		float cellWidth = mRenderCfg.cellSize[0];
		float cellHeight = mRenderCfg.cellSize[1];

		if (DEBUG) {
			Log.d(TAG, "onRender() checkpoint 1");
		}

		if (DEBUG) {
			Log.d(TAG, "The max points per page is " + mLinesPerPage);
		}
		float lastPointX = mRenderCfg.rect.left;
		float lastPointY = mRenderCfg.rect.bottom
				- (Math.max(0, rawData.data[0] - mRenderCfg.region[2]))
				* cellHeight / mRenderCfg.density[1];

		// Calculate points for line
		for (int i = 0; i < rawData.data.length - 1; i++) {
			int j = i % mLinesPerPage;
			Log.d(TAG, "j is " + j);
			mPoints[j  * 4] = lastPointX;
			mPoints[j  * 4 + 1] = lastPointY;
			lastPointX = mPoints[j * 4 + 2] = mRenderCfg.rect.left
					+ i % (mLinesPerPage + 1) * cellWidth
					* mRenderCfg.xVelocity;
			// the y size can't be greater than interestedRegion[3]
			float celly = Math.min(mRenderCfg.region[3], rawData.data[i + 1]);
			lastPointY = mPoints[j * 4 + 3] = mRenderCfg.rect.bottom
					- (Math.max(0, celly - mRenderCfg.region[2])) * cellHeight
					/ mRenderCfg.density[1];
			if (j == (mLinesPerPage - 1) && mHelper != null) {
				int region = RenderHelper.REGION_GRID_BACKGROUND | RenderHelper.REGION_GRID_ITSELF | RenderHelper.REGION_GRID_LABELS;
				mHelper.redraw(region);
				Log.d(TAG, "mPoints:");
				for (float tmp : mPoints) {
					Log.d(TAG, " " + tmp);
				}
				Log.d(TAG, "\n");
				mRenderCfg.canvas.drawLines(mPoints, mPaint);
/*				lastPointX = mRenderCfg.rect.left;
				lastPointY = mRenderCfg.rect.bottom
						- (Math.max(0, rawData.data[i + 1] - mRenderCfg.region[2]))
						* cellHeight / mRenderCfg.density[1];
				i++;*/
			}
		}

		if (DEBUG) {
			Log.d(TAG, "onRender() checkpoint 2");
		}
	}

	@Override
	public void onRender(FFTData data) {
		// Do nothing, we only display audio data
	}
}
