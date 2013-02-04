package com.outsource.ecg.ui;

import com.outsource.ecg.defs.IECGMsgParser;
import com.outsource.ecg.defs.IECGMsgSegment;
import com.outsource.ecg.ui.Renderer.RenderCfg;
import com.outsource.ecg.ui.Renderer.RenderHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.outsource.ecg.R;

public class WaveformView extends View implements
		IECGMsgParser.OnDataCaptureListener, RenderHelper {
	private static final boolean DEBUG = true;
	private static final String TAG = "WaveformView";

	// internal data members
	RawData mRawData = new RawData();
	private Rect mRect = new Rect();
	private Bitmap mCanvasBitmap;
	private Canvas mCanvas;
	private Renderer mRenderer;
	private Matrix mMatrix = new Matrix();
	private String mTitle;
	private int mDecorBackgroundColor = 0xff0B0B61;
	private int mGridBackgroundColor = 0xFFFCFDF1;
	private int mGridColor = 0xFFE5B0B7;
	
	/* the grid & labels should be rendered only when most needed */
	private boolean mInitialized = false;
	private int mWaveformColor = 0xFFA29390;
	private int mBackgroundRoundRadiusX = 12;
	private int mBackgroundRoundRadiusY = 12;

	// drawing level in x direction
	private int mGridCellX = 10;
	// drawing level in y direction
	private int mGridCellY = 30;
	// the text size of grid label
	private int mLabelSize = 15;
	// the color of grid label
	private int mLabelColor = 0xff340000;
	
	// store the cell size info, NO. of cells in vertical/horizontal direction
	private float[] mCellInfo = null;
	
	private int [] mGridSize = null;
	private float mMaxXValue = 10.0f;
	private float mMaxYValue = 30.0f;
	private float mMinXValue = 0.0f;
	private float mMinYValue = 0.0f;
	private float mXVelocity = 0.2f;
	/*
	 * The value represent range per cell(in x/y direction)
	 */
	private float[] mValueDensity;
	private int mMaxYLabelWidth = 0;
	private int mMaxHorizontalLabelHeight = 0;

	private float[] mInterestedDataRegion;
	private int width;
	private int height;
	private RenderCfg mRenderConfig = null;
	public enum Region {
		REG_X_MIN, REG_X_MAX, REG_Y_MIN, REG_Y_MAX, REG_NUM
	}

	public enum Label {
		LABEL_X, LABEL_Y, LABEL_NUM
	}

	private float[] mThresholds = new float[Region.REG_NUM.ordinal()];
	private String[] mLabels = new String[Label.LABEL_NUM.ordinal()];

	public WaveformView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context, attrs, defStyle);
	}

	public WaveformView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaveformView(Context context) {
		this(context, null, 0);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.width = w;
		this.height = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void init(Context context, AttributeSet attrs, int defStyle) {
		for (int i = Region.REG_X_MIN.ordinal(); i < Region.REG_NUM.ordinal(); i++) {
			// initialize these thresholds to invalid
			mThresholds[i] = Float.NaN;
		}

		for (int i = Label.LABEL_X.ordinal(); i < Label.LABEL_NUM.ordinal(); i++) {
			mLabels[i] = null;
		}
		// retrieve some config info from layout xml file
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.WaveformView, defStyle, 0);
		mGridCellX = a.getInteger(R.styleable.WaveformView_cellX, mGridCellX);
		mGridCellY = a.getInteger(R.styleable.WaveformView_cellY, mGridCellY);
		mMaxXValue = a.getFloat(R.styleable.WaveformView_maxXValue, mMaxXValue);
		mMaxYValue = a.getFloat(R.styleable.WaveformView_maxYValue, mMaxYValue);
		mMinXValue = a.getFloat(R.styleable.WaveformView_minXValue, mMinXValue);
		mMinYValue = a.getFloat(R.styleable.WaveformView_minYValue, mMinYValue);
		mXVelocity = a.getFloat(R.styleable.WaveformView_xVelocity, mXVelocity);
		mValueDensity = new float[] { (mMaxXValue - mMinXValue) / mGridCellX, (mMaxYValue - mMinYValue) / mGridCellY};

		if (DEBUG) {
			Log.d(TAG, "mMaxXValue:" + mMaxXValue + " mMaxYValue:" + mMaxYValue
					+ " mMinXValue:" + mMinXValue + " mMinYValue:" + mMinYValue
					+ " mXVelocity:" + mXVelocity);
		}
		mInterestedDataRegion = new float[] { mMinXValue, mMaxXValue, mMinYValue,
				mMaxYValue };

		mDecorBackgroundColor = a.getColor(
				R.styleable.WaveformView_decorBackgroundColor,
				mDecorBackgroundColor);
		mGridBackgroundColor = a.getColor(
				R.styleable.WaveformView_gridBackgroundColor,
				mGridBackgroundColor);
		mGridColor = a.getColor(R.styleable.WaveformView_gridColor, mGridColor);
		mWaveformColor = a.getColor(R.styleable.WaveformView_waveformColor,
				mWaveformColor);
		mLabelColor = a.getColor(R.styleable.WaveformView_labelColor,
				mLabelColor);
		mLabelSize = a.getInteger(R.styleable.WaveformView_labelSize,
				mLabelSize);
		mLabels[Label.LABEL_X.ordinal()] = a
				.getString(R.styleable.WaveformView_xAxislabel);
		mLabels[Label.LABEL_Y.ordinal()] = a
				.getString(R.styleable.WaveformView_yAxislabel);
		mBackgroundRoundRadiusX = a.getInteger(
				R.styleable.WaveformView_decorBackgroundRadiusX,
				mBackgroundRoundRadiusX);
		mBackgroundRoundRadiusY = a.getInteger(
				R.styleable.WaveformView_decorBackgroundRadiusY,
				mBackgroundRoundRadiusY);
		mTitle = a.getString(R.styleable.WaveformView_title);
		a.recycle();
	}

	public void setRenderer(Renderer renderer) {
		if (mRenderer == null && renderer != null) {
			mRenderer = renderer;
			mRenderer.setRenderHelper(this);
		}
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setLabel(Label key, String label) {
		switch (key) {
		case LABEL_X:
		case LABEL_Y:
			if (DEBUG) {
				Log.d(TAG, "setLabel(key" + key + " label:" + label + ")");
			}
			mLabels[key.ordinal()] = label;
		default:
			Log.d(TAG, "ERROR! invalid key:" + key + " in setLabel()");
		}
	}

	public void setThreshold(Region key, float threshold) {
		switch (key) {
		case REG_X_MIN:
		case REG_X_MAX:
		case REG_Y_MIN:
		case REG_Y_MAX:
			if (DEBUG) {
				Log.d(TAG, "setThreshold(key" + key + " threshold:" + threshold
						+ ")");
			}
			mThresholds[key.ordinal()] = threshold;
		default:
			Log.d(TAG, "ERROR! invalid key:" + key + " in setThreshold()");
		}
	}

	public void setGridDimension(int x, int y) {
		if (x > 0 && y > 0) {
			mGridCellX = x;
			mGridCellY = y;
		} else {
			throw new IllegalArgumentException(
					"x & y dimension must be greater than zero!");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mCanvasBitmap == null) {
			mCanvasBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
		}
		if (mCanvas == null) {
			mCanvas = new Canvas(mCanvasBitmap);
		}

		if (!mInitialized) {
			drawOuterRoundRect();
			drawGridAndLabels();
			if (null == mRenderConfig) {
				mRenderConfig = new RenderCfg(mCanvas, mRect, mInterestedDataRegion, mCellInfo, mValueDensity, mGridSize, mXVelocity);
			}
			mInitialized = mRenderer.init(mRenderConfig);
		}

		// draw the waveform
		if (null != mRenderer) {
			mRenderer.render(mRawData);
		}
		canvas.drawBitmap(mCanvasBitmap, mMatrix, null);
	}

	// the outer decorate round rectangle
	private void drawOuterRoundRect() {
		if (null == mCanvas) {
			return;
		}
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, mCanvas.getWidth(),
				mCanvas.getHeight());
		final RectF rectF = new RectF(rect);

		// get the little rounded cornered outside
		paint.setAntiAlias(true);
		mCanvas.drawARGB(0, 0, 0, 0);
		paint.setColor(mDecorBackgroundColor);
		mCanvas.drawRoundRect(rectF, mBackgroundRoundRadiusX,
				mBackgroundRoundRadiusY, paint);
	}

	private void drawGridAndLabels() {
		if (null == mCanvas) {
			return;
		}
		if (DEBUG) {
			Log.d(TAG, "when drawGridAndLabels() width:" + this.width + " height:"
					+ this.height);
		}
		int left = 0;
		int top = 0;
		// first drawing the labels
		Paint paint = getLabelPaint();
		String label = mTitle;
		int[] textDimension = calcTextDimension(paint, label);
		if (null == textDimension || textDimension.length != 2) {
			return;
		}
		left += mBackgroundRoundRadiusX;
		top += mBackgroundRoundRadiusY;

		// draw title
		mCanvas.drawText(label, width / 2 - textDimension[0] / 2, top, paint);
		top += textDimension[1];

		// draw y label
		label = mLabels[Label.LABEL_Y.ordinal()];
		mCanvas.drawText(label, left, top, paint);
		String yMaxLabel = Float.toString(mThresholds[Region.REG_Y_MAX
				.ordinal()]);
		textDimension = calcTextDimension(paint, yMaxLabel);
		if (null == textDimension || textDimension.length != 2) {
			return;
		}
		mMaxYLabelWidth = textDimension[0];
		textDimension = maxDimension(calcTextDimension(paint, label),
				textDimension);
		if (null == textDimension || textDimension.length != 2) {
			return;
		}
		left += textDimension[0];
		top += textDimension[1];

		// draw x label
		label = mLabels[Label.LABEL_X.ordinal()];
		if (DEBUG) {
			Log.d(TAG, "The label x is " + label);
		}
		textDimension = calcTextDimension(paint, label);
		if (null == textDimension || textDimension.length != 2) {
			return;
		}
		mMaxHorizontalLabelHeight = textDimension[1];
		int right = width - textDimension[0] - mBackgroundRoundRadiusX;
		int bottom = height - mBackgroundRoundRadiusY;
		mCanvas.drawText(label, right, bottom, paint);

		bottom -= 2.5f * textDimension[1];

		// draw the grid background
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(mGridBackgroundColor);
		mCanvas.drawRect(left, top, right, bottom, paint);

		int totalGridWidth = right - left;
		int totalGridHeight = bottom - top;

		// Store the rect for render drawing
		mRect.set(left, top, right, bottom);

		// draw the grid itself
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(mGridColor);
		float cellWidth = totalGridWidth / mGridCellX;
		float cellHight = totalGridHeight / mGridCellY;
		if (mCellInfo == null && mGridSize == null) {
			mCellInfo = new float[] { cellWidth, cellHight };
			mGridSize = new int[] { mGridCellX, mGridCellY };
		}
		// draw the grid itself step1: draw vertical label and lines
		for (int y = 0; y <= mGridCellY; y++) {
			float ceil_y = Math.max(bottom - y * cellHight, top);
			float verticalValue = mMinYValue + (mMaxYValue - mMinYValue) * y
					/ mGridCellY;
			String verticalLabel = Float.toString(verticalValue);
			mCanvas.drawText(verticalLabel,
					left - mMaxYLabelWidth - 4 /* margin */, ceil_y
							+ mMaxHorizontalLabelHeight / 2, getLabelPaint());
			mCanvas.drawLine(left, ceil_y, right, ceil_y, paint);
		}

		// draw the grid itself step2: draw horizontal label & lines
		for (int x = 0; x <= mGridCellX; x++) {
			float ceil_x = Math.min(left + x * cellWidth, right);
			float horizontalValue = mMinXValue + (mMaxXValue - mMinXValue) * x
					/ mGridCellX;
			String horizontalLabel = Float.toString(horizontalValue);
			int xOffset = 0;
			textDimension = calcTextDimension(getLabelPaint(), horizontalLabel);
			if (null != textDimension && textDimension.length == 2) {
				xOffset = textDimension[0] / 2;
			}
			int horizontalLableY = bottom + mMaxHorizontalLabelHeight + 2 /* margin */;
			mCanvas.drawText(horizontalLabel, (int) ceil_x - xOffset,
					horizontalLableY, getLabelPaint());
			mCanvas.drawLine(ceil_x, top, ceil_x, bottom, paint);
		}
	}

	private static int[] calcTextDimension(Paint paint, String text) {
		if (paint == null || text == null) {
			return null;
		}
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		return new int[] { rect.width(), rect.height() };
	}

	private static int[] maxDimension(int[] first, int[] second) {
		if (first == null || first.length != 2 || second == null
				|| second.length != 2) {
			return null;
		}
		return new int[] { Math.max(first[0], second[0]),
				Math.max(first[1], second[1]) };
	}

	private Paint getLabelPaint() {
		Paint paint = new Paint();
		paint.setColor(mLabelColor);
		paint.setTypeface(Typeface.SANS_SERIF);
		paint.setTextSize(mLabelSize);
		return paint;
	}

	/**
	 * Pass rightly captured raw data to this view
	 */
	@Override
	public void onWaveFormDataCaptured(float[] data, int format) {
		// TODO Auto-generated method stub
		mRawData.data = data;
		if (DEBUG) {
			Log.d(TAG, "onWaveFormDataCaptured");
		}
		invalidate();
	}

	/**
	 * Pass a newly found segment to this view
	 */
	@Override
	public void onNewSegmentFound(IECGMsgSegment segment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void redraw(int region) {
		// TODO Auto-generated method stub
		switch(region) {
		case RenderHelper.REGION_GRID_BACKGROUND | RenderHelper.REGION_GRID_ITSELF | RenderHelper.REGION_GRID_LABELS:
			drawGridAndLabels();
			break;
		}
	}
}
