package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtils;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener {
	private TextView tv_current_date, tv_ptime, tv_temp1, tv_temp2, city_name,
			tv_weather_desp;
	private LinearLayout weather_info_layout;
	private Button switch_city, refresh_weather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
		weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
		city_name = (TextView) findViewById(R.id.city_name);
		tv_ptime = (TextView) findViewById(R.id.tv_ptime);
		tv_current_date = (TextView) findViewById(R.id.tv_current_date);
		tv_weather_desp = (TextView) findViewById(R.id.tv_weather_desp);
		tv_temp1 = (TextView) findViewById(R.id.tv_temp1);
		tv_temp2 = (TextView) findViewById(R.id.tv_temp2);

		// ��ʼ����ť�����ü�����
		switch_city = (Button) findViewById(R.id.switch_city);
		refresh_weather = (Button) findViewById(R.id.refresh_weather);
		switch_city.setOnClickListener(this);
		refresh_weather.setOnClickListener(this);

		String countyCode = getIntent().getStringExtra("countyCode");
		if (!TextUtils.isEmpty(countyCode)) {
			// ���ؼ�����ʱ��ȥ��ѯ
			tv_ptime.setText("ͬ����...");
			weather_info_layout.setVisibility(View.INVISIBLE);
			city_name.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// û���ؼ����ž�ֱ����ʾ��������
			showWeather();
		}
	}

	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * 
	 * @param countyCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	/**
	 * ���ݴ���ĵ�ַ���������������ѯ�������Ż���������Ϣ
	 * 
	 * @param address
	 * @param string
	 */
	private void queryFromServer(String address, final String type) {
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String result) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(result)) {
						// �ӷ��������ص������н�������������
						String[] array = result.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					System.out.println(result);
					// ����json���ݲ����浽sp��
					Utility.handleWeatherResponse(WeatherActivity.this, result);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_ptime.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	/**
	 * ��sup�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		SharedPreferences sp = getSharedPreferences("weather", MODE_PRIVATE);
		city_name.setText(sp.getString("city_name", ""));
		tv_ptime.setText("����" + sp.getString("ptime", "") + "����");
		tv_current_date.setText(sp.getString("current_date", ""));
		tv_weather_desp.setText(sp.getString("weather", ""));
		tv_temp1.setText(sp.getString("temp1", ""));
		tv_temp2.setText(sp.getString("temp2", ""));
		weather_info_layout.setVisibility(View.VISIBLE);
		city_name.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			tv_ptime.setText("ͬ����...");
			SharedPreferences sp = getSharedPreferences("weather", MODE_PRIVATE);
			String weatherCode = sp.getString("city_id", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		}
	}

}
