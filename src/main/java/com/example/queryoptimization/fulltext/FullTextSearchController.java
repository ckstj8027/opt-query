package com.example.queryoptimization.fulltext;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FullTextSearchController {

    private final FullTextSearchService fullTextSearchService;

    public FullTextSearchController(FullTextSearchService fullTextSearchService) {
        this.fullTextSearchService = fullTextSearchService;
    }

    @GetMapping("/full-text-search")
    public String fullTextSearch(Model model) {
        long unoptimizedTime = fullTextSearchService.runUnoptimizedQuery();
        long optimizedTime = fullTextSearchService.runOptimizedQuery();

        model.addAttribute("optimizationType", "Full-Text Search vs. LIKE '%...%'");
        model.addAttribute("unoptimizedTime", unoptimizedTime);
        model.addAttribute("optimizedTime", optimizedTime);

        model.addAttribute("unoptimizedQuery", "SELECT * FROM articles WHERE content LIKE '%middle_keyword%'");
        model.addAttribute("unoptimizedExplanation", 
            "<code>LIKE '%...%'</code> 검색은 인덱스를 사용할 수 없어, 테이블의 모든 행을 처음부터 끝까지 스캔(Full Table Scan)해야 합니다. " +
            "데이터가 많아질수록 성능이 심각하게 저하됩니다.");

        model.addAttribute("optimizedQuery", "SELECT * FROM articles WHERE MATCH(content) AGAINST('+middle_keyword' IN BOOLEAN MODE)");
        model.addAttribute("optimizedExplanation", 
            "<b>Full-Text Index</b>는 텍스트 내용을 단어(Term) 기준으로 미리 색인화해 둡니다. <code>MATCH() AGAINST()</code> 구문은 이 인덱스를 사용하여 특정 단어가 포함된 문서를 매우 빠르게 찾아냅니다. " +
            "이는 <code>LIKE</code> 검색의 한계를 극복하는 가장 올바른 최적화 방법입니다.");

        return "results";
    }
}
