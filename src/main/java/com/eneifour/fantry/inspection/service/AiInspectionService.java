package com.eneifour.fantry.inspection.service;

import com.eneifour.fantry.inspection.dto.gemini.GeminiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiInspectionService {
    private final WebClient.Builder webClientBuilder;
    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    public String analyzeImage(MultipartFile file) {
        try {
            // 1. 이미지 파일을 Base64 문자열로 인코딩
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = file.getContentType();

            // 2. 프롬프트 설정
            String prompt = "이 이미지는 K-POP 아이돌 굿즈야. 이 상품의 종류(포토카드, 앨범 등)를 추측하고, 찢어짐, 오염, 스크래치 같은 하자가 있는지 자세히 분석해줘. 한국어로 답변해.";

            // 3. 요청 DTO 생성
            GeminiDto.Request requestPayload = GeminiDto.Request.of(prompt, base64Image, mimeType);

            // 4. WebClient로 API 호출
            GeminiDto.Response response = webClientBuilder.build()
                    .post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestPayload)
                    .retrieve()
                    .bodyToMono(GeminiDto.Response.class)
                    .block();

            // 5. 결과 반환
            return response != null ? response.getText() : "AI 응답 실패";

        } catch (IOException e) {
            log.error("이미지 인코딩 실패", e);
            return "이미지 처리 중 오류 발생";
        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            return "AI 분석 중 오류 발생: " + e.getMessage();
        }

    }



}
