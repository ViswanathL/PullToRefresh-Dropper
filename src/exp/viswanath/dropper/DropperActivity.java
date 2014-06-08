package exp.viswanath.dropper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/*
 * 
 * Author: Viswanath L
 * 
 * viswanath.l@experionglobal.com
 * 
 * 08-Dec-2013
 *
 */

public class DropperActivity extends Activity implements UiRefreshListener, OnItemClickListener{

	private CustomList myView;

	private CustomAdapter adapter;

	private Runnable myRunnable;
	private Handler handler;

	String[] values = {"Shop By Category", "Category", "Notification", "Video", "Flasgship Store", "About Us", "Follow Rocksmith", "Contact Us","About Us", "Follow Rocksmith", "Contact Us","Shop By Category", "Category", "Notification", "Video", "Flasgship Store", "About Us", "Follow Rocksmith", "Contact Us","About Us", "Follow Rocksmith", "Contact Us"};
	Integer[] imageId = {
			R.drawable.category,
			R.drawable.cart,
			R.drawable.notification,
			R.drawable.video,
			R.drawable.flagship,
			R.drawable.about_us,
			R.drawable.contact_us,
			R.drawable.about,
			R.drawable.category,
			R.drawable.cart,
			R.drawable.notification,
			R.drawable.category,
			R.drawable.cart,
			R.drawable.notification,
			R.drawable.video,
			R.drawable.flagship,
			R.drawable.about_us,
			R.drawable.contact_us,
			R.drawable.about,
			R.drawable.category,
			R.drawable.cart,
			R.drawable.notification
	};

	/*
	 * Use CustomList in layout and set adapter using set adapter method of the view 
	 * use the UiRefreshListener interface to get the refresh events
	 * After refreshing the view call the OnRefreshCompleted() to finish refreshing
	 * 
	 * You can also implement the OnItemClickListener interface to get nofied on item click
	 * 
	 */
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wrapper_layout);

		myView = (CustomList)findViewById(R.id.wrapper);
		adapter = new CustomAdapter(this, values, imageId);
		myView.SetAdapter(adapter);

		/* Set the listener */
		myView.setUiRefreshListener(this);
		handler = new Handler();

		myView.setItemClickListener(this);

		myRunnable = new Runnable() {
			@Override
			public void run() {
				Log.d("", "Refreshing runnable ");
				myView.OnRefreshCompleted();
				handler.removeCallbacks(myRunnable);
			}
		};	
	}

	/* Refresh listener will call this method on refreshing */
	@Override
	public void onRefreshed() {
		handler.postDelayed(myRunnable, 3000);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(this, values[position],Toast.LENGTH_SHORT).show();
	}

}
