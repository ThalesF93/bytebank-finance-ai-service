package br.com.financeaiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FinanceAiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceAiServiceApplication.class, args);
    }

}
