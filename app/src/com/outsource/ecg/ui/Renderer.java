package com.outsource.ecg.ui;

import android.graphics.Canvas;
import android.graphics.Rect;

abstract public class Renderer {
	// Have these as members, so we don't have to re-create them each time
/*	protected float[] mPoints;
	protected float[] mFFTPoints;*/
	protected RenderHelper mHelper;
	protected RenderCfg mRenderCfg;
	public Renderer() {
	}

	/**
	 * Implement this method to initialize this render object
	 * 
	 * @param cfg
	 *            - RenderCfg used to configure this render object
	 */
	abstract public boolean init(RenderCfg cfg);
	
	// As the display of raw/FFT audio will usually look different, subclasses
	// will typically only implement one of the below methods
	/**
	 * Implement this method to render the audio data onto the canvas
	 * 
	 * @param canvas
	 *            - Canvas to draw on
	 * @param data
	 *            - Data to render
	 * @param rect
	 *            - Rect to render into
	 */
	abstract public void onRender(RawData data);

	/**
	 * Implement this method to render the FFT audio data onto the canvas
	 * 
	 * @param canvas
	 *            - Canvas to draw on
	 * @param data
	 *            - Data to render
	 * @param rect
	 *            - Rect to render into
	 * @param region
	 *            - Data region want to be renderred
	 */
	abstract public void onRender(FFTData data);

	// These methods should actually be called for rendering
	/**
	 * Render the audio data onto the canvas
	 * 
	 * @param canvas
	 *            - Canvas to draw on
	 * @param data
	 *            - Data to render
	 * @param rect
	 *            - Rect to render into
	 * @param region
	 *            - Data region want to be renderred
	 */
	final public void render(RawData data) {
/*		if (mPoints == null || mPoints.length < (data.data.length - 1) * 4) {
			mPoints = new float[(data.data.length - 1) * 4];
		}*/
		onRender(data);
	}

	/**
	 * Render the FFT data onto the canvas
	 * 
	 * @param canvas
	 *            - Canvas to draw on
	 * @param data
	 *            - Data to render
	 * @param rect
	 *            - Rect to render into
	 */
	final public void render(FFTData data) {
/*		if (mFFTPoints == null || mFFTPoints.length < data.bytes.length * 4) {
			mFFTPoints = new float[data.bytes.length * 4];
		}*/
		onRender(data);
	}

	/**
	 * Set a render help to this render object, so as to request some other's
	 * help
	 */
	public void setRenderHelper(RenderHelper helper) {
		mHelper = helper;
	}
	
	/**
	 * The parameter class used by Render when renderring
	 * 
	 * @author faywong
	 * 
	 */
	public static class RenderCfg {
		/* the drawing api holder */
		public Canvas canvas;
		/* The Rectangle region to render into */
		public Rect rect;
		/* wanted data region(composite order is: Xmin, Xmax, Ymin, Ymax) */
		public float[] region;
		/* wanted cell size which configured in the view properties */
		public float[] cellSize;
		/*
		 * the value density in x & y direction, when displaying waveform
		 * dynamically, the density in y direction is invalid
		 */
		public float[] density;
		/* the size of grid(in the unit of cell) */
		public int[] gridSize;
		/* the velocity in x direction, valid when displaying dynamic data */
		public float xVelocity;

		public RenderCfg(Canvas canvas, Rect rect, float[] region,
				float[] cellSize, float[] density, int[] gridSize,
				float xVelocity) {
			this.canvas = canvas;
			this.rect = rect;
			this.region = region;
			this.cellSize = cellSize;
			this.density = density;
			this.gridSize = gridSize;
			this.xVelocity = xVelocity;
		}
	}

	/**
	 * interface which acting as a render object's buddy to perform some trivial
	 * operations(such as redraw some region)
	 * 
	 * @author faywong
	 * 
	 */
	public interface RenderHelper {
		/**
		 * region description integers
		 */
		public static final int REGION_GRID_BACKGROUND = 1;
		public static final int REGION_GRID_LABELS = 1 << 1;
		public static final int REGION_GRID_ITSELF = 1 << 2;

		/**
		 * any client should use this method to request the RenderHelper object to redraw some region
		 * 
		 * @param region
		 *              - region description integer
		 */
		public void redraw(int region);
	}
}
