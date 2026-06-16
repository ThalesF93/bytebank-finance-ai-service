package br.com.financeaiservice.infrastructure.client.response;

import java.util.UUID;

public record AccountResponse(
                                UUID id,


                                String name,


                                String email) {

}
