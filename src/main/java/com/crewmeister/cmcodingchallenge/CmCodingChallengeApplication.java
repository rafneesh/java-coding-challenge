package com.crewmeister.cmcodingchallenge;

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
