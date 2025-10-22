package com.example.queryoptimization.pagination;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginationController {

    private final PaginationService paginationService;

    public PaginationController(PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    @GetMapping("/pagination")
    public String pagination(Model model) {
        long unoptimizedTime = paginationService.runUnoptimizedQuery();
        long optimizedTime = paginationService.runOptimizedQuery();

        model.addAttribute("optimizationType", "Pagination Optimization (Seek Method vs. Offset)");
        model.addAttribute("unoptimizedTime", unoptimizedTime);
        model.addAttribute("optimizedTime", optimizedTime);

        model.addAttribute("unoptimizedQuery", "SELECT * FROM users ORDER BY created_at DESC LIMIT 10 OFFSET 9990");
        model.addAttribute("unoptimizedExplanation", 
            "<b>목표:</b> 최신 가입자 순으로 정렬된 목록의 1000번째 페이지를 조회합니다.<br>" +
            "<b>문제:</b> <code>OFFSET</code>은 페이지 번호가 커질수록 심각한 성능 저하를 유발합니다. 원하는 10개를 가져오기 위해, 데이터베이스는 디스크에서 앞선 <b>9,990개</b>의 데이터를 모두 읽고 정렬한 후 버려야만 합니다.");

        model.addAttribute("optimizedQuery", "SELECT * FROM users WHERE (created_at, id) < (?, ?) ORDER BY created_at DESC, id DESC LIMIT 10");
        model.addAttribute("optimizedExplanation", 
            "<b>해결:</b> No-Offset 방식(Seek Method)은 마지막 페이지의 마지막 데이터(커서)를 기준으로 다음 데이터를 찾습니다. <code>WHERE (createdAt, id) < (마지막_createdAt, 마지막_id)</code> 조건을 사용하면, 데이터베이스는 인덱스를 사용해 시작 위치로 즉시 점프할 수 있습니다. " +
            "불필요한 데이터 스캔을 완전히 제거하므로 페이지 번호에 상관없이 항상 일정한 성능을 보장합니다.");

        return "results";
    }
}
