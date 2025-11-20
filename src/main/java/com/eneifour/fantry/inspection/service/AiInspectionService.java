package com.eneifour.fantry.inspection.service;

import com.eneifour.fantry.checklist.domain.ChecklistItem;
import com.eneifour.fantry.inspection.dto.gemini.GeminiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiInspectionService {
    private final WebClient.Builder webClientBuilder;
    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    /**
     * 동적 프롬프트를 이용한 AI 검수 요청
     *
     * @param categoryName 카테고리 명 (예: 포토카드)
     * @param checklistItems 해당 카테고리의 체크리스트 항목 정보 (라벨, 키)
     * @param userAnswers 판매자가 작성한 답변 (Key:Value)
     * @param files 업로드된 이미지 파일들 (TODO: 현재는 첫 번째 이미지만 분석 예시)
     * @return AI 분석 결과 문자열 (JSON 형태 권장)
     */
    public String analyzeInspection(String categoryName,
                                    List<ChecklistItem> checklistItems,
                                    Map<String, String> userAnswers,
                                    List<MultipartFile> files) {
        try {
            // 1. 이미지 처리
            if(files == null || files.isEmpty()) return "이미지가 없습니다.";
            MultipartFile file = files.getFirst(); // TODO: 첫 번째 이미지만 분석
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = file.getContentType();

            // 2. 동적 프롬프트 생성
            String prompt = buildDynamicPrompt(categoryName, checklistItems, userAnswers);

            // 3. 요청 DTO 생성
            GeminiDto.Request requestPayload = GeminiDto.Request.of(prompt, base64Image, mimeType);
            String urlString = apiUrl + "?key=" + apiKey;

            // 4. WebClient API 호출
            GeminiDto.Response response = webClientBuilder.build()
                    .post()
                    .uri(URI.create(urlString))
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

    // 동적 프롬프트 생성
    private String buildDynamicPrompt(String categoryName, List<ChecklistItem> checklistItems, Map<String, String> userAnswers) {
        StringBuilder sb = new StringBuilder();

        // [역할 부여]
        sb.append("당신은 K-POP 굿즈 전문 검수 AI입니다. 다음 정보를 바탕으로 상품 상태를 정밀하게 분석해주세요.\n\n");
        // [카테고리 정보]
        sb.append(String.format("**[상품 카테고리]**\n%s\n\n", categoryName));

        sb.append("**[검수 항목 및 판매자 주장]**\n");
        for (ChecklistItem item : checklistItems) {

        }

        return sb.toString();
    }

}
