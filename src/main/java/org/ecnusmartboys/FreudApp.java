package org.ecnusmartboys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@SpringBootApplication(scanBasePackages = {"org.ecnusmartboys"})
@ServletComponentScan
@EnableCaching
public class FreudApp {
    public static void main(String[] args) {
        SpringApplication.run(FreudApp.class, args);
    }

    @RequestMapping
    public String hello() {
        return "Hello Freud";
    }
}