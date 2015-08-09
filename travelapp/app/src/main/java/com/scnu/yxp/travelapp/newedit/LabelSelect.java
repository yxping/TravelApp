package com.scnu.yxp.travelapp.newedit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scnu.yxp.travelapp.R;

public class LabelSelect extends Activity implements OnClickListener{
	private GridView gridView;
	private List<Drawable> drawableList ;
	private LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_label_choose);
		gridView = (GridView) findViewById(R.id.new_label_choose_grid);
		drawableList = new ArrayList<Drawable>();
		drawableList.add(getResources().getDrawable(R.drawable.new_label_select_landscape));
		drawableList.add(getResources().getDrawable(R.drawable.new_label_select_food));
		drawableList.add(getResources().getDrawable(R.drawable.new_label_select_life));
		layout = (LinearLayout) findViewById(R.id.new_label_choose_layout);
		gridView.setAdapter(new LabelAdapter());
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				setResult(position+1);
				finish();
			}
		});
		layout.setOnClickListener(this);
	}
	
	public class LabelAdapter extends BaseAdapter {
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(LabelSelect.this);

            i.setImageDrawable(drawableList.get(position));
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            final int w = (int) (36 * getResources().getDisplayMetrics().density + 0.5f);
            i.setLayoutParams(new GridView.LayoutParams(w, w));
            return i;
        }


        public final int getCount() {
            return drawableList.size();
        }

        public final Object getItem(int position) {
            return drawableList.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

	@Override
	public void onClick(View arg0) {
		finish();
	}
}
