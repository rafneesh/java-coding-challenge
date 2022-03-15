package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.components.DataFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class CmCodingChallengeApplication {

	@Autowired
	DataFeed dataFeed;

	public static void main(String[] args) {
		SpringApplication.run(CmCodingChallengeApplication.class, args);
	}

}
