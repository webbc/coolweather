package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtils;
import com.coolweather.app.util.Utility;

/**
 * 选择区域界面
 * 
 * @author Administrator
 * 
 */
public class ChooseAreaActivity extends Activity {
	private static final int LEVEL_PROVINCE = 0; // 选择省的级别
	private static final int LEVEL_CITY = 1;// 选择市的级别
	private static final int LEVEL_COUNTY = 2;// 选择市的级别
	private int currentLevel;// 当前选择的级别

	private List<Province> provinceList;// 省的数据
	private List<City> cityList;// 市的数据
	private List<County> countyList;// 区县的数据
	private List<String> dataList = new ArrayList<String>();// 显示的数据

	private TextView title_text;
	private ListView list_view;

	private CoolWeatherDB coolWeatherDb;// 数据库操作对象

	private Province selectProvince;// 当前选择的省份
	private City selectCity;// 当前选择的城市
	private ArrayAdapter<String> adapter;// 适配器
	private ProgressDialog progressDialog;// 进度条对话框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		title_text = (TextView) findViewById(R.id.title_text);
		list_view = (ListView) findViewById(R.id.list_view);
		// 获取库连接对象
		coolWeatherDb = CoolWeatherDB.getInstance(this);
		// 适配器对象
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		// 设置数据适配器
		list_view.setAdapter(adapter);
		// 给ListView设置数据适配器
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					// 点击了某省
					selectProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					// 点击了某市
					selectCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					// 点击了某县

				}
			}

		});
		queryProvinces();// 加载省级数据
	}

	/*
	 * 加载县级数据
	 */
	protected void queryCounties() {
		countyList = coolWeatherDb.loadCounties(selectCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectCity.getCityCode(), "county");
		}
	}

	/*
	 * 加载市级数据
	 */
	protected void queryCities() {
		cityList = coolWeatherDb.loadCities(selectProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectProvince.getProvinceCode(), "city");
		}
	}

	/*
	 * 加载省级数据，优先从数据库进行查询，如果没有的话再到服务器进行查询
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDb.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 请求服务器
	 * 
	 * @param code
	 * @param type
	 */
	private void queryFromServer(String code, final String type) {
		String address;
		// 根据code值来判断要请求的网络地址是多少
		if (TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		}
		showProgressDialog();// 显示进度条对话框
		// 加载网络
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// 如果请求服务器成功，保存数据到数据库中
				boolean result = false;
				if (type.equals("province")) {
					result = Utility.handleProvincesResponse(coolWeatherDb,
							response);
				} else if (type.equals("city")) {
					result = Utility.handleCitiesResponse(coolWeatherDb,
							response, selectProvince.getId());
				} else if (type.equals("county")) {
					result = Utility.handleCountiesResponse(coolWeatherDb,
							response, selectCity.getId());
				}
				// 如果保存成功，就进行显示操作
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();// 关闭进度条对话框
							if (type.equals("province")) {
								queryProvinces();
							} else if (type.equals("city")) {
								queryCities();
							} else if (type.equals("county")) {
								queryCounties();
							}
						}

					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 0)
								.show();
					}
				});
			}
		});

	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
