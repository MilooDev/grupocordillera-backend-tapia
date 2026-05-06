package com.grupocordillera.gc_bff_reportes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableFeignClients
@EnableCaching // <--- ¡Activa el superpoder de la memoria RAM!
public class GcBffReportesApplication {
	public static void main(String[] args) {
		SpringApplication.run(GcBffReportesApplication.class, args);
	}
}