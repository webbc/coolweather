package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * ���ݿ������װ��
 * 
 * @author Administrator
 * 
 */
public class CoolWeatherDB {
	// ���ݿ���
	public static final String DB_NAME = "cool_weather";
	// ���ݿ�汾
	public static final int VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * �����췽��˽�л�
	 * 
	 * @param context
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * ������ȡCoolWeather��ʵ��
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * ��Provinceʵ���洢�����ݿ���
	 * 
	 * @param province
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * �����ݿ��м������е�ʡ����Ϣ
	 * 
	 * @return
	 */
	public List<Province> loadProvinces() {
		List<Province> proviceLists = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Province province = new Province();
			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceName(cursor.getString(cursor
					.getColumnIndex("province_name")));
			province.setProvinceCode(cursor.getString(cursor
					.getColumnIndex("province_code")));
			proviceLists.add(province);
		}
		cursor.close();
		return proviceLists;
	}

	/**
	 * ��Cityʵ���洢�����ݿ���
	 * 
	 * @param province
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	/**
	 * �����ݿ��л�ȡĳʡ���еĳ�����Ϣ
	 * 
	 * @return
	 */
	public List<City> loadCities(int provinceId) {
		List<City> cityLists = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { provinceId + "" }, null, null, null);
		while (cursor.moveToNext()) {
			City city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor
					.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor
					.getColumnIndex("city_code")));
			city.setProvinceId(provinceId);
			cityLists.add(city);
		}
		cursor.close();
		return cityLists;
	}

	/**
	 * ��Countyʵ���洢�����ݿ���
	 * 
	 * @param province
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}

	/**
	 * �����ݿ��л�ȡĳ�����е�������Ϣ
	 * 
	 * @return
	 */
	public List<County> loadCounties(int cityId) {
		List<County> countyLists = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { cityId + "" }, null, null, null);
		while (cursor.moveToNext()) {
			County county = new County();
			county.setId(cursor.getInt(cursor.getColumnIndex("id")));
			county.setCountyName(cursor.getString(cursor
					.getColumnIndex("county_name")));
			county.setCountyCode(cursor.getString(cursor
					.getColumnIndex("county_code")));
			county.setCityId(cityId);
			countyLists.add(county);
		}
		cursor.close();
		return countyLists;
	}

}
