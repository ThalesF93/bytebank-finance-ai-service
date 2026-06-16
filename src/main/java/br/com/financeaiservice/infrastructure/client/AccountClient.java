package br.com.financeaiservice.infrastructure.client;

import br.com.financeaiservice.infrastructure.client.response.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "bytebank-accounts", path = "/api/v1/accounts")
public interface AccountClient {

    @GetMapping("/feign/customer/{id}")
    AccountResponse findCustomerByAccountId(@PathVariable UUID id);
}
