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
 * ѡ���������
 * 
 * @author Administrator
 * 
 */
public class ChooseAreaActivity extends Activity {
	private static final int LEVEL_PROVINCE = 0; // ѡ��ʡ�ļ���
	private static final int LEVEL_CITY = 1;// ѡ���еļ���
	private static final int LEVEL_COUNTY = 2;// ѡ���еļ���
	private int currentLevel;// ��ǰѡ��ļ���

	private List<Province> provinceList;// ʡ������
	private List<City> cityList;// �е�����
	private List<County> countyList;// ���ص�����
	private List<String> dataList = new ArrayList<String>();// ��ʾ������

	private TextView title_text;
	private ListView list_view;

	private CoolWeatherDB coolWeatherDb;// ���ݿ��������

	private Province selectProvince;// ��ǰѡ���ʡ��
	private City selectCity;// ��ǰѡ��ĳ���
	private ArrayAdapter<String> adapter;// ������
	private ProgressDialog progressDialog;// �������Ի���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		title_text = (TextView) findViewById(R.id.title_text);
		list_view = (ListView) findViewById(R.id.list_view);
		// ��ȡ�����Ӷ���
		coolWeatherDb = CoolWeatherDB.getInstance(this);
		// ����������
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		// ��������������
		list_view.setAdapter(adapter);
		// ��ListView��������������
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					// �����ĳʡ
					selectProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					// �����ĳ��
					selectCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					// �����ĳ��

				}
			}

		});
		queryProvinces();// ����ʡ������
	}

	/*
	 * �����ؼ�����
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
	 * �����м�����
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
	 * ����ʡ�����ݣ����ȴ����ݿ���в�ѯ�����û�еĻ��ٵ����������в�ѯ
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
			title_text.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * ���������
	 * 
	 * @param code
	 * @param type
	 */
	private void queryFromServer(String code, final String type) {
		String address;
		// ����codeֵ���ж�Ҫ����������ַ�Ƕ���
		if (TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		}
		showProgressDialog();// ��ʾ�������Ի���
		// ��������
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// �������������ɹ����������ݵ����ݿ���
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
				// �������ɹ����ͽ�����ʾ����
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();// �رս������Ի���
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", 0)
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
			progressDialog.setMessage("���ڼ���...");
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
