package com.yhz.study.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 * Created by yhz on 2018/3/6.
 *
 */

public class ColorPickView extends LinearLayout {
    public interface OnColorBarListener{
        public void moveBar(int color);
    }

    //设置的默认颜色
    private static final int[] PICKCOLORBAR_COLORS = new int[]{Color.rgb(255,0,0),
            Color.rgb(255,255,0),Color.rgb(0,255,0),
            Color.rgb(0,255,255),Color.rgb(0,0,255)};
    //每个颜色的位置
    private static final float[] PICKCOLORBAR_POSITIONS = new float[]{0f,0.25f,0.5f,0.75f,1f};
    private static final float[] SUBBAR_POSITIONS = new float[]{0f,1f};

    private int[] mSubColors = new int[]{Color.BLACK,Color.RED};
    private SeekBar mMainBar;
    private SeekBar mSubBar;
    private int mSubPostion = 0;

    public ColorPickView(Context context) {
        super(context);
    }

    public ColorPickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        linerInit();
        barInit(context);
        defaultBarListener();
        addView(mMainBar);
        addView(mSubBar);
    }

    public ColorPickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ColorPickView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void barInit(Context context) {
        mMainBar = new SeekBar(context);
        mSubBar = new SeekBar(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,0);
        params.weight=1f;
        params.gravity = Gravity.CENTER;
        params.height = 105;
        mMainBar.setPadding(20,40,20,40);
        mSubBar.setPadding(20,40,20,40);
        mMainBar.setLayoutParams(params);
        mSubBar.setLayoutParams(params);

        initMainBarBg();
        setSubBarBg(Color.RED);
    }

    private void initMainBarBg(){
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient linearGradient = new LinearGradient(0, 0, width, height,
                        PICKCOLORBAR_COLORS,PICKCOLORBAR_POSITIONS, Shader.TileMode.REPEAT);
                return linearGradient;
            }
        };
        PaintDrawable paintMain = new PaintDrawable();
        paintMain.setShape(new RectShape());
        paintMain.setCornerRadius(12);
        paintMain.setShaderFactory(shaderFactory);
        mMainBar.setProgressDrawable(paintMain);
    }

    private void setSubBarBg(final int color){
        mSubColors[1]=color;
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient linearGradient = new LinearGradient(0, 0, width, height,
                        mSubColors , SUBBAR_POSITIONS, Shader.TileMode.REPEAT);
                return linearGradient;
            }
        };

        PaintDrawable paint = new PaintDrawable();

        paint.setShape(new RectShape());
        paint.setCornerRadius(12);
        paint.setShaderFactory(shaderFactory);
        mSubBar.setProgressDrawable(paint);
    }

    private void linerInit() {
        setWeightSum(2);
        setOrientation(VERTICAL);
    }


    public void setBarListener(final OnColorBarListener listener){
        mMainBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radio = (float)progress / mSubBar.getMax();
                int color = getColor(radio,PICKCOLORBAR_COLORS,PICKCOLORBAR_POSITIONS);
                setSubBarBg(color);
                listener.moveBar(getColor(mSubPostion,mSubColors,SUBBAR_POSITIONS));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSubBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSubPostion = progress;
                float radio = (float)progress / mSubBar.getMax();
                listener.moveBar(getColor(radio,mSubColors,SUBBAR_POSITIONS));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void defaultBarListener(){
        mMainBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radio = (float)progress / mSubBar.getMax();
                setSubBarBg(getColor(radio,PICKCOLORBAR_COLORS,PICKCOLORBAR_POSITIONS));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 获取某个百分比位置的颜色
     * @param radio 取值[0,1]
     * @return color
     */
    public int getColor(float radio,int []colorArr,float []positionArr) {
        int startColor;
        int endColor;
        if (radio >= 1) {
            return colorArr[colorArr.length - 1];
        }
        for (int i = 0; i < positionArr.length; i++) {
            if (radio <= positionArr[i]) {
                if (i == 0) {
                    return colorArr[0];
                }
                startColor = colorArr[i - 1];
                endColor = colorArr[i];
                float areaRadio = getAreaRadio(radio,positionArr[i-1],positionArr[i]);
                return getColorFrom(startColor,endColor,areaRadio);
            }
        }
        return -1;
    }

    private float getAreaRadio(float radio, float startPosition, float endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    /**
     *  取两个颜色间的渐变区间 中的某一点的颜色
     * @param startColor s
     * @param endColor e
     * @param radio r
     * @return color
     */
    public int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }
}
