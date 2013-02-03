package com.outsource.ekg.ui;

import android.graphics.Canvas;
import android.graphics.Rect;

abstract public class Renderer
{
  // Have these as members, so we don't have to re-create them each time
  protected float[] mPoints;
  protected float[] mFFTPoints;
  public Renderer()
  {
  }

  // As the display of raw/FFT audio will usually look different, subclasses
  // will typically only implement one of the below methods
  /**
   * Implement this method to render the audio data onto the canvas
   * @param canvas - Canvas to draw on
   * @param data - Data to render
   * @param rect - Rect to render into
   */
  abstract public void onRender(Canvas canvas, RawData data, Rect rect, float[] region);

  /**
   * Implement this method to render the FFT audio data onto the canvas
   * @param canvas - Canvas to draw on
   * @param data - Data to render
   * @param rect - Rect to render into
   * @param region - Data region want to be renderred
   */
  abstract public void onRender(Canvas canvas, FFTData data, Rect rect, float[] region);


  // These methods should actually be called for rendering
  /**
   * Render the audio data onto the canvas
   * @param canvas - Canvas to draw on
   * @param data - Data to render
   * @param rect - Rect to render into
   * @param region - Data region want to be renderred
   */
  final public void render(Canvas canvas, RawData data, Rect rect, float[] region)
  {
    if (mPoints == null || mPoints.length < data.bytes.length * 4) {
      mPoints = new float[data.bytes.length * 4];
    }

    onRender(canvas, data, rect, region);
  }

  /**
   * Render the FFT data onto the canvas
   * @param canvas - Canvas to draw on
   * @param data - Data to render
   * @param rect - Rect to render into
   */
  final public void render(Canvas canvas, FFTData data, Rect rect, float[] region)
  {
    if (mFFTPoints == null || mFFTPoints.length < data.bytes.length * 4) {
      mFFTPoints = new float[data.bytes.length * 4];
    }

    onRender(canvas, data, rect, region);
  }
}
