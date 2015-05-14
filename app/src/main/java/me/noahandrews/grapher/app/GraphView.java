package me.noahandrews.grapher.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class GraphView extends View{
    private int widthPX;
    private int heightPX;
    private float widthDP;
    private float heightDP;
    private float widthUnits;
    private float heightUnits;
    private float xOffsetPX;
    private float yOffsetPX;
    private float yOffsetUnits;
    private int DPperUnit = 80;
    private float PXperUnit;
    private DisplayMetrics displayMetrics;

    private float aCoefficient, bCoefficient, cCoefficient;

    private Paint equationPaint, axisPaint, gridPaint;
    private Path equationPath, xAxisPath, yAxisPath, gridLinePath;

    public static final String GRAPH_VIEW_TAG = "GraphView";

    public GraphView(Context context) {
        super(context);
        this.setWillNotDraw(false);
        init();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
        init();
        TypedArray attrsArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GraphView,
                0, 0);
        try {
            aCoefficient = attrsArray.getFloat(R.styleable.GraphView_a_coefficient,1);
            bCoefficient = attrsArray.getFloat(R.styleable.GraphView_b_coefficient,0);
            cCoefficient = attrsArray.getFloat(R.styleable.GraphView_c_coefficient,0);
            DPperUnit = attrsArray.getInt(R.styleable.GraphView_zoom_level, 80);
        } finally {
            attrsArray.recycle();
        }
    }

    public boolean setaCoefficient(int aCoefficient) {
        this.aCoefficient = aCoefficient;
        invalidate();
        requestLayout();
        if (aCoefficient != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setbCoefficient(float bCoefficient) {
        this.bCoefficient = bCoefficient;
        invalidate();
        requestLayout();
    }

    public boolean setcCoefficient(float cCoefficient) {
        updateZoomValues();
        float vertexXUnits = -bCoefficient / (2 * aCoefficient);
        float vertexYUnits = (aCoefficient * vertexXUnits * vertexXUnits) + (bCoefficient * vertexXUnits) + cCoefficient;
        if(Math.abs(vertexYUnits) < Math.abs(yOffsetUnits)) {
            this.cCoefficient = cCoefficient;
            invalidate();
            requestLayout();
            return true;
        } else {
            if(heightUnits == 0)return true;
            else return false;
        }
    }

    public boolean setZoomLevel(int zoomLevel) {
        updateZoomValues();
        float vertexXUnits = -bCoefficient / (2 * aCoefficient);
        float vertexYUnits = (aCoefficient * vertexXUnits * vertexXUnits) + (bCoefficient * vertexXUnits) + cCoefficient;
        if(Math.abs(vertexYUnits) < Math.abs(yOffsetUnits)) {
            this.DPperUnit = zoomLevel;
            invalidate();
            requestLayout();
            return true;
        } else {
            if(heightUnits == 0)return true;
            else return false;
        }
    }

    private void init() {
        displayMetrics = getResources().getDisplayMetrics();

        equationPaint = new Paint();
        equationPaint.setColor(Color.BLUE);
        equationPaint.setAlpha(255);
        equationPaint.setStrokeWidth(dpToPX(5));
        equationPaint.setStyle(Paint.Style.STROKE);

        axisPaint = new Paint(equationPaint);
        axisPaint.setStrokeWidth(dpToPX(5));
        axisPaint.setColor(Color.BLACK);

        gridPaint = new Paint(axisPaint);
        gridPaint.setStrokeWidth(dpToPX(2));

        xAxisPath = new Path();
        yAxisPath = new Path();
        equationPath = new Path();
        gridLinePath = new Path();
    }

    @Override
    public void onDraw(Canvas canvas){
        xAxisPath.moveTo(0, yOffsetPX);
        xAxisPath.lineTo(widthPX, yOffsetPX);

        yAxisPath.moveTo(xOffsetPX, 0);
        yAxisPath.lineTo(xOffsetPX, heightPX);

        {
            int horizontalGridIterator = 0;
            do {
                gridLinePath.reset();
                float yPositionPX = yOffsetPX + (horizontalGridIterator * PXperUnit);
                gridLinePath.moveTo(0, yPositionPX);
                gridLinePath.lineTo(widthPX, yPositionPX);
                canvas.drawPath(gridLinePath, gridPaint);
                horizontalGridIterator++;
            } while(horizontalGridIterator < heightUnits / 2);
            horizontalGridIterator = -1;
            float yPositionPX;
            do {
                gridLinePath.reset();
                yPositionPX = yOffsetPX + (horizontalGridIterator * PXperUnit);
                gridLinePath.moveTo(0, yPositionPX);
                gridLinePath.lineTo(widthPX, yPositionPX);
                canvas.drawPath(gridLinePath, gridPaint);
                horizontalGridIterator--;
            } while(yPositionPX > 0);
        }


        {
            int verticalGridIterator = 0;
            do {
                gridLinePath.reset();
                float xPositionPX = xOffsetPX + (verticalGridIterator * PXperUnit);
                gridLinePath.moveTo(xPositionPX, 0);
                gridLinePath.lineTo(xPositionPX, heightPX);
                canvas.drawPath(gridLinePath, gridPaint);
                verticalGridIterator++;
            } while(verticalGridIterator < widthUnits / 2);
            verticalGridIterator = -1;
            float xPositionPX;
            do {
                gridLinePath.reset();
                xPositionPX = xOffsetPX + (verticalGridIterator * PXperUnit);
                gridLinePath.moveTo(xPositionPX, 0);
                gridLinePath.lineTo(xPositionPX, heightPX);
                canvas.drawPath(gridLinePath, gridPaint);
                verticalGridIterator--;
            } while(xPositionPX > 0);
            int temp = 1;
        }


        equationPath.reset();
        if (aCoefficient != 0) {
            float startY = (aCoefficient > 0) ? heightUnits / 2 : -(heightUnits / 2) ;
            //This expression only works where b equals 0.
            // I don't have time to figure out how to make it work for non-zero values of b,
            // so I'm just going to calculate the graph for the entire width of the screen.
            // float startX = (float)( ( bCoefficient - Math.sqrt( (4 * aCoefficient * startY) - (4 * aCoefficient * cCoefficient) + (bCoefficient * bCoefficient) ) ) / (2 * Math.abs(aCoefficient)) );
            float startX = -(widthUnits / 2);
            float xUnits, yUnits, xPX, yPX;
            float vertexXUnits = -bCoefficient / (2 * aCoefficient);
            float vertexYUnits = (aCoefficient * vertexXUnits * vertexXUnits) + (bCoefficient * vertexXUnits) + cCoefficient;
            Log.i(GRAPH_VIEW_TAG, "Path generation initiated.");

            boolean firstIteration = true, beenVisible = false, isVisible = false;
            xUnits = startX;
            do {
                yUnits = (aCoefficient * xUnits * xUnits) + (bCoefficient * xUnits) + cCoefficient;
                xPX = xUnits * PXperUnit;
                yPX = yUnits * PXperUnit;
                xPX = xPX + xOffsetPX;
                yPX = heightPX - yPX - yOffsetPX;
                isVisible = yPX >= -50 && yPX <= heightPX + 50;
                Log.i(GRAPH_VIEW_TAG, "Equation path calculation in progress: (" + xUnits + "," + yUnits + ") units");
                if(!isVisible) {
                    xUnits += .05;
                    continue;
                }
                if(firstIteration){
                    Log.i(GRAPH_VIEW_TAG, "Equation path moved to (" + xPX + "," + yPX + ") PX.");
                    equationPath.moveTo(xPX, yPX);
                    firstIteration = false;
                } else {
                    equationPath.lineTo(xPX, yPX);
                }
                xUnits += .05;

                isVisible = yPX >= 0 && yPX <= heightPX + 50;
                if(isVisible && !beenVisible) {
                    beenVisible = true;
                }
            } while(isVisible || !beenVisible);

            Log.i(GRAPH_VIEW_TAG, "Path generated.");
        }

        canvas.drawPath(xAxisPath, axisPaint);
        canvas.drawPath(yAxisPath, axisPaint);
        canvas.drawPath(equationPath, equationPaint);
    }
    private boolean shouldGraphLoopContinue(int aCoefficient, float yPX) {
        if(aCoefficient > 0) {

        }
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        displayMetrics = getResources().getDisplayMetrics();

        this.widthDP = pxToDP(w);
        this.heightDP = pxToDP(h);
        this.widthPX = w;
        this.heightPX = h;
        updateZoomValues();

        this.xOffsetPX = w / 2;
        this.yOffsetPX = h / 2;

        Log.i(GRAPH_VIEW_TAG, "Size changed.");
        Log.i(GRAPH_VIEW_TAG, "New size in PX is " + widthPX + " x " + heightPX);
        Log.i(GRAPH_VIEW_TAG, "New size in DP is " + widthDP + " x " + heightDP);
        Log.i(GRAPH_VIEW_TAG, "New size in units is " + widthUnits + " x " + heightUnits);
    }

    private void updateZoomValues() {
        this.heightUnits = heightDP / DPperUnit;
        this.widthUnits = widthDP / DPperUnit;
        this.PXperUnit = dpToPX((float)DPperUnit);
        this.yOffsetUnits = heightUnits / 2;
    }

    public float dpToPX(float dp){
        return dp * (float)displayMetrics.densityDpi / (float)160;
    }

    public float pxToDP(float px) {
        return px * (float)160 / (float)displayMetrics.densityDpi;
    }
}
