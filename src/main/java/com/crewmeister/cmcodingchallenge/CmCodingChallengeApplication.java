package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.service.CurrencyDataFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
public class CmCodingChallengeApplication {

	public static void main(String[] args) {

		System.setProperty("user.timezone", "GMT");

		SpringApplication.run(CmCodingChallengeApplication.class, args);
	}

}
