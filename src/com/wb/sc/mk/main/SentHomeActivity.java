package com.wb.sc.mk.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wb.sc.R;
import com.wb.sc.activity.base.BaseActivity;
import com.wb.sc.activity.base.ReloadListener;
import com.wb.sc.adapter.SentHomeAdpater;
import com.wb.sc.app.SCApp;
import com.wb.sc.bean.OneKm;
import com.wb.sc.bean.OneKm.MerchantItem;
import com.wb.sc.bean.PostType;
import com.wb.sc.bean.SentHome;
import com.wb.sc.config.NetConfig;
import com.wb.sc.config.RespCode;
import com.wb.sc.task.OneKmRequest;
import com.wb.sc.task.PostTypeRequest;
import com.wb.sc.util.Constans;
import com.wb.sc.util.MetaUtil;
import com.wb.sc.util.ParamsUtil;

public class SentHomeActivity extends BaseActivity implements OnMenuItemClickListener, Listener<OneKm>, 
ErrorListener, ReloadListener{

	private PullToRefreshListView mPullToRefreshListView;
	private SentHomeAdpater mAdpter;
	
	private String mKeyword;
	private String sId;
	
	private int pageNo;
	private int pageSize = 10;
	private boolean hasNextPage;
	private String mDistrictName;
	
	private OneKmRequest mOneKmRequest;
	
	private List<MerchantItem> list = new ArrayList<MerchantItem>();
	
	private Spinner mSpinner;
	private Spinner mDistanceSpinner;
	
	public String longitude;   // 经度
	public String latitude;     // 维度
	public String merchantCategoryId;  // 商户类别
	private String merchantName;
	private String distance ="";
	
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="gcj02";
	
	//发帖类型
	private PostTypeRequest mPostTypeRequest;
	private PostTypeListener mPostTypeListener = new PostTypeListener();
	private DistanceTypeListener distanceTypeListener = new DistanceTypeListener();
	
	private PostType mPostType;
	private PostType mDistanceType;
	private String[] types;
	private String[] dis;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sent_home);
		getIntentData();
		initView();
		
		getGps();
		
		showLoading();
		
	    requestPostType(getPostTypeRequestParams("SHOPPING_TYPE"), mPostTypeListener, this);
	    
	    requestPostType(getPostTypeRequestParams("SHOPPING_DISTANCE"), distanceTypeListener, this);
		
