package com.game.sdk.view;

import com.game.sdk.utils.StringUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlideSwitchButton extends View {

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿

	boolean isOn = false;
	float curX = 0;
	float centerY; // y固定
	float viewWidth;
	float radius;
	float lineStart; // 直线段开始的位置（横坐标，即
	float lineEnd; // 直线段结束的位置（纵坐标
	float lineWidth;
	final int SCALE = 4; // 控件长度为滑动的圆的半径的倍数
	OnStateChangedListener onStateChangedListener;
	Context mContext;
	boolean isMove = false;
	
	String lineColor= "f65d4a";
	
	public SlideSwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public SlideSwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public SlideSwitchButton(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		curX = event.getX();
		
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			isMove = true;
		}
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if(isMove){
				if (curX > viewWidth / 2) {
					curX = lineEnd;
					if (false == isOn) {
						// 只有状态发生改变才调用回调函数， 下同
						if (null != onStateChangedListener) {
							onStateChangedListener.onStateChanged(true);
						}
						isOn = true;
					}
				} else {
					curX = lineStart;
					if (true == isOn) {
						if (null != onStateChangedListener) {
							onStateChangedListener.onStateChanged(false);
						}
						isOn = false;
					}
				}
				isMove = false;
			}else{
				if (false == isOn) {
					curX = lineEnd;
					// 只有状态发生改变才调用回调函数， 下同
					if (null != onStateChangedListener) {
						onStateChangedListener.onStateChanged(true);
					}
					isOn = true;
				} else {
					curX = lineStart;
					if (null != onStateChangedListener) {
						onStateChangedListener.onStateChanged(false);
					}
					isOn = false;
				}
			}
		}
		/* 通过刷新调用onDraw */
		this.postInvalidate();
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/* 保持宽是高的SCALE / 2倍， 即圆的直径 */
		this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredWidth() * 2 / SCALE);
		viewWidth = this.getMeasuredWidth();
		radius = viewWidth / SCALE;
		lineWidth = radius * 2f; // 直线宽度等于滑块直径
		curX = isOn ? radius + lineWidth : radius;
		centerY = this.getMeasuredWidth() / SCALE; // centerY为高度的一半
		lineStart = radius;
		lineEnd = (SCALE - 1) * radius;
	}
	
	public void setClickSwitchOpen(){
		isOn = !isOn;
		curX = isOn ? radius + lineWidth : radius;
		
		if (false == isOn) {
			//curX = lineEnd;
			// 只有状态发生改变才调用回调函数， 下同
			if (null != onStateChangedListener) {
				onStateChangedListener.onStateChanged(false);
			}
		} else {
			//curX = lineStart;
			if (null != onStateChangedListener) {
				onStateChangedListener.onStateChanged(true);
			}
		}
		
		this.postInvalidate();
	}
	
	//设置是否打开开关
	public void setSwitchOpen(boolean isSwitch){
		isOn = isSwitch;
	}
	
	//设置开关背景线的颜色
	public void setLineColor(String lineColor){
		if(!StringUtils.isEmpty(lineColor)){			
			this.lineColor = lineColor;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		/* 限制滑动范围 */
		curX = curX > lineEnd ? lineEnd : curX;
		curX = curX < lineStart ? lineStart : curX;

		/* 划线 */
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(lineWidth);
		/* 左边部分的线，绿色 */
		mPaint.setColor(Color.parseColor("#"+lineColor));
		canvas.drawLine(lineStart, centerY, curX, centerY, mPaint);
		/* 右边部分的线，灰色 */
		mPaint.setColor(Color.parseColor("#f2f5f5f5"));
		canvas.drawLine(curX, centerY, lineEnd, centerY, mPaint);

		/* 画圆 */
		/* 画最左和最右的圆，直径为直线段宽度， 即在直线段两边分别再加上一个半圆 */
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.parseColor("#f2f5f5f5"));
		canvas.drawCircle(lineEnd, centerY, lineWidth / 2, mPaint);
		mPaint.setColor(Color.parseColor("#"+lineColor));
		canvas.drawCircle(lineStart, centerY, lineWidth / 2, mPaint);
		/* 圆形滑块 */
		mPaint.setColor(Color.parseColor("#e1e1e1"));
		canvas.drawCircle(curX, centerY, radius, mPaint);

	}

	/* 设置开关状态改变监听器 */
	public void setOnStateChangedListener(OnStateChangedListener o) {
		this.onStateChangedListener = o;
	}

	/* 内部接口，开关状态改变监听器 */
	public interface OnStateChangedListener {
		public void onStateChanged(boolean state);
	}

}