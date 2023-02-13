package com.example.seatchoice.config.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataShareBean <T> {

	private Map<String, List<T>> dataShareMap = new HashMap<>();

	public void addData(String key, T data) {
		List<T> dataList =
			dataShareMap.computeIfAbsent(key, k -> new ArrayList<>());
		dataList.add(data);
	}

	public List<T> getData(String key) {
		return dataShareMap.get(key);
	}

}
