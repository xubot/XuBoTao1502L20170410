package com.example.administrator.xubotao1502l20170410.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.example.administrator.xubotao1502l20170410.R;

/**
 * 用途：
 * 作者：xuBoTao
 * 时间：2017/4/10 09:00
 */

public class MyView extends View {
    private float circleDimensionRate=0.1f;
    private float triangleDimensionRate=0.1f;
    private int color_start;
    private int color_end;


    private float s;//三角形边长
    private float strokeWidth;//圆环宽度
    private float angle=360;//角度0-360
    private RectF oval = new RectF();
    private Paint paint = new Paint();
    private Path path = new Path();//绘制三角形的路径
    private Path path_matrix = new Path();//经过matrix变换的三角形路径
    private Matrix matrix = new Matrix();//三角形旋转矩阵
    Rect rect = new Rect();//测量文字所占的高度宽度
    private final float sqrt3 = (float) Math.sqrt(3);
    public MyView(Context context) {
        super(context);
        init(null,0);
    }

    public MyView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }
    public void init(AttributeSet attrs,int defStyleAttr)
    {
        //得到属性对象
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyView, defStyleAttr, 0);
        color_start = typedArray.getColor(R.styleable.MyView_color_start, Color.GREEN);
        color_end = typedArray.getColor(R.styleable.MyView_color_end, Color.LTGRAY);
        circleDimensionRate = typedArray.getFloat(R.styleable.MyView_circleDimensionRate,0.1f);
        triangleDimensionRate = typedArray.getFloat(R.styleable.MyView_triangleDimensionRate,0.1f);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        initDrawVar(width);
        //画绿色圆环
        paint.setAntiAlias(true);
        paint.setColor(color_start);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        //正右方为0度，顺时针旋转，正上方为270度
        canvas.drawArc(oval,270-angle,angle,false,paint);
        //画一个灰色的圆环
        paint.setColor(color_end);
        canvas.drawArc(oval,270,360-angle,false,paint);
        //画三角形
        paint.setStrokeWidth(1);
        paint.setColor(color_start);
        paint.setStyle(Paint.Style.FILL);

        matrix.reset();
        matrix.setRotate(180-angle);//旋转
        float angle_rad = (float)(angle/180f*Math.PI);//化成弧度制
        matrix.postTranslate((float) (width/2f-(width/2f-s+s/sqrt3)*Math.sin(angle_rad)), (float) (width/2f-(width/2f-s+s/sqrt3)*Math.cos(angle_rad)));
        path_matrix.set(path);
        path_matrix.transform(matrix);
        canvas.drawPath(path_matrix,paint);

        //显示百分比
        paint.setTextSize(width*0.1f);
        String text = String.valueOf(Math.round(angle/360*100))+"%";
        //paint.getFontMetrics()方法测量不准确，无法保证居中显示；paint.getTextBounds方法测量较准确
        paint.getTextBounds(text,0,text.length(),rect);//测量text所占宽度和高度
        canvas.drawText(text, width/2f - rect.width()/2, width/2f+ rect.height() / 2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float r = this.getWidth()/2-s-strokeWidth;//圆环半径
                //判断在圆环外 则处理触摸事件
                if(Math.pow(x-this.getWidth()/2,2) +
                        Math.pow(y-this.getWidth()/2,2) > Math.pow(r,2)){
                    double angle = Math.atan((this.getWidth()/2-x)/(this.getWidth()/2-y));
                    angle = angle/Math.PI*180;
                    if(x>this.getWidth()/2 && y<=this.getWidth()/2) {//第一象限
                        angle += 360;
                    }else if(y>this.getWidth()/2){//第三四象限
                        angle += 180;
                    }
                    if(Math.abs(this.angle-angle)>1){
                        this.angle = (float)angle;
                        this.invalidate();
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void initDrawVar(int width) {
        if (s != triangleDimensionRate * width) {
            s = triangleDimensionRate * width;//三角形边长
            strokeWidth = circleDimensionRate * width;//圆环宽度
            //绘制圆环的矩形区域
            oval.set(s + strokeWidth / 2f, s + strokeWidth / 2f,
                    width - s - strokeWidth / 2f, width - s - strokeWidth / 2f);
            //绘制三角形的路径
            path.reset();
            path.moveTo(0, -s / sqrt3);
            path.lineTo(s / 2f, sqrt3 / 6 * s);
            path.lineTo(-s / 2f, sqrt3 / 6 * s);
            path.close();
        }
    }
    //获取当前选择的比例
    public float getRate(){
        return angle/360f;
    }

    public int getColor_start() {
        return color_start;
    }

    public void setColor_start(int color_start) {
        this.color_start = color_start;
    }

    public int getColor_end() {
        return color_end;
    }

    public void setColor_end(int color_end) {
        this.color_end = color_end;
    }

    public float getCircleDimensionRate() {
        return circleDimensionRate;
    }

    public void setCircleDimensionRate(float circleDimensionRate) {
        this.circleDimensionRate = circleDimensionRate;
    }

    public float getTriangleDimensionRate() {
        return triangleDimensionRate;
    }

    public void setTriangleDimensionRate(float triangleDimensionRate) {
        this.triangleDimensionRate = triangleDimensionRate;
    }

}
