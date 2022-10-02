package io.kall.mattertoday;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "webcalguru", url = "${webcalguru.baseurl}")
public interface WebcalGuruClient {
	
	// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=name_days_finland
	// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=263
	@GetMapping("lataa_kalenteri?calendar_instance_id=263")
	@Cacheable(cacheNames = "webcalguru-cache", key = "#root.methodName")
	String getNimipaivatIcal();
	
	// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=holidays_good_to_know
	// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=180
	@GetMapping("lataa_kalenteri?calendar_instance_id=180")
	@Cacheable(cacheNames = "webcalguru-cache", key = "#root.methodName")
	String getHyvaTietaa();

	// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=holidays
	// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=52
	@GetMapping("lataa_kalenteri?calendar_instance_id=52")
	@Cacheable(cacheNames = "webcalguru-cache", key = "#root.methodName")
	String getPyhat();
	
	
	// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=holidays_funny_local
	// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=3082
	@GetMapping("lataa_kalenteri?calendar_instance_id=3082")
	@Cacheable(cacheNames = "webcalguru-cache", key = "#root.methodName")
	String getHauskatMerkkipaivat();
}
