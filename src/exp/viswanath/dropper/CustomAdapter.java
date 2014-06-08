package exp.viswanath.dropper;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 
 * Author: Viswanath L
 * 
 * viswanath.l@experionglobal.com
 * 
 * 08-Dec-2013
 *
 */

public class CustomAdapter extends ArrayAdapter<String>{

private final Activity context;
private final String[] web;
private final Integer[] imageId;


public CustomAdapter(Activity context, String[] web, Integer[] imageId) {
super(context, R.layout.custom_list_new, web);
this.context = context;
this.web = web;
this.imageId = imageId;

}

@Override
public View getView(int position, View view, ViewGroup parent) {
LayoutInflater inflater = context.getLayoutInflater();
View rowView= inflater.inflate(R.layout.custom_list_new, null, true);

TextView txtTitle = (TextView) rowView.findViewById(R.id.description);

ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
txtTitle.setText(web[position]);
Resources res = context.getResources();
txtTitle.setTextColor(res.getColor(R.color.darkGray));

imageView.setImageResource(imageId[position]);
return rowView;
}
}