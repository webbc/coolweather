package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {

	/**
	 * �����ʹ�����������ص�ʡ������
	 * 
	 * @param coolWeatherDb
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDb, String response) {
		String[] provinceArr = response.split(",");
		if (provinceArr != null && provinceArr.length > 0) {
			for (String string : provinceArr) {
				String[] provinceInfoArr = string.split("\\|");
				Province province = new Province();
				province.setProvinceCode(provinceInfoArr[0]);
				province.setProvinceName(provinceInfoArr[1]); // ���������������ݴ洢��Provinceʵ������
				coolWeatherDb.saveProvince(province);// ���浽���ݿ���
			}
			return true;
		}
		return false;
	}

	/**
	 * �����ʹ�����������صĳ�������
	 * 
	 * @param coolWeatherDb
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB coolWeatherDb, String response, int provinceId) {
		String[] cityArr = response.split(",");
		if (cityArr != null && cityArr.length > 0) {
			for (String string : cityArr) {
				String[] cityInfoArr = string.split("\\|");
				City city = new City();
				city.setProvinceId(provinceId);
				city.setCityCode(cityInfoArr[0]);
				city.setCityName(cityInfoArr[1]); // ���������������ݴ洢��Cityʵ������
				coolWeatherDb.saveCity(city);// ���浽���ݿ���
			}
			return true;
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص���������
	 * 
	 * @param coolWeatherDb
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCountiesResponse(
			CoolWeatherDB coolWeatherDb, String response, int cityId) {
		String[] countyArr = response.split(",");
		if (countyArr != null && countyArr.length > 0) {
			for (String string : countyArr) {
				String[] countyInfoArr = string.split("\\|");
				County county = new County();
				county.setCityId(cityId);
				county.setCountyCode(countyInfoArr[0]);
				county.setCountyName(countyInfoArr[1]); // ���������������ݴ洢��Cityʵ������
				coolWeatherDb.saveCounty(county);// ���浽���ݿ���
			}
			return true;
		}
		return false;
	}

	/**
	 * ��������
	 * 
	 * @param response
	 * @return
	 */
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherinfo.getString("city");
			String cityid = weatherinfo.getString("cityid");
			String img1 = weatherinfo.getString("img1");
			String img2 = weatherinfo.getString("img2");
			String ptime = weatherinfo.getString("ptime");
			String temp1 = weatherinfo.getString("temp1");
			String temp2 = weatherinfo.getString("temp2");
			String weather = weatherinfo.getString("weather");
			saveWeatherInfo(context, cityName, cityid, img1, img2, ptime,
					temp1, temp2, weather);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������������
	 * 
	 * @param context
	 * @param cityName
	 * @param cityid
	 * @param img1
	 * @param img2
	 * @param ptime
	 * @param temp1
	 * @param temp2
	 * @param weather
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String cityid, String img1, String img2, String ptime,
			String temp1, String temp2, String weather) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences sp = context.getSharedPreferences("weather",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("city_id", cityid);
		editor.putString("img1", img1);
		editor.putString("img2", img2);
		editor.putString("ptime", ptime);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather", weather);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
