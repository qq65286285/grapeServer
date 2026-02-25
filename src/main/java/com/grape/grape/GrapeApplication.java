package com.grape.grape;

import com.grape.grape.service.QdrantSyncService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
@SpringBootApplication
@MapperScan(value = "com.grape.grape.mapper")
public class GrapeApplication {

	@Autowired
	private QdrantSyncService qdrantSyncService;

	public static void main(String[] args) {
		SpringApplication.run(GrapeApplication.class, args);
	}

	@Bean
	public CommandLineRunner initQdrantSync() {
		return args -> {
			System.out.println("=== 初始化 Qdrant 同步服务 ===");
			try {
				qdrantSyncService.init();
				System.out.println("✅ Qdrant 同步服务初始化成功");
			} catch (Exception e) {
				System.out.println("❌ Qdrant 同步服务初始化失败: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}

}
