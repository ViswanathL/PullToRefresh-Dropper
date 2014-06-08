package exp.viswanath.dropper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/*
 * 
 * Author: Viswanath L
 * 
 * viswanath.l@experionglobal.com
 * 
 * 09-Dec-2013
 *
 */

public class CustomList extends LinearLayout implements OnTouchListener, RefreshListener{

	private RelativeLayout parentLayout;
	private Droplet myView;
	private ListView listView;
	private ProgressBar progressBar;

	private int initialY;
	private int _height;
	private int _viewHeight;

	private static final int STARTING_POSITION = 0;

	private UiRefreshListener uiRefreshListener;

	/* Flag that will be true on refreshing */
	private boolean _isRefreshed;

	/* Flag to disable propagated touch after refreshing */
	private boolean clearTouch;
	private PULL_STATUS status;

	private SoundPlayer mplayer;

	/* Flag for enabling and disabling sound */
	private boolean isSoundEnabled;

	/* Flag used for itemCLick */
	private boolean isMoved;

	private OnItemClickListener itemClickListener;

	public CustomList(Context context, AttributeSet attrs) {
		super(context, attrs);

		View rowView = LayoutInflater.from(context).inflate(R.layout.activity_dropper, null);

		parentLayout = (RelativeLayout)rowView.findViewById(R.id.parent);
		myView = (Droplet)parentLayout.findViewById(R.id.custom_view);

		progressBar = (ProgressBar)parentLayout.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.INVISIBLE);

		listView = (ListView)parentLayout.findViewById(R.id.listView);

		this.addView(rowView);

