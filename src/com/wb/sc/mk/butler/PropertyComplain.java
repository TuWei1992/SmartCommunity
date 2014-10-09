package com.wb.sc.mk.butler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.common.widget.hzlib.HorizontalAdapterView.OnItemClickListener;
import com.wb.sc.R;
import com.wb.sc.activity.base.BasePhotoActivity;
import com.wb.sc.mk.personal.MyComplaintActivity;

public class PropertyComplain extends BasePhotoActivity implements OnItemClickListener{
	
	private View myComplainV;
	private Spinner typeSp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_complain);
		initHeader(getString(R.string.ac_property_complain));
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		myComplainV = findViewById(R.id.my_complain);
		myComplainV.setOnClickListener(this);
		initPhoto();
		
		typeSp = (Spinner) findViewById(R.id.type);
		String[] types = getResources().getStringArray(R.array.property_complain_type);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
    			R.layout.spinner_text_layout, types);
    	adapter.setDropDownViewResource(R.layout.spinner_down_text_layout);
    	typeSp.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch(v.getId()) {
		case R.id.my_complain:
			Intent intent = new Intent(this, MyComplaintActivity.class);
			startActivity(intent);
			break;
		}
	}
	
}
