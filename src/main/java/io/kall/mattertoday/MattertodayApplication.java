package io.kall.mattertoday;

import java.time.Duration;
import java.time.ZoneId;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.github.benmanes.caffeine.cache.Caffeine;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
@EnableScheduling
public class MattertodayApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MattertodayApplication.class, args);
	}
	
	@Bean
	public CacheManager webcalGuruCache() {
		CaffeineCacheManager mgr = new CaffeineCacheManager("webcalguru-cache");
		mgr.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(7)));
		return mgr;
	}
	
	@Bean
	public ZoneId timezone() {
		return ZoneId.of("Europe/Helsinki");
	}
	
}
