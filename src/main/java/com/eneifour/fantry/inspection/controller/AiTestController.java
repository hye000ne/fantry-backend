package com.eneifour.fantry.inspection.controller;

import com.eneifour.fantry.inspection.service.AiInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test/ai")
@RequiredArgsConstructor
public class AiTestController {

    private final AiInspectionService aiInspectionService;

    @PostMapping("/inspect")
    public ResponseEntity<String> inspectImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 없습니다.");
        }

        // AI 분석 실행
        String result = aiInspectionService.analyzeImage(file);

        return ResponseEntity.ok(result);
    }
}
