package com.li.bookworm.runner;

import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features", glue = { "classpath:com.li.bookworm.stepdefs" }, tags = "@BookwormTest", plugin = { "pretty", "junit:target/cucumber-reports/bookworm-test-report.xml" })
public class AppIntegrationTestsRunner {
}
