package exp.viswanath.dropper;

import android.view.View;
import android.widget.AdapterView;

/*
 * Author : Viswanath L
 *
 * viswanath.l@experionglobal.com
 *
 * Dec 10, 2013
 */

public interface OnItemClickListener {

	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id);
	
}

