package com.eneifour.fantry.inspection.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Gemini API 통신을 위한 DTO 모임
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeminiDto {
    /**
     * Gemini 요청을 위한 데이터 구조
     * @param contents
     */
    public record Request (List<Content> contents) {
        public static Request of(String text, String base64Image, String mimeType){
            Part textPart = new Part(text, null);
            Part imagePart = new Part(null, new InlineData(mimeType, base64Image));
            return new Request(List.of(new Content("user", List.of(textPart, imagePart))));
        }
    }

    public record Content(String role, List<Part> parts) {}
    public record Part(String text, @JsonProperty("inline_data") InlineData inlineData) {}
    public record InlineData(@JsonProperty("mime_type") String mimeType, String data) {}

    /**
     * Gemini 응답을 위한 데이터 구조
     * @param candidates
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(List<Candidate> candidates) {
        public String getText() {
            if (candidates == null || candidates.isEmpty()) return "분석 결과 없음";
            Candidate firstCandidate = candidates.getFirst();
            // 안전한 Null 체크
            if (firstCandidate.content() == null ||
                    firstCandidate.content().parts() == null ||
                    firstCandidate.content().parts().isEmpty()) {
                return "텍스트 응답 없음";
            }
            return firstCandidate.content().parts().getFirst().text();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(Content content) {}

}