//		requestBase(getBaseRequestParams(), this, this);
	}
	
	@Override
	protected void onStop() {
		mLocationClient.stop();
		super.onStop();
	}
	
	
	/**
	 * 获取帖子分类请求参数
	 * @return
	 */
	private List<String> getPostTypeRequestParams(String type) {
		List<String> params = new ArrayList<String>();
		params.add(ParamsUtil.getReqParam("FG21", 4));
		params.add(ParamsUtil.getReqParam("MC_CENTERM", 16));
		params.add(ParamsUtil.getReqParam("00001", 20));
		params.add(ParamsUtil.getReqParam(type, 64));
		params.add(ParamsUtil.getReqParam("", 64));
		params.add(ParamsUtil.getReqParam("", 64));
		params.add(ParamsUtil.getReqIntParam(1, 3));
		params.add(ParamsUtil.getReqIntParam(10, 2));
		return params;
	}
	
	/**
	 * 执行任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestPostType(List<String> params,	 
			Listener<PostType> listenre, ErrorListener errorListener) {			
//		if(mPostTypeRequest != null) {
//			mPostTypeRequest.cancel();
//		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL;
		mPostTypeRequest = new PostTypeRequest(url, params, listenre, errorListener);
		startRequest(mPostTypeRequest);		
	}
	

    
    /**
	 * 
	 * @描述：帖子分类监听
	 * @作者：liang bao xian
	 * @时间：2014年10月27日 上午8:51:09
	 */
	class PostTypeListener implements Listener<PostType>{
		/**
		 * 请求完成，处理UI更新
		 */
		@Override
		public void onResponse(PostType response) {		
//			showContent();	
			if(response.respCode.equals(RespCode.SUCCESS)) {			
				mPostType = response;
				types = new String[mPostType.datas.size()/* + 1*/];
//				types[0] = "全部"; 
				for(int i=0; i< mPostType.datas.size() ; i++) {
					types[i /*+ 1*/] = mPostType.datas.get(i).name;								
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SentHomeActivity.this, 
		    			R.layout.spinner_text_layout, types);
		    	adapter.setDropDownViewResource(R.layout.spinner_down_text_layout);
		    	mSpinner.setAdapter(adapter);
		    	mSpinner.setSelection(0, false);
//		    	adapter.notifyDataSetChanged();

			} else {
				ToastHelper.showToastInBottom(SentHomeActivity.this, response.respCodeMsg);
			}
		}
	}
	
	
    /**
	 * 
	 * @描述：帖子分类监听
	 * @作者：liang bao xian
	 * @时间：2014年10月27日 上午8:51:09
	 */
	class DistanceTypeListener implements Listener<PostType>{
		/**
		 * 请求完成，处理UI更新
		 */
		@Override
		public void onResponse(PostType response) {		
//			showContent();	
			if(response.respCode.equals(RespCode.SUCCESS)) {			
				mDistanceType = response;
				dis = new String[mDistanceType.datas.size() /*+ 1*/];
//				dis[0] = "全部"; 
				for(int i=0; i< mDistanceType.datas.size() ; i++) {
					dis[i /*+ 1*/] = mDistanceType.datas.get(i).name;								
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SentHomeActivity.this, 
		    			R.layout.spinner_text_layout, dis);
		    	adapter.setDropDownViewResource(R.layout.spinner_down_text_layout);
		    	mDistanceSpinner.setAdapter(adapter);
		    	mDistanceSpinner.setSelection(0, false);
//		    	adapter.notifyDataSetChanged();

			} else {
				ToastHelper.showToastInBottom(SentHomeActivity.this, response.respCodeMsg);
			}
		}
	}
	
	private void getGps() {
		mLocationClient = SCApp.getInstance().mLocationClient;
		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation arg0) {
				mLocationClient.stop();
				longitude = arg0.getLongitude() + "";
				latitude = arg0.getLatitude() + "";
				mLocationClient.unRegisterLocationListener(this);
				mLocationClient.stop();
				requestBase(getBaseRequestParams(), SentHomeActivity.this, SentHomeActivity.this);
			}
			
		});
		InitLocation();
		mLocationClient.start();
	}
	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
		int span=1000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void back (View view) {
		finish();
     }

	public void initView() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_scroll);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				new GetDataTask().execute();
				pageNo = 1;
				list.clear();
				requestBase(getBaseRequestParams(), SentHomeActivity.this, SentHomeActivity.this);
			}
		});
		
		mPullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				requestBase(getBaseRequestParams(), SentHomeActivity.this, SentHomeActivity.this);
			}
		});
		
		
		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SentHomeActivity.this, SentHomeDetailActivity.class);
				intent.putExtra("merchantId", list.get(position -1).merchantId);  //此处还未查明原因
				intent.putExtra("merchantTel", list.get(position -1).merchantTel);  //此处还未查明原因
				intent.putExtra("merchantName", list.get(position -1).merchantName);
				startActivity(intent);
			}
		});
		
		
		initData();
		mAdpter = new SentHomeAdpater(SentHomeActivity.this, list);
		mPullToRefreshListView.setDividerDrawable(null);
		mPullToRefreshListView.setAdapter(mAdpter);
		
	      // 初始化控件
		mSpinner = (Spinner) findViewById(R.id.spinner1);
		// 建立数据源
//		String[] mItems = getResources().getStringArray(R.array.spinnername);
//		// 建立Adapter并且绑定数据源
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
//    			R.layout.spinner_text_layout, mItems);
//    	adapter.setDropDownViewResource(R.layout.spinner_down_text_layout);
//		mSpinner.setAdapter(adapter);
		mSpinner.setSelection(0, false);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
		  		showLoading();
        		pageNo = 1;
        		list.clear();
        		mAdpter.notifyDataSetChanged();
        		
        		int postion = arg2 > 0 ? arg2 -1 : arg2;
        		postion = arg2; //突然又不加全部
        		merchantCategoryId = mPostType.datas.get(postion).id;
        		if (longitude != null) {
        			requestBase(getBaseRequestParams(), SentHomeActivity.this, SentHomeActivity.this);
        		}
        		
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// 初始化控件
		mDistanceSpinner = (Spinner) findViewById(R.id.spinner2);
		// 建立数据源
//		final String[] distances = getResources().getStringArray(R.array.spinner_distance);
//		// 建立Adapter并且绑定数据源
//		ArrayAdapter<String> distanceAdapter = new ArrayAdapter<String>(this, 
//				R.layout.spinner_text_layout, distances);
//		distanceAdapter.setDropDownViewResource(R.layout.spinner_down_text_layout);
//		mDistanceSpinner.setAdapter(distanceAdapter);
		mDistanceSpinner.setSelection(0, false);
		mDistanceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
