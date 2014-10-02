package com.wb.sc.mk.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.common.widget.hzlib.HorizontalAdapterView.OnItemClickListener;
import com.wb.sc.R;
import com.wb.sc.activity.base.BasePhotoFragment;

public class PostFragment extends BasePhotoFragment implements OnItemClickListener{
	
	private Spinner typeSp;
	
	@Override
    public void onAttach(Activity activity) {
       super.onAttach(activity);
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {
       return setContentView(inflater, R.layout.fragment_post);
    }
 
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
      
       initView(view);
    }
   
    private void initView(View view) {
    	initPhoto(view);  
    	
    	typeSp = (Spinner) view.findViewById(R.id.type);
    	String[] types = getResources().getStringArray(R.array.post_type);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
    			R.layout.spinner_text_layout, types);
    	adapter.setDropDownViewResource(R.layout.spinner_down_text_layout);
    	typeSp.setAdapter(adapter);
    }
}
