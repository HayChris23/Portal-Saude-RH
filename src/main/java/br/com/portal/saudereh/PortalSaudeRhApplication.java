package br.com.portal.saudereh;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PortalSaudeRhApplication {

    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));

        SpringApplication.run(PortalSaudeRhApplication.class, args);
    }

}