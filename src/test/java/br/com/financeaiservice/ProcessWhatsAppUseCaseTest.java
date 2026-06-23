package br.com.financeaiservice;

import br.com.financeaiservice.application.service.LLMResponseService;
import br.com.financeaiservice.application.service.TranscriptionService;
import br.com.financeaiservice.application.usecase.ProcessWhatsAppUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessWhatsAppUseCaseTest {

    @Mock
    private TranscriptionService transcriptionService;

    @Mock
    private LLMResponseService llmResponseService;

    @InjectMocks
    private ProcessWhatsAppUseCase useCase;

    @Test
    void shouldTranscribeAudioAndCallLLM_whenAudioIsProvided() {
        MultipartFile audio = mock(MultipartFile.class);
        String transcribed = "gastei 50 reais no mercado";
        String expectedResponse = "Transação registrada com sucesso!";

        when(transcriptionService.transcribe(audio)).thenReturn(transcribed);
        when(llmResponseService.llmResponse(transcribed)).thenReturn(expectedResponse);

        String result = useCase.execute(audio, null);

        assertEquals(expectedResponse, result);
        verify(transcriptionService).transcribe(audio);
        verify(llmResponseService).llmResponse(transcribed);
    }

    @Test
    void shouldSkipTranscriptionAndCallLLMDirectly_whenOnlyTextIsProvided() {
        String text = "quanto gastei esse mês?";
        String expectedResponse = "Você gastou R$ 350 esse mês.";

        when(llmResponseService.llmResponse(text)).thenReturn(expectedResponse);

        String result = useCase.execute(null, text);

        assertEquals(expectedResponse, result);
        verifyNoInteractions(transcriptionService);
        verify(llmResponseService).llmResponse(text);
    }
}