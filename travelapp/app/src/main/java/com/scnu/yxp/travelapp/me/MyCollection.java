package com.scnu.yxp.travelapp.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.adapter.HomePageListViewAdapter;
import com.scnu.yxp.travelapp.info.IntentKey;
import com.scnu.yxp.travelapp.info.PersonalInfo;
import com.scnu.yxp.travelapp.tool.httpUtil;
import com.scnu.yxp.travelapp.view.MyListView;
import com.scnu.yxp.travelapp.view.MyListView.MListViewListener;
import com.scnu.yxp.travelapp.view.MyListView.MoveBtnBarListener;

public class MyCollection extends Fragment {
	private MyListView listView;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	//homepage的适配器
	private HomePageListViewAdapter adapter;
	private View view;
	private String response;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x120)
			{
				adapter = new HomePageListViewAdapter(getActivity(), list, listView);
				listView.setAdapter(adapter);
			}
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null==view){
			view = inflater.inflate(R.layout.me_collection, container,false);
			listView = (MyListView)view.findViewById(R.id.me_collection_listView);
			listView.setPullRefreshEnable(true);
			listView.setPullLoadEnable(true);
			listView.setTopLayoutGone();
			listView.setMoveBtnBarListener(new MoveBtnBarListener() {
				public void onTextOut() {
				}
				public void onTextIn() {
				}
				public void onMove(float deltaY) {
				}
				public void onBtnShrink() {
				}
				public void onBtnEnlarge() {
				}
				public int getBtnMargin() {
					return 0;
				}
			});
			setListener();
			getContent();
	    }else{
            ViewGroup mViewGroup=(ViewGroup) view.getParent();
            mViewGroup.removeView(view);
	    }
		return view;
	}
	
	public void setListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Bundle bundle = new Bundle();
				bundle.putString("email", list.get(position).get("email"));
				bundle.putString("title", list.get(position).get("title"));
				bundle.putString("image", list.get(position).get("img_url_s"));
				bundle.putInt("position", position);
				Intent intent = new Intent(getActivity().getApplicationContext(), MyDairyGallery.class);
				intent.putExtras(bundle);
				getActivity().startActivityForResult(intent, IntentKey.MY_COLLECTION_DETEAL);
			}
		});
		listView.setMListViewListener(new MListViewListener() {
			
			@Override
			public void onRefresh() {
				
			}
			
			@Override
			public void onLoadMore() {
				
			}
		});
	}
	
	public void getContent(){
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=my_collect", 
						"email="+PersonalInfo.account);
				Log.i("MyDairyresponse", response);
				try {
					JSONTokener jsonPaser = new JSONTokener(response);
					jsonPaser.nextValue();
					JSONObject obj = (JSONObject)jsonPaser.nextValue();
					JSONArray jsonObjs = obj.getJSONArray("jsonarray");//new JSONObject(response).getJSONArray("jsonarray");
					list = new ArrayList<Map<String, String>>();
					Log.i("size",jsonObjs.length()+"");
					for(int i = 0; i < jsonObjs.length(); i++){ 
						Map<String, String> listitemn = new HashMap<String, String>();
						JSONObject jsonObj = jsonObjs.getJSONObject(i);
						listitemn.put("author",jsonObj.getString("author"));
						listitemn.put("img_url",jsonObj.getString("img_url_s"));
						listitemn.put("headImg",jsonObj.getString("img_head"));
						listitemn.put("date",jsonObj.getString("date"));
						listitemn.put("location",jsonObj.getString("location"));
						listitemn.put("title",jsonObj.getString("title"));
						listitemn.put("readTime", "100"+"次");
						listitemn.put("allDay","5"+"天");
						listitemn.put("id",jsonObj.getString("id"));
						listitemn.put("email",jsonObj.getString("email"));
						list.add(listitemn);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0x120);
			}});
		thread.start();
	}
	
	public void changeListAdapter(Intent data){
		list.get(data.getIntExtra("position", 0)).put("title", data.getStringExtra("title"));
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
}
