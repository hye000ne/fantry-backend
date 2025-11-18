package com.eneifour.fantry.inspection.dto.gemini;

import java.util.List;

/**
 * Gemini API 통신을 위한 DTO 모임
 */
public class GeminiDto {
    /**
     * Gemini 요청을 위한 데이터 구조
     * @param contents
     */
    public record Request (List<Content> contents) {
        public static Request of(String text, String base64Image, String mimeType){
            Part textPart = new Part(text, null);
            Part imagePart = new Part(null, new InlineData(mimeType, base64Image));
            return new Request(List.of(new Content(List.of(textPart, imagePart))));
        }
    }

    public record Content(List<Part> parts) {}
    public record Part(String text, InlineData inlineData) {}
    public record InlineData(String mime_type, String data) {}

    /**
     * Gemini 응답을 위한 데이터 구조
     * @param candidates
     */
    public record Response(List<Candidate> candidates) {
        public String getText(){
            if(candidates == null || candidates.isEmpty()) return "분석 결과 없음";
            return candidates.get(0).content.parts.get(0).text;

        }
    }

    public record Candidate(Content content) {}

}
