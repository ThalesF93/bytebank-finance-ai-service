package br.com.financeaiservice.infrastructure.adapters.in.rest;

import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.application.usecase.ProcessWhatsAppUseCase;
import br.com.financeaiservice.infrastructure.adapters.in.http.request.OperationRequest;
import br.com.financeaiservice.infrastructure.adapters.in.http.response.OperationResponse;
import br.com.financeaiservice.infrastructure.context.UserContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
@Slf4j
public class OperationController {

    private final PersistOperationUseCase persistOperationUseCase;
    private final ProcessWhatsAppUseCase whatsAppUseCase;
    private final UserContext userContext;


    @PostMapping("/create")
    public ResponseEntity<OperationResponse> persistOperation(@Valid @RequestBody OperationRequest request,
                                                              @NotBlank @RequestHeader("X-User-Id") String userId,
                                                              UriComponentsBuilder uriBuilder){

        var result = persistOperationUseCase.execute(request.toInput(), UUID.fromString(userId));

        URI  uri = uriBuilder
                .path("/operations/{id}")
                .buildAndExpand(result.operationId())
                .toUri();

        return ResponseEntity.created(uri).body(OperationResponse.from(result));
    }

    @PostMapping(value = "/whatsapp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> processWhatsapp(@RequestPart(value = "audio", required = false) MultipartFile audio,
                                                  @RequestPart(value = "text", required = false) String text,
                                                  @NotBlank @RequestHeader("X-User-Id") String userId) {

        userContext.setUserId(userId);

        String response = whatsAppUseCase.execute(audio, text);
        return ResponseEntity.ok(response);
    }

}
