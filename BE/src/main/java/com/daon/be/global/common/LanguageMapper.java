package com.daon.be.global.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 국가코드를 언어명으로 변환하는 유틸리티 클래스
 */
public class LanguageMapper {
    
    private static final Map<String, String> NATION_TO_LANGUAGE = new HashMap<>();
    
    static {
        // 주요 국가코드와 언어명 매핑
        NATION_TO_LANGUAGE.put("BD", "Bengali");
        NATION_TO_LANGUAGE.put("BR", "Portuguese");
        NATION_TO_LANGUAGE.put("CN", "Chinese");
        NATION_TO_LANGUAGE.put("ID", "Indonesian");
        NATION_TO_LANGUAGE.put("IN", "Hindi");
        NATION_TO_LANGUAGE.put("JP", "Japanese");
        NATION_TO_LANGUAGE.put("KH", "Khmer");
        NATION_TO_LANGUAGE.put("KR", "Korean");
        NATION_TO_LANGUAGE.put("MM", "Burmese");
        NATION_TO_LANGUAGE.put("MN", "Mongolian");
        NATION_TO_LANGUAGE.put("MY", "Malay");
        NATION_TO_LANGUAGE.put("NP", "Nepali");
        NATION_TO_LANGUAGE.put("NG", "English");
        NATION_TO_LANGUAGE.put("PH", "Filipino");
        NATION_TO_LANGUAGE.put("PK", "Urdu");
        NATION_TO_LANGUAGE.put("RU", "Russian");
        NATION_TO_LANGUAGE.put("TH", "Thai");
        NATION_TO_LANGUAGE.put("US", "English");
        NATION_TO_LANGUAGE.put("UZ", "Uzbek");
        NATION_TO_LANGUAGE.put("VN", "Vietnamese");
    }
    
    /**
     * 국가코드를 언어명으로 변환
     * @param nationCode 국가코드 (예: "KR", "VN")
     * @return 언어명 (예: "Korean", "Vietnamese"), 매핑이 없으면 "English" 반환
     */
    public static String getLanguageFromNationCode(String nationCode) {
        if (nationCode == null || nationCode.trim().isEmpty()) {
            return "English"; // 기본값
        }
        
        String language = NATION_TO_LANGUAGE.get(nationCode.toUpperCase());
        return language != null ? language : "English"; // 매핑이 없으면 영어로 기본 설정
    }
}
