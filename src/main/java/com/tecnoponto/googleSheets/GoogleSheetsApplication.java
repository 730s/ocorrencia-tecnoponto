package com.tecnoponto.googleSheets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoogleSheetsApplication {

	public static void main(String[] args) {
		System.setProperty("server.servlet.session.cookie.same-site", "none");
		System.setProperty("server.servlet.session.cookie.secure", "true");
		SpringApplication.run(GoogleSheetsApplication.class, args);
	}

}
