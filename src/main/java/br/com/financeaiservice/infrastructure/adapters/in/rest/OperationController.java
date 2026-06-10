package br.com.financeaiservice.infrastructure.adapters.in.rest;

import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.infrastructure.adapters.in.http.request.OperationRequest;
import br.com.financeaiservice.infrastructure.adapters.in.http.response.OperationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
public class OperationController {

    private final PersistOperationUseCase persistOperationUseCase;

    @PostMapping("/create")
    public ResponseEntity<OperationResponse> persistOperation(@RequestBody OperationRequest request,
                                                              UriComponentsBuilder uriBuilder){
        var result = persistOperationUseCase.execute(request.toInput());

        URI  uri = uriBuilder
                .path("/operations/{id}")
                .buildAndExpand(result.operationId())
                .toUri();

        return ResponseEntity.created(uri).body(OperationResponse.from(result));
    }
}
