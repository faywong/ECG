package com.outsource.ekg.ui;

import com.outsource.ekg.defs.IEKGMsgParser;
import com.outsource.ekg.defs.IEKGMsgSegment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.outsource.ekg.R;

public class WaveformView extends View implements
		IEKGMsgParser.OnDataCaptureListener {
	private static final boolean DEBUG = true;
	private static final String TAG = "WaveformView";

	// internal data members
	RawData mRawData = new RawData();
	private Rect mRect = new Rect();
	private Bitmap mCanvasBitmap;
	private Canvas mCanvas;
	private Renderer mRenderer;
	private String mTitle;
	private int mDecorBackgroundColor = 0xff0B0B61;
	private int mGridBackgroundColor = 0xFFFCFDF1;
	private int mGridColor = 0xFFE5B0B7;
	private int mWaveformColor = 0xFFA29390;
	private int mBackgroundRoundRadiusX = 12;
	private int mBackgroundRoundRadiusY = 12;

	// drawing level in x direction
	private int mGridCellX = 30;
	// drawing level in y direction
	private int mGridCellY = 10;
	// the text size of grid label
	private int mLabelSize = 15;
	// the color of grid label
	private int mLabelColor = Color.WHITE;

	private int width;
	private int height;

	public enum Config {
		CFG_X_MIN, CFG_X_MAX, CFG_Y_MIN, CFG_Y_MAX, CFG_NUM
	}

	public enum Label {
		LABEL_X, LABEL_Y, LABEL_NUM
	}

	private float[] mThresholds = new float[Config.CFG_NUM.ordinal()];
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
		for (int i = Config.CFG_X_MIN.ordinal(); i < Config.CFG_NUM.ordinal(); i++) {
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

	public void setThreshold(Config key, float threshold) {
		switch (key) {
		case CFG_X_MIN:
		case CFG_X_MAX:
		case CFG_Y_MIN:
		case CFG_Y_MAX:
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

		drawOuterRoundRect();
		drawGridAndLabels();

		// draw the waveform
		if (null != mRenderer) {
			mRenderer.render(mCanvas, mRawData, mRect);
		}
		canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
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
		int width = mCanvasBitmap.getWidth();
		int height = mCanvasBitmap.getHeight();
		if (DEBUG) {
			Log.d(TAG, "when drawGridAndLabels() width:" + width + " height:"
					+ height);
		}
		int left = 0;
		int top = 0;
		// first drawing the labels
		final Paint paint = new Paint();
		paint.setColor(mLabelColor);
		paint.setTypeface(Typeface.SANS_SERIF);
		paint.setTextSize(mLabelSize);
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

		textDimension = calcTextDimension(paint, label);
		if (null == textDimension || textDimension.length != 2) {
			return;
		}
		// draw y label
		label = mLabels[Label.LABEL_Y.ordinal()];
		mCanvas.drawText(label, left, top, paint);
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
		int right = width - textDimension[0] - mBackgroundRoundRadiusX;
		int bottom = height - textDimension[1] - mBackgroundRoundRadiusY;
		mCanvas.drawText(label, right, bottom, paint);

		bottom -= 2 * textDimension[1];
		// draw the grid background
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(mGridBackgroundColor);
		mCanvas.drawRect(left, top, right, bottom, paint);

		int totalGridWidth = right - left;
		int totalGridHeight = bottom - top;
		// Store the rect for render drawing
		mRect.set(left, top, totalGridWidth, totalGridHeight);

		// draw the grid itself
		// TODO:
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(mGridColor);
		float cellWidth = totalGridWidth / mGridCellX;
		float cellHight = totalGridHeight / mGridCellY;

		// draw horizontal lines
		for (int y = 0; y <= mGridCellY; y++) {
			float ceil_y = Math.min(left + y * cellHight, bottom);
			mCanvas.drawLine(left, ceil_y, right, ceil_y, paint);
		}

		// draw vertical lines
		for (int x = 0; x <= mGridCellX; x++) {
			float ceil_x = Math.min(left + x * cellWidth, right);
			mCanvas.drawLine(ceil_x, top, ceil_x, bottom, paint);
		}
	}

	private static int[] calcTextDimension(Paint paint, String text) {
		if (paint == null || text == null) {
			return null;
		}
		FontMetrics tp = paint.getFontMetrics();
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		return new int[] { rect.width(), rect.height() };
	}

	/**
	 * Pass rightly captured raw data to this view
	 */
	@Override
	public void onWaveFormDataCaptured(byte[] bytes, int format) {
		// TODO Auto-generated method stub
		mRawData.bytes = bytes;
		invalidate();
	}

	/**
	 * Pass a newly found segment to this view
	 */
	@Override
	public void onNewSegmentFound(IEKGMsgSegment segment) {
		// TODO Auto-generated method stub

	}
}
