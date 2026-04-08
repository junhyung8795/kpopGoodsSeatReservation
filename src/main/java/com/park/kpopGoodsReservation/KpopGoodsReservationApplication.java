package com.park.kpopGoodsReservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KpopGoodsReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpopGoodsReservationApplication.class, args);
	}

}
