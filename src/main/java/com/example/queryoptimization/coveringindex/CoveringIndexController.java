package com.example.queryoptimization.coveringindex;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoveringIndexController {

    private final CoveringIndexService coveringIndexService;

    public CoveringIndexController(CoveringIndexService coveringIndexService) {
        this.coveringIndexService = coveringIndexService;
    }

    @GetMapping("/covering-index")
    public String coveringIndex(Model model) {
        long unoptimizedTime = coveringIndexService.runUnoptimizedQuery();
        long optimizedTime = coveringIndexService.runOptimizedQuery();

        model.addAttribute("optimizationType", "Covering Index Optimization");
        model.addAttribute("unoptimizedTime", unoptimizedTime);
        model.addAttribute("optimizedTime", optimizedTime);

        model.addAttribute("unoptimizedQuery", "SELECT u.* FROM User u WHERE u.firstName = ?");
        model.addAttribute("unoptimizedExplanation", 
            "<b>목표:</b> `firstName`으로 사용자를 찾아 `lastName` 목록을 얻는 것은 동일합니다.<br>" +
            "<b>문제:</b> <code>SELECT *</code>를 사용하여, 최종 목표에 필요하지 않은 <code>id</code>, <code>created_at</code> 등의 모든 컬럼을 함께 조회합니다. <code>(firstName, lastName)</code> 인덱스에는 이 모든 정보가 없으므로, 데이터베이스는 인덱스에서 주소를 찾은 후 데이터 파일에 추가로 접근하여 나머지 데이터를 읽어옵니다. 이 과정에서 불필요한 디스크 I/O가 발생합니다.");

        model.addAttribute("optimizedQuery", "SELECT u.lastName FROM User u WHERE u.firstName = ?");
        model.addAttribute("optimizedExplanation", 
            "<b>해결:</b> <code>SELECT</code> 절에서 목표에 필요한 <code>lastName</code> 컬럼만 명시적으로 요청합니다. " +
            "쿼리에 필요한 모든 컬럼(<code>firstName</code>, <code>lastName</code>)이 <code>idx_firstname_lastname</code> 인덱스에 포함되어 있으므로, 데이터베이스는 데이터 파일에 접근할 필요 없이 인덱스만 읽고 쿼리를 완료할 수 있습니다. 이를 <b>커버링 인덱스</b>라고 합니다.");

        return "results";
    }
}
