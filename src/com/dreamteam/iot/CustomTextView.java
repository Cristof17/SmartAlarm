package com.dreamteam.iot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView{

	public CustomTextView(Context context) {
		super(context);
	}
	
	

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}



	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}



	@Override
	protected void onDraw(Canvas canvas) {
		
		Paint paint = new Paint();
		canvas.drawPaint(paint);
		canvas.drawText(getText().toString(), 0, 0, paint);
		super.onDraw(canvas);
	}

	
	
}
