package com.yuong.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.math.BigDecimal;

/**
 * Created by yuandong on 2018/5/17.
 */

public class ProgressView extends View {
    private static String TAG = ProgressView.class.getSimpleName();

    //view的宽度和高度
    private int width, height;

    //圆心坐标
    private float oX, oY;

    //圆心半径
    private float radius;

    //环型宽度
    private float circleWidth;

    //矩形（用于画弧）
    private RectF oval;

    //圆形画笔
    private Paint circlePaint;

    //扇形画笔
    private Paint arcPaint, arcPaint2;

    //文字画笔
    private Paint textPaint;

    //矩形(用于确定文本的宽高)
    private Rect bounds;

    //边距
    private int padding;

    //背景色
    private int bgColor = Color.parseColor("#F8F8F8");

    //圆环背景色
    private int bgCircleColor = Color.parseColor("#EEEFF3");

    //标签字体颜色
    private int labelTextColor = Color.parseColor("#4C4C4C");

    //默认字体颜色
    private int defaultTextColor = Color.parseColor("#7F7F7F");

    //颜色一（用于建议替换）
    private int textColor = Color.parseColor("#EBE420");

    //颜色二（用于正常使用）
    private int textColor2 = Color.parseColor("#50C749");

    //渐变颜色
    private int[] shaderColor = {Color.parseColor("#C7E61D"),
            Color.parseColor("#EBE420")};

    //渐变颜色二
    private int[] shaderColor2 = {Color.parseColor("#26C5A9"),
            Color.parseColor("#6ADD5C")};

    //弧的总长度
    private float maxValue;

    //当前弧的长度（用于动态画弧）
    private float value;

    //起始画弧角度
    private float startAngle;

    //剩余百分比
    private float percentage;


    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        padding = dp2px(10);
        circleWidth = dp2px(25);

        // 初始化画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setColor(bgCircleColor);
        circlePaint.setStrokeWidth(circleWidth);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStyle(Paint.Style.STROKE);

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setDither(true);
        arcPaint.setStrokeWidth(circleWidth);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStyle(Paint.Style.STROKE);

        arcPaint2 = new Paint();
        arcPaint2.setAntiAlias(true);
        arcPaint2.setDither(true);
        arcPaint2.setStrokeWidth(circleWidth);
        arcPaint2.setStyle(Paint.Style.STROKE);

        oval = new RectF();
        bounds = new Rect();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        radius = width / 4;
        height = width / 2 + 2 * padding + (int) circleWidth;
        oX = width / 2;
        oY = width / 4 + padding + circleWidth / 2;

        oval.left = oX - radius;
        oval.top = oY - radius;
        oval.right = oX + radius;
        oval.bottom = oY + radius;

        double sin = circleWidth / 2 / radius;
        // Log.e(TAG, "正弦值 ：" + sin);
        startAngle = (float) numberFormat(Math.asin(sin) * 180 / Math.PI, 1);
        Log.i(TAG, "正弦值对应的角度 ：" + startAngle);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(bgColor);
        drawCircle(canvas);
        drawText(canvas);
    }


    //画圆
    private void drawCircle(Canvas canvas) {
        canvas.save();
        //画背景圈
        canvas.drawCircle(oX, oY, radius, circlePaint);
        //画动态弧
        SweepGradient shader = null;
        if (value > 0.2 * 360) {
            shader = new SweepGradient(oX, oY, shaderColor2, new float[]{0, value / 360});
        } else {
            shader = new SweepGradient(oX, oY, shaderColor, new float[]{0, value / 360});
        }
        if (value > 0 && value < 360) {
            if (value >= 360 - startAngle) {
                value = 360 - startAngle;
            }
            if (value < 2 * startAngle) {
                arcPaint2.setShader(shader);
                canvas.rotate(-180, oX, oY);
                canvas.drawArc(oval, 0, value, false, arcPaint2);//画弧
            } else {
                arcPaint.setShader(shader);
                canvas.rotate(-180 - startAngle, oX, oY);
                canvas.drawArc(oval, startAngle, value - startAngle, false, arcPaint);//画弧
            }
        } else {
            arcPaint.setShader(shader);
            canvas.rotate(-180, oX, oY);
            canvas.drawArc(oval, 0, value, false, arcPaint);//画弧
        }
        canvas.restore();
    }


    private void startAnimation() {
        ValueAnimator anim = ValueAnimator.ofFloat(value, maxValue);
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        anim.start();
    }

    //画文字
    private void drawText(Canvas canvas) {

        textPaint.setTextSize(sp2px(18));
        String text = "滤芯剩余寿命";
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        textPaint.setColor(labelTextColor);
        canvas.drawText(text, oX, oY + bounds.height() / 2, textPaint);

        textPaint.setTextSize(sp2px(36));
        String text2 = (int) percentage + "%";
        textPaint.setColor(getColor(percentage));
        canvas.drawText(text2, oX, oY - 2 * padding, textPaint);

        textPaint.setTextSize(sp2px(14));
        String text3 = "";
        if (percentage > 20) {
            textPaint.setColor(defaultTextColor);
            text3 = "正常使用";
        } else if (percentage > 0) {
            textPaint.setColor(defaultTextColor);
            text3 = "可以更换";
        }else{
            text3 = "请更换";
            textPaint.setColor(defaultTextColor);
        }
        textPaint.getTextBounds(text3, 0, text3.length(), bounds);

        canvas.drawText(text3, oX, oY + 2 * padding + bounds.height(), textPaint);
    }

    private int getColor(float percentage) {
        if (percentage > 20) {
            return textColor2;
        } else if (percentage > 0) {
            return textColor;
        }
        return defaultTextColor;
    }

    /**
     * dp转化为px
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * 四点五入
     *
     * @param number
     * @param digit  保留几位小数
     * @return
     */
    private double numberFormat(double number, int digit) {
        BigDecimal b = new BigDecimal(number);
        return b.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //设置数据
    public void setPercentage(float percentage) {
        this.percentage = percentage;
        this.maxValue = percentage * 360 / 100;
        startAnimation();
    }
}
