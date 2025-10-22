package com.example.queryoptimization.insert;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InsertController {

    private final InsertService insertService;

    public InsertController(InsertService insertService) {
        this.insertService = insertService;
    }

    @GetMapping("/insert")
    public String insert(Model model) {
        long unoptimizedTime = insertService.runUnoptimizedQuery();
        long optimizedTime = insertService.runOptimizedQuery();

        model.addAttribute("optimizationType", "INSERT Optimization (Batch vs. Single)");
        model.addAttribute("unoptimizedTime", unoptimizedTime);
        model.addAttribute("optimizedTime", optimizedTime);

        model.addAttribute("unoptimizedQuery", "// Loop 10,000 times:\nINSERT INTO products (...) VALUES (...);");
        model.addAttribute("unoptimizedExplanation", 
            "반복문 안에서 <code>save()</code>를 호출하면, 각 INSERT 문마다 별도의 네트워크 왕복(Round Trip)과 데이터베이스 커밋이 발생합니다. " +
            "10,000개의 데이터를 삽입하기 위해 10,000번의 통신이 일어나므로 오버헤드가 매우 큽니다.");

        model.addAttribute("optimizedQuery", "// Using saveAll() with batching enabled:\nINSERT INTO products (...) VALUES (...), (...), ...;");
        model.addAttribute("optimizedExplanation", 
            "<code>saveAll()</code>을 사용하고 JDBC 배치(Batch) 옵션이 활성화되어 있으면, JPA(Hibernate)가 여러 INSERT 문을 하나의 묶음으로 만들어 데이터베이스에 한 번만 전송합니다. " +
            "이를 통해 네트워크 통신 횟수를 획기적으로 줄여 대량 데이터 삽입 성능을 극대화합니다.");

        // 테스트 완료 후 products 테이블을 비워서 멱등성을 보장합니다.
        insertService.clearProductsTable();

        return "results";
    }
}
