package exp.viswanath.dropper;

/*
 * 
 * Author: Viswanath L
 * 
 * viswanath.l@experionglobal.com
 * 
 * 09-Dec-2013
 *
 */

public interface UiRefreshListener {

	/**
	 * <p>
	 *    This method will be called when the Refresh state begins.
	 *    @Note Should notify OnRefreshCompleted() after refresh proccess 
	 * </p>
	 **/
	public void onRefreshed();
	
}