//				merchantCategoryId = "" + (arg2);
		  		showLoading();
        		pageNo = 1;
        		list.clear();
        		mAdpter.notifyDataSetChanged();
        		int postion = arg2 > 0 ? arg2 -1 : arg2;
        		postion = arg2; //突然又不加全部
        		distance = mDistanceType.datas.get(postion).id;
        		if (longitude != null) {
        			requestBase(getBaseRequestParams(), SentHomeActivity.this, SentHomeActivity.this);
        		}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final EditText input_content = (EditText) findViewById(R.id.input_content);
		input_content.setOnEditorActionListener(new OnEditorActionListener() {  
            @Override  
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
            	if (actionId == EditorInfo.IME_ACTION_SEARCH ) {
            		
            		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
            		imm.showSoftInput(input_content,InputMethodManager.SHOW_FORCED);  
            		imm.hideSoftInputFromWindow(input_content.getWindowToken(), 0); //强制隐藏键盘  
            		
            		merchantName = input_content.getText() +"";
            		showLoading();
            		pageNo = 1;
            		list.clear();
            		requestBase(getBaseRequestParams(), SentHomeActivity.this, SentHomeActivity.this);
            		return true;
            	} 
                return false;  
            }  
        }); 
		
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mAdpter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullToRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	private void initData() {
		String [] name = {"缇斯西饼(洪山桥)", "安德鲁森(洪山桥太阳城店)", "陌上花开(仓山店)", "比哥鸡排", "那时花开"};
		String []  category = {"餐饮", "餐饮", "花店", "餐饮", "花店"};
		String []  distance = {"100米", "100米", "100米", "100米", "100米"};
		int [] resId = {R.drawable.xibing, R.drawable.mianbao, R.drawable.huadian, R.drawable.jipai, R.drawable.nashihuadian};
		for (int i = 0; i < resId.length; i++) {
			SentHome sentHome = new SentHome();
			sentHome.name = name [i];
			sentHome.category = category [i];
			sentHome.resId = resId [i];
			sentHome.distance = distance[i];
//			list.add(sentHome);
		}
 	}


	public void getIntentData() {
		Intent intent = getIntent();
		mKeyword = intent.getStringExtra("mKeyword");
		pageNo = 1;
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 执行任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestBase(List<String> paramsList,	 
			Listener<OneKm> listenre, ErrorListener errorListener) {
		if(mOneKmRequest != null) {
			mOneKmRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL;
		mOneKmRequest = new OneKmRequest(url, paramsList, this, this);
		startRequest(mOneKmRequest);		
	}
	
	/**
	 * 获取请求参数,请按照接口文档列表顺序排列
	 * @return
	 */
	private List<String> getBaseRequestParams() {
		List<String> params = new ArrayList<String>();
		params.add(ParamsUtil.getReqParam("FG46", 4));
		params.add(ParamsUtil.getReqParam("MC_CENTERM", 16));
		params.add(ParamsUtil.getReqParam(MetaUtil.readMeta(this, Constans.APP_CHANNEL), 20));
		String categoryName = merchantName == null ? "":merchantName;
		params.add(ParamsUtil.getReqParam(categoryName, 32));
		params.add(ParamsUtil.getReqParam(longitude, 128));
		params.add(ParamsUtil.getReqParam(latitude, 128));
		params.add(ParamsUtil.getReqParam(distance, 32));
		String categoryId = merchantCategoryId == null ? "":merchantCategoryId;
		params.add(ParamsUtil.getReqParam(categoryId, 32));
		params.add(ParamsUtil.getReqIntParam(pageNo, 3));
		params.add(ParamsUtil.getReqIntParam(pageSize, 2));
		
		return params;
	}


	@Override
	public void onReload() {
		showLoading();
		requestBase(getBaseRequestParams(), this, this);
	}


	@Override
	public void onErrorResponse(VolleyError error) {
		showContent();	
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(this, error));
	}


	@Override
	public void onResponse(OneKm response) {
		if(response.respCode.equals(RespCode.SUCCESS)) {
			if (response.totalNum > 0) {
				pageNo ++;
			}
			
			if (response.totalNum == 0) {
				ToastHelper.showToastInBottom(SentHomeActivity.this, "查询不到相关数据");
				showContent();
				return;
			}

			list.addAll(response.datas);
			mAdpter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullToRefreshListView.onRefreshComplete();
			if (!response.hasNextPage) {
				mPullToRefreshListView.setMode(Mode.DISABLED);
			}
			showContent();
		} else {
			showLoadError(this);
			ToastHelper.showToastInBottom(this, response.respCodeMsg);
		}
		
	}
	

}
