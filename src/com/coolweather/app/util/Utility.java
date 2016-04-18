package com.coolweather.app.util;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
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
				province.setProvinceName(provinceInfoArr[1]); // 将解析出来的数据存储到Province实体类中
				coolWeatherDb.saveProvince(province);// 保存到数据库中
			}
			return true;
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的城市数据
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
				city.setCityName(cityInfoArr[1]); // 将解析出来的数据存储到City实体类中
				coolWeatherDb.saveCity(city);// 保存到数据库中
			}
			return true;
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的区县数据
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
				county.setCountyName(countyInfoArr[1]); // 将解析出来的数据存储到City实体类中
				coolWeatherDb.saveCounty(county);// 保存到数据库中
			}
			return true;
		}
		return false;
	}
}
