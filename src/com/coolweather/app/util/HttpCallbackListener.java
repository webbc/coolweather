package com.coolweather.app.util;

public interface HttpCallbackListener {
	public void onFinish(String result);

	public void onError(Exception e);
}
