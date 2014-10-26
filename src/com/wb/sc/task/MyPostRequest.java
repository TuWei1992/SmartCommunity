package com.wb.sc.task;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.ParamsEncryptRequest;
import com.wb.sc.bean.MyPost;
import com.wb.sc.config.RespCode;
import com.wb.sc.parser.BaseParser;
import com.wb.sc.parser.MyPostParser;

public class MyPostRequest extends ParamsEncryptRequest<MyPost> {
	public MyPostRequest (String url, List<String> params, 
			Listener<MyPost> listenre, ErrorListener errorListener) {
		super(url, params, listenre, errorListener);
	}
	
	@Override
	protected Response<MyPost> parseNetworkResponse(NetworkResponse response) {
		String resultStr = null;
		try {
			resultStr = new String(response.data, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		MyPost baseBean = new MyPost();
		BaseParser.parse(baseBean, resultStr);
		
		if(baseBean.respCode.equals(RespCode.SUCCESS)) {
			new MyPostParser().parse(baseBean);
		}
		
		return Response.success(baseBean, getCacheEntry());
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders(); 
		
		return headers;
	}
}
