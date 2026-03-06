package com.Glitch.browserIDE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BrowserIdeApplication {
	public static void main(String[] args) {
		SpringApplication.run(BrowserIdeApplication.class, args);
	}

}