		init(context);
	}
	
	/**
	 * This handles the click event on the list view
	 */
	private android.widget.AdapterView.OnItemClickListener listItemClickListener =  new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			if(itemClickListener != null && !isMoved)
				itemClickListener.onItemClick(parent, view, position, id);
		}	
	};

	/**
	 *  Set the adapter for listView 
	 **/
	public void SetAdapter(ListAdapter adapter)
	{
		listView.setAdapter(adapter);
	}

	/**
	 *  Set the listener to get notified when item state changed 
	 **/
	public void setItemClickListener(OnItemClickListener listener)
	{
		itemClickListener = listener;
	}

	/**
	 * Initializes the view
	 */
	public void init(Context context)
	{
		status = PULL_STATUS.PULL_BAR_NOT_VISIBLE;

		itemClickListener = null;

		isSoundEnabled = true;
		mplayer = new SoundPlayer(context);

		/* Set the listview itemclick listener */
		listView.setOnItemClickListener(listItemClickListener);

		_viewHeight = myView.getLayoutParams().height;
		initList();

		_height = (-1) * _viewHeight;

		_isRefreshed = false;
		clearTouch = false;

		myView.setListener(this);
		listView.setOnTouchListener(this);
	}

	/**
	 * Set the listener to notify UI on refresh
	 */
	public void setUiRefreshListener(UiRefreshListener uiRefreshListener)
	{
		this.uiRefreshListener = uiRefreshListener;
	}

	/**
	 * Set sound enabled or disabled
	 */
	public void setSoundEnabled(boolean enable)
	{
		isSoundEnabled = enable;
	}

	/**
	 * This method will get called when a sound has to be played
	 * @param TYPE
	 */
	private void playSound(SOUND_TYPE TYPE)
	{
		if(!_isRefreshed && isSoundEnabled)
			mplayer.playSound(TYPE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int Y = (int)event.getRawY();


		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		{
			onTouch(Y);
			break;
		}
		case MotionEvent.ACTION_MOVE:
		{			
			int deltaY = Y - initialY;

			if(deltaY > 2) /* Flag to set itemCLick */
				isMoved = true;

			if(clearTouch)
				return true;

			if(status == PULL_STATUS.PULL_BAR_NOT_VISIBLE  && ((listView.getFirstVisiblePosition() != STARTING_POSITION || (listView.getFirstVisiblePosition() == STARTING_POSITION && listView.getChildAt(STARTING_POSITION).getTop() != STARTING_POSITION)) || deltaY <= STARTING_POSITION))
			{
				/* Just make it as listview handle it */
				initialY = Y;
				break;
			}
			else if(status == PULL_STATUS.PULL_BAR_COMPRESSING)
			{
				beginCompression(Y);
				return true;
			}
			else if((_height + deltaY) <= STARTING_POSITION  && (status == PULL_STATUS.PULL_BAR_NOT_VISIBLE || status == PULL_STATUS.PULL_BAR_ON_PROGRESS || status == PULL_STATUS.PULL_BAR_COMPRESSING)) /* Bring refresh button to screen */
			{
				moveDropletWithList(deltaY);
				return true;
			}
			else if(status == PULL_STATUS.PULL_BAR_ON_PROGRESS && (_height + deltaY) >= STARTING_POSITION)
			{
				/* To reset initialY on streching begins */
				beginExpansion(Y);
				return true;
			}
			else if(status == PULL_STATUS.PULL_BAR_VISIBLE || status == PULL_STATUS.PULL_BAR_STRECHING)
			{
				strechDroplet(deltaY);
				return true;
			}

			break;
		}
		case MotionEvent.ACTION_CANCEL:
		{
			break;
		}
		case MotionEvent.ACTION_UP:
		{	
			onActionUp();
			break;
		}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * This method gets called when user touch the screen<br>
	 * It will initialize the params
	 * @param Y
	 */
	private void onTouch(final int Y) {
		initialY = Y;
		clearTouch = false;

		isMoved = false;
	}

	/**
	 * This method gets called when the droplet is not visible<br>
	 * or it should move off the screen.<br>
	 * @param deltaY
	 */
	private void moveDropletWithList(int deltaY) {
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) parentLayout.getLayoutParams();

		if(status == PULL_STATUS.PULL_BAR_NOT_VISIBLE)
			setProgressBarVisibility(false);
		
		/* Pull the layout withing screen and off the screen */
		layoutParams.topMargin = _height + deltaY;
		parentLayout.setLayoutParams(layoutParams);
		status = PULL_STATUS.PULL_BAR_ON_PROGRESS;
	}

	/**
	 * This method get called when the expansion begins <br>
	 * This will initialize the params to start expansion<br>
	 * After this initialization droplet will strech
	 * @param Y
	 */
	private void beginExpansion(final int Y) {
		initialY = Y;
		playSound(SOUND_TYPE.PLAY_PULL);
		showRefreshList();

		status = PULL_STATUS.PULL_BAR_VISIBLE;
	}

	/**
	 * This method will handle the streching of droplet <br>
	 * This will handle both expansion and compression
	 * @param deltaY
	 */
	private void strechDroplet(int deltaY) {
		RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) myView.getLayoutParams();
		lParams.height = _viewHeight + deltaY;
		if(deltaY >= STARTING_POSITION) /* Pull bar streching */
		{
			myView.setLayoutParams(lParams);
			/* Set the distance */
			if(!getProgressBarVisibility())
			{
				myView.setThreshold(deltaY);
				myView.invalidate();
				status = PULL_STATUS.PULL_BAR_STRECHING;
			}
			else
				status = PULL_STATUS.PULL_BAR_VISIBLE;
		}
		else /* Pull bar compressing */
		{
			showRefreshList();
			status = PULL_STATUS.PULL_BAR_COMPRESSING;
		}
	}

	/**
	 * This method gets called when compression begins
	 * <br> It will initialize params for starting the compression,<br>
	 * After this view will move with list upwards
	 * @param Y
	 */
	private void beginCompression(final int Y) {
		initialY = Y + (_height); /* to reset initialY for compression */
		playSound(SOUND_TYPE.PLAY_COMPRESS);
		status = PULL_STATUS.PULL_BAR_ON_PROGRESS;
	}

	/**
	 * This method get called when user releases his finger after drag
	 */
	private void onActionUp() {
		if(_isRefreshed)
			showRefreshList();
		else
		{
			/* Hide the pull bar on touch release */
			initList();
			status = PULL_STATUS.PULL_BAR_NOT_VISIBLE;
		}
	}

	/** 
	 * To show idle refresh until OnrefreshCompleted() 
	 */
	private void showRefreshList()
	{
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) parentLayout.getLayoutParams();
		layoutParams.topMargin = STARTING_POSITION;
		parentLayout.setLayoutParams(layoutParams);

		RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) myView.getLayoutParams();
		lParams.height = _viewHeight;
		myView.setLayoutParams(lParams);

		myView.setThreshold(STARTING_POSITION);
		myView.invalidate();
	}

	/**
	 * This method is called to hide the refresh view and show listView only 
	 */
	private void initList() {

		/* Set the layout height */
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) parentLayout.getLayoutParams();
		layoutParams.topMargin = (-1) * _viewHeight;
		parentLayout.setLayoutParams(layoutParams);

		/* Set the view height */
		RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) myView.getLayoutParams();
		lParams.height = _viewHeight;
		myView.setLayoutParams(lParams);

		/* Restore droplet to initial position */
		myView.setThreshold(STARTING_POSITION);
		myView.invalidate();

		/* Hide progressbar */
		if(getProgressBarVisibility())
			setProgressBarVisibility(false);

		status = PULL_STATUS.PULL_BAR_NOT_VISIBLE;

		_isRefreshed = false;
	}

	/** 
	 * This is called when the dropper blows , Should refresh list on this call 
	 */
	@Override
	public void RefreshList() {

		/* play the refresh sound */
		playSound(SOUND_TYPE.PLAY_REFRESH);

		/* Flag value for refresh */
		_isRefreshed = true; 

		status = PULL_STATUS.PULL_BAR_VISIBLE;
		setProgressBarVisibility(true);

		/* Make call to UI listener */
		if(uiRefreshListener != null)
			uiRefreshListener.onRefreshed();
	}

	/**
	 *  Set the visibility of progressbar 
	 * @param visibility
	 */
	private void setProgressBarVisibility(boolean visibility)
	{
		if(visibility)
		{
			progressBar.setVisibility(View.VISIBLE);
			myView.setVisibility(View.INVISIBLE);
		}
		else
		{
			myView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 *  Check the progressbar visibility 
	 **/
	private boolean getProgressBarVisibility()
	{
		return progressBar.isShown();
	}

	/** 
	 * Internal listener method notified on refresh 
	 **/
	public void OnRefreshCompleted()
	{
		initList();
		clearTouch = true;

		/* Disable and enable touch to start touch event again from scratch*/
		listView.setOnTouchListener(null);
		listView.setOnTouchListener(this);

	}

}
