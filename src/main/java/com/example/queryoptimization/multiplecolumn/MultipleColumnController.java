package com.example.queryoptimization.multiplecolumn;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MultipleColumnController {

    private final MultipleColumnService multipleColumnService;

    public MultipleColumnController(MultipleColumnService multipleColumnService) {
        this.multipleColumnService = multipleColumnService;
    }

    @GetMapping("/multiple-column-index")
    public String multipleColumnIndex(Model model) {
        long unoptimizedTime = multipleColumnService.runUnoptimizedQuery();
        long optimizedTime = multipleColumnService.runOptimizedQuery();

        model.addAttribute("optimizationType", "Multiple-Column Index Optimization");
        model.addAttribute("unoptimizedTime", unoptimizedTime);
        model.addAttribute("optimizedTime", optimizedTime);

        model.addAttribute("unoptimizedQuery", "SELECT u FROM User u WHERE CONCAT(u.lastName, u.firstName) = ?");
        model.addAttribute("unoptimizedExplanation", 
            "<b>목표:</b> `lastName`과 `firstName`으로 사용자를 찾는 것은 동일합니다.<br>" +
            "<b>문제:</b> <code>WHERE</code> 절에서 인덱스가 걸린 컬럼을 <code>CONCAT</code> 함수로 가공했습니다. 데이터베이스는 가공된 결과값이 인덱스에 어떻게 저장되어 있는지 알 수 없으므로, 결국 <code>(lastName, firstName)</code> 복합 인덱스를 포기하고 전체 테이블을 스캔(Full Table Scan)합니다.");

        model.addAttribute("optimizedQuery", "SELECT u FROM User u WHERE u.lastName = ? AND u.firstName = ?");
        model.addAttribute("optimizedExplanation", 
            "<b>해결:</b> <code>WHERE</code> 절에서 각 컬럼을 가공하지 않고 개별적으로 비교합니다. " +
            "이를 통해 데이터베이스는 <code>(lastName, firstName)</code> 복합 인덱스를 효율적으로 사용하여 원하는 데이터를 빠르게 찾을 수 있습니다.");

        return "results";
    }
}
