package exp.viswanath.dropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.util.AttributeSet;
import android.view.View;

/*
 * 
 * Author: Viswanath L
 * 
 * viswanath.l@experionglobal.com
 * 
 * 09-Dec-2013
 *
 */

/**
 * This class draws the droplet based on the touch on listview,<br>
 * Use the setThreshold value to set the touch distance,<br>
 * Invalidate after setting the threshold will redraw the view
 *  
 */

public class Droplet extends View
{
	// width of the canvas
	private int width;
	// Holds the drag position of ListView
	private int distance;
	
	// Co-ordinate points
	private int top_X1;
	private int top_X2;
	private int bottom_X1;
	private int bottom_X2;

	private static int BEND_POINT_X = 10;
	private static int BEND_POINT_Y = 80;

	// Radius of the cirle - bitmapWidth/2
	private static int RADIUS;

	// Y distance of center point of circle
	private static final int CENTER_POINT_Y = 60;

	// Threshold position when refresh has to be happened
	private static int REFRESH_POSITION;

	// Holds the caller or whom who get notified on refresh events
	private RefreshListener listener;

	// Slight x- difference value
	private final int SLOPE_THRESHOLD = 8;
	private final int OFFSET = 5;
	
	private Paint paint;

	/**
	 * Basic constructor that initializes the Droplet view
	 * @param context
	 * @param attrs
	 */
	public Droplet(Context context, AttributeSet attrs) {
		super(context, attrs);

		distance = 0;
		paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(getResources().getColor(R.color.PaleGray));
		paint.setStrokeWidth(4);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
	}

	/**
	 * Used to set the current threshold value,<br>
	 * Used frequently to notify ListView drag positions  
	 * @param distance
	 */
	public void setThreshold(int distance)
	{
		this.distance = (int) (distance/(1.3));
	}
	
	/**
	 * Set the listener object to get notified for refresh events
	 * @param listener
	 */
	public void setListener(RefreshListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * @Overrided method that handles the core portion, <br>
	 * @warning Dont change the implementation
	 * This method get called when ever a invalidate is invoked
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// Gets the whole available width to work on
		width = canvas.getWidth();

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.reload);
		// Create a mutable bitmap to work on. @warning dont load large images 
		Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		// Set the radius based on image width
		RADIUS = bitmap.getWidth() / 2;
		
		calculatePoints();
		// release unused bitmap memory
		bitmap.recycle();
		Path _path = new Path();
		
		if(distance < REFRESH_POSITION && distance >= 0)
		{
			// Rough Y point which bend to pass through
			BEND_POINT_Y = distance/2;
			// Rough X point which bend to pass through
			BEND_POINT_X = distance/5;

			_path.moveTo(top_X1, CENTER_POINT_Y); 
			_path.quadTo(top_X1 + BEND_POINT_X, CENTER_POINT_Y + BEND_POINT_Y, bottom_X1, CENTER_POINT_Y + distance);
			
			/* Calculate the radius of the bottom arc */
			int bottomRadius = (bottom_X2 - bottom_X1)/2; 
			
			_path.quadTo(bottom_X1 + bottomRadius, CENTER_POINT_Y + distance + bottomRadius, bottom_X2, CENTER_POINT_Y + distance);
			_path.quadTo(top_X2 - BEND_POINT_X, CENTER_POINT_Y + BEND_POINT_Y, top_X2, CENTER_POINT_Y );

			_path.lineTo(top_X1, CENTER_POINT_Y);

			_path.setFillType(FillType.WINDING);
		}
		_path.close();

		// Draw on canvas
		canvas.drawPath(_path, paint);
		canvas.drawBitmap(mutableBitmap, (width/2) - RADIUS, CENTER_POINT_Y - RADIUS, paint);

		refreshList();
		mutableBitmap.recycle();
	}

	/**
	 * Calculate the points to draw droplet
	 */
	private void calculatePoints() {
		// Dynamically calculate the refresh postion based on radius - This is resolution dependent
		REFRESH_POSITION = (RADIUS * 5) - 10;

		top_X1 = (width/2) - RADIUS;                        /* Left line top point  \  */
		top_X2 = (width/2) + RADIUS;                        /* Right line top point  /  */

		bottom_X1 = top_X1 + (distance/SLOPE_THRESHOLD) + OFFSET;  /* Left line bottom point  \  */
		bottom_X2 = top_X2 - (distance/SLOPE_THRESHOLD) - OFFSET;  /* Right line bottom point  /  */
	}

	/**
	 * This method get called when ever invalidate is invoked <br>
	 * This will notify the refreshListener if droplet exceeds the strech limit
	 */
	private void refreshList() {
		if(distance >= REFRESH_POSITION)
		{
			if(listener != null)
				listener.RefreshList();
		}
	}

}