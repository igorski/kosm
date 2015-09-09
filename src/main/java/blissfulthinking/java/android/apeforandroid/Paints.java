package blissfulthinking.java.android.apeforandroid;

import android.graphics.Paint;

public class Paints {
	
	public static final Paint rectanglePaint = new Paint();
	
	public static final Paint circlePaint = new Paint();
	
	public static final Paint textpaint = new Paint();
	
	
	public Paints() {
	}
	
	public void init() {
		rectanglePaint.setARGB(255, 255, 75, 10);
		rectanglePaint.setStrokeWidth(2.0f);
		
		circlePaint.setARGB(255, 10, 75, 255);
		circlePaint.setStrokeWidth(1.0f);
		
		textpaint.setARGB(255, 244, 244, 244);
		textpaint.setStrokeWidth(1.0f);
	}

}
