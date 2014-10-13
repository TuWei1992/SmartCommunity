package com.wb.sc.task;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.ParamsRequest;
import com.wb.sc.config.DebugConfig;
import com.wb.sc.parser.BaseParser;
import com.wb.sc.bean.BaseBean;

public class BaseRequest extends ParamsRequest<BaseBean> {
	public BaseRequest (String url, List<String> params, 
			Listener<BaseBean> listenre, ErrorListener errorListener) {
		super(url, params, listenre, errorListener);
	}
	
	@Override
	protected Response<BaseBean> parseNetworkResponse(NetworkResponse response) {
		String resultStr = null;
		try {
			resultStr = new String(response.data, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseBean baseBean = new BaseBean();
		BaseParser.parse(baseBean, resultStr);
		return Response.success(baseBean, getCacheEntry());
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders(); 
		
		return headers;
	}
}
