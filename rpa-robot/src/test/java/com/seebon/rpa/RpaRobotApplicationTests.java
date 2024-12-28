package com.seebon.rpa;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RpaRobotApplicationTests {

	@Test
	void contextLoads() {
		System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\webdriver\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("debuggerAddress","172.0.0.1:1688");
		WebDriver driver = new ChromeDriver(options);
	}
}
