/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hztuen.zxing.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.hztuen.shanqi.R;
import com.hztuen.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    private Bitmap line;
    Bitmap line1;
    //  private final Paint paint;
    private Bitmap resultBitmap;
    //  private final int maskColor;
//  private final int resultColor;
//  private final int frameColor;
//  private final int laserColor;
//  private final int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    int a = 100;
    private Paint mPaint;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
//    paint = new Paint();
//    Resources resources = getResources();
//    maskColor = resources.getColor(R.color.viewfinder_mask);
//    resultColor = resources.getColor(R.color.result_view);
//    frameColor = resources.getColor(R.color.viewfinder_frame);
//    laserColor = resources.getColor(R.color.viewfinder_laser);
//    resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
//    mPaint.setAntiAlias(false);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(6);
//    mPaint.setAlpha(100);
        line1 = BitmapFactory.decodeResource(this.getContext().getResources(), R.mipmap.scanning_line);


    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int height = canvas.getHeight();

        //画二维码扫描中间的那根线
        if (a < height * 3 / 10) {
            a = height * 3 / 10;
        }

        int width = canvas.getWidth();

        int lineX1 = width / 6;
        int lineX2 = width * 5 / 6;

        if (line == null) {
            line = scaleBitmap(line1, width * 2 / 3, height / 100);
        }

        canvas.drawBitmap(line, lineX1, a, null);

        a = a + 5;
        if (a > height * 5 / 7) {
            a = height * 3 / 10;
        }


        if (resultBitmap != null) {

        } else {

            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }
}
