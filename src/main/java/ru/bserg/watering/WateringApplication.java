package ru.bserg.watering;

import lombok.extern.log4j.Log4j2;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
@SpringBootApplication
@Log4j2
public class WateringApplication {

	public static void main(String[] args) {
		try {
			// Запуск сервера h2
			Server.createTcpServer(new String[0]).start();

			// Запуск spring boot
			SpringApplication.run(WateringApplication.class, args);

		} catch (Exception ex) {
			log.error(ex);
		}
	}

}
