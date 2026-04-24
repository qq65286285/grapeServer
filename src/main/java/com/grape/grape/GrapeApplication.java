package com.grape.grape;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;

@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
@SpringBootApplication
@MapperScan(value = "com.grape.grape.mapper")
public class GrapeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrapeApplication.class, args);
	}

}
