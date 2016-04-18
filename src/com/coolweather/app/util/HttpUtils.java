package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 网络请求帮助类
 * 
 * @author Administrator
 * 
 */
public class HttpUtils {

	/**
	 * 发送请求
	 * 
	 * @param address地址
	 * @param listener回调接口
	 */
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread() {
			private HttpURLConnection conn = null;

			public void run() {
				try {
					URL url = new URL(address);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					conn.setConnectTimeout(5000);
					conn.connect();
					if (conn.getResponseCode() == 200) {
						InputStream inputStream = conn.getInputStream();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(inputStream));
						String line = "";
						StringBuilder sb = new StringBuilder();
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						if (listener != null) {
							listener.onFinish(sb.toString());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					listener.onError(e);
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
			};
		}.start();
	}

}
