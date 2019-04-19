package com.ipiecoles.java.java350.acceptance;

import com.ipiecoles.java.java350.repository.EmployeRepository;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(Cucumber.class)
@CucumberOptions(glue = {"com.ipiecoles.java.java350.acceptance", "cucumber.api.spring"}, plugin = {"pretty", "html:target/cucumber"}, features = "src/test/resources")
public class RunAcceptanceTest {

}
