package com.ternak.sapi;

import com.ternak.sapi.config.PathConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackageClasses = {
		TernakSapiApplication.class,
})
public class TernakSapiApplication {

	@Value("${hdfs.user}")
	private String userr;
	
	@PostConstruct
	void init() {
		System.setProperty("HADOOP_USER_NAME", userr);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		PathConfig.createStoragePathIfNotExists();
	}

	public static void main(String[] args) {
		SpringApplication.run(TernakSapiApplication.class, args);
	}
}
