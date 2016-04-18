package com.coolweather.app.util;

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
}
