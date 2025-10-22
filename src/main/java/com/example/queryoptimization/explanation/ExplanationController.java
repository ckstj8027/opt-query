package com.example.queryoptimization.explanation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExplanationController {

    @GetMapping("/auto-increment-lock-mode")
    public String autoIncrementLockMode(Model model) {
        model.addAttribute("title", "Auto Increment Lock Mode Optimization");
        String content = "<h2>About <code>innodb_autoinc_lock_mode</code></h2>"
            + "<p>MySQL의 <code>innodb_autoinc_lock_mode</code> 설정은 <code>AUTO_INCREMENT</code> 값을 생성할 때 사용되는 잠금 모드를 결정합니다. 이 설정은 애플리케이션 코드가 아닌 서버 설정(<code>my.cnf</code>)에서 변경하며, 대량 삽입(Bulk Insert) 작업 시 동시성과 성능에 큰 영향을 미칩니다.</p>"
            + "<h3>세 가지 모드:</h3>"
            + "<ul>"
            + "<li><b><code>0</code> (Traditional):</b> 가장 보수적인 모드입니다. INSERT 문이 실행되는 동안 테이블 레벨의 <code>AUTO-INC</code> 잠금을 유지합니다. 여러 트랜잭션이 동시에 데이터를 삽입하려고 할 때 병목 현상이 발생할 수 있습니다.</li>"
            + "<li><b><code>1</code> (Consecutive - Default):</b> 기본값입니다. 삽입할 행의 수를 미리 아는 간단한 INSERT의 경우, 테이블 잠금 없이 경량화된 뮤텍스(mutex)를 사용하여 연속된 ID를 할당합니다. 하지만 <code>INSERT ... SELECT</code>와 같은 대량 삽입에서는 여전히 테이블 잠금을 사용합니다. 대부분의 경우에 적합한 균형 잡힌 모드입니다.</li>"
            + "<li><b><code>2</code> (Interleaved):</b> 가장 확장성이 좋은 모드입니다. 테이블 잠금을 전혀 사용하지 않습니다. 여러 INSERT 문이 동시에 실행될 수 있으며, 그 결과 ID가 서로 엇갈려 할당됩니다 (예: 트랜잭션 A가 1, 2, 5, 6을, 트랜잭션 B가 3, 4, 7, 8을 가져감). 동시성은 가장 높지만, ID가 반드시 연속적이어야 하는 애플리케이션에는 적합하지 않습니다.</li>"
            + "</ul>"
            + "<h3>최적화 전략:</h3>"
            + "<p>대량의 동시 삽입이 필요한 시스템(예: 로그 서버, 대용량 트래픽 서비스)에서는 <code>innodb_autoinc_lock_mode = 2</code>로 설정하여 잠금 경합을 줄이고 성능을 크게 향상시킬 수 있습니다. 단, 애플리케이션 로직이 ID의 연속성에 의존하지 않아야 합니다.</p>"
            + "<pre><code># my.cnf 설정 예시\n[mysqld]\ninnodb_autoinc_lock_mode = 2</code></pre>";
        model.addAttribute("content", content);
        return "explanation";
    }

    @GetMapping("/innodb-deadlock-detect")
    public String innodbDeadlockDetect(Model model) {
        model.addAttribute("title", "InnoDB Deadlock Detect Optimization");
        String content = "<h2>InnoDB 교착 상태 감지 (Deadlock Detection)</h2>"
            + "<p>InnoDB는 트랜잭션 간의 교착 상태를 자동으로 감지하고, 그중 하나의 트랜잭션(희생자)을 롤백하여 문제를 해결합니다. 이 기능은 데이터 무결성에 매우 중요하지만, 이와 관련된 설정을 이해하면 성능 최적화에 도움이 될 수 있습니다.</p>"
            + "<h3>최적화 및 설정:</h3>"
            + "<ul>"
            + "<li><b><code>innodb_deadlock_detect</code> (기본값: <code>ON</code>):</b> 교착 상태 감지 기능을 켤지 여부를 결정합니다. 만약 이 값을 <code>OFF</code>로 설정하면, InnoDB는 교착 상태를 감지하는 대신 <code>innodb_lock_wait_timeout</code>에 설정된 시간만큼 대기한 후 쿼리를 롤백시킵니다.</li>"
            + "<li><b>전략:</b> 극도로 높은 동시성 환경에서는 교착 상태 감지 자체가 CPU 자원을 소모하여 부담이 될 수 있습니다. 이런 경우, 교착 상태 발생 빈도가 낮다고 판단되면 이 설정을 <code>OFF</code>하고 타임아웃에 의존하는 전략을 고려할 수 있습니다. 하지만 대부분의 애플리케이션에서는 즉각적인 교착 상태 해결을 위해 <code>ON</code>으로 두는 것이 권장됩니다.</li>"
            + "</ul>"
            + "<pre><code># my.cnf 설정 예시\n[mysqld]\ninnodb_deadlock_detect = OFF\ninnodb_lock_wait_timeout = 10</code></pre>";
        model.addAttribute("content", content);
        return "explanation";
    }

    @GetMapping("/select-for-update-skip-locked")
    public String selectForUpdateSkipLocked(Model model) {
        model.addAttribute("title", "SELECT FOR UPDATE SKIP LOCKED");
        String content = "<h2><code>SKIP LOCKED</code>를 이용한 작업 큐(Job Queue) 동시성 제어</h2>"
            + "<p><code>SELECT ... FOR UPDATE</code>는 읽는 행에 배타적 잠금(exclusive lock)을 걸어 다른 트랜잭션이 해당 행을 수정하거나 잠글 수 없게 만드는 강력한 기능입니다. 하지만 여러 워커(worker)가 동일한 작업 큐 테이블에서 처리할 작업을 가져가려 할 때 병목 현상을 유발할 수 있습니다.</p>"
            + "<h3>문제점: 작업 큐 경합</h3>"
            + "<p>워커 1이 <code>SELECT ... FOR UPDATE LIMIT 1</code>으로 작업을 가져가면, 워커 2는 워커 1의 트랜잭션이 끝날 때까지 대기해야 합니다. 이는 작업 처리량을 크게 저하시킵니다.</p>"
            + "<h3>해결책: <code>SKIP LOCKED</code></h3>"
            + "<p>MySQL 8.0부터 지원되는 <code>SKIP LOCKED</code> 옵션은 이 문제를 해결합니다. <code>FOR UPDATE</code>와 함께 사용하면, 다른 트랜잭션에 의해 이미 잠긴 행을 발견했을 때 대기하지 않고 즉시 건너뛰고 다음 행을 잠그려고 시도합니다.</p>"
            + "<pre><code>-- 워커 1과 2가 동시에 실행\nSELECT * FROM jobs WHERE status = 'PENDING' ORDER BY id LIMIT 1 FOR UPDATE SKIP LOCKED;</code></pre>"
            + "<p>워커 1이 ID 1번 작업을 잠그면, 워커 2는 ID 1을 건너뛰고 다음 작업인 ID 2를 잠급니다. 따라서 두 워커가 대기 없이 동시에 작업을 처리할 수 있어 시스템 처리량이 극대화됩니다.</p>"
            + "<h3>JPA 구현 예시:</h3>"
            + "<p>JPA에서는 <code>@Lock</code>과 <code>@QueryHint</code>를 조합하여 사용할 수 있습니다.</p>"
            + "<pre><code>@Lock(LockModeType.PESSIMISTIC_WRITE)\n@QueryHints({@QueryHint(name = \"jakarta.persistence.lock.timeout\", value = \"-2\")}) // -2가 SKIP LOCKED를 의미\n@Query(\"SELECT j FROM Job j WHERE j.status = 'PENDING' ORDER BY j.id\")\nList&lt;Job&gt; findNextAvailableJob(Pageable pageable);</code></pre>";
        model.addAttribute("content", content);
        return "explanation";
    }

    @GetMapping("/innodb-buffer-pool")
    public String innodbBufferPool(Model model) {
        model.addAttribute("title", "InnoDB Buffer Pool Optimization");
        String content = "<h2>InnoDB 버퍼 풀이란?</h2>"
            + "<p>InnoDB 버퍼 풀은 테이블과 인덱스 데이터를 캐싱하는 메모리 영역입니다. MySQL에서 가장 중요한 성능 튜닝 파라미터 중 하나로, 디스크가 아닌 메모리에서 데이터를 읽게 하여 데이터베이스의 성능을 크게 향상시킵니다.</p>"
            + "<h3>핵심 설정: <code>innodb_buffer_pool_size</code></h3>"
            + "<ul>"
            + "<li>데이터베이스 전용 서버의 경우, 보통 <b>전체 시스템 RAM의 50-75%</b>로 설정하는 것이 권장됩니다.</li>"
            + "<li>버퍼 풀이 클수록 디스크 I/O가 줄어들어 읽기 중심의 워크로드에 매우 유리합니다.</li>"
            + "<li>하지만 너무 크게 설정하면 다른 시스템 프로세스가 사용할 메모리가 부족해져 스와핑(swapping)이 발생하고, 이는 오히려 성능을 심각하게 저하시킬 수 있습니다.</li>"
            + "</ul>"
            + "<h3>전략:</h3>"
            + "<p><code>SHOW ENGINE INNODB STATUS;</code> 명령어로 버퍼 풀의 히트율(Hit rate)을 확인하세요. 히트율이 99% 이상으로 높게 유지된다면 버퍼 풀이 효과적으로 동작하는 것입니다. 만약 히트율이 낮고 가용 RAM이 있다면 <code>innodb_buffer_pool_size</code>를 늘리는 것을 고려해볼 수 있습니다.</p>"
            + "<pre><code># my.cnf 설정 예시 (16GB RAM 서버)\n[mysqld]\ninnodb_buffer_pool_size = 12G</code></pre>";
        model.addAttribute("content", content);
        return "explanation";
    }

    @GetMapping("/innodb-buffer-pool-flushing")
    public String innodbBufferPoolFlushing(Model model) {
        model.addAttribute("title", "InnoDB Buffer Pool Flushing Optimization");
        String content = "<h2>버퍼 풀 플러싱(Flushing)이란?</h2>"
            + "<p>플러싱은 버퍼 풀에서 변경되었지만 아직 디스크에 기록되지 않은 데이터(dirty page)를 데이터 파일에 쓰는 과정입니다. 데이터 영속성을 위해 필수적이지만, 제대로 설정되지 않으면 I/O 병목을 유발할 수 있습니다.</p>"
            + "<h3>핵심 설정:</h3>"
            + "<ul>"
            + "<li><b><code>innodb_io_capacity</code></b>: 시스템의 디스크가 초당 처리할 수 있는 I/O 작업의 양(IOPS)을 InnoDB에 알려줍니다. 최신 SSD의 경우 2000-5000, NVMe SSD의 경우 10000 이상으로 설정할 수 있습니다.</li>"
            + "<li><b><code>innodb_io_capacity_max</code></b>: 플러싱 작업이 지연되었을 때, 이를 따라잡기 위해 순간적으로 사용할 수 있는 최대 IOPS를 설정합니다. 보통 <code>innodb_io_capacity</code>의 두 배 값으로 설정합니다.</li>"
            + "</ul>"
            + "<h3>전략:</h3>"
            + "<p>이 값들을 스토리지 사양에 맞게 튜닝하면, 급격한 플러싱으로 인한 애플리케이션 성능 저하(stalls)를 방지하고 부드러운 I/O 성능을 유지할 수 있습니다.</p>"
            + "<pre><code># my.cnf 설정 예시 (고성능 SSD 기준)\n[mysqld]\ninnodb_io_capacity = 4000\ninnodb_io_capacity_max = 8000</code></pre>";
        model.addAttribute("content", content);
        return "explanation";
    }

    @GetMapping("/redo-log-size")
    public String redoLogSize(Model model) {
        model.addAttribute("title", "Redo Log / InnoDB Log File Size Optimization");
        String content = "<h2>리두 로그(Redo Log)의 역할</h2>"
            + "<p>리두 로그(InnoDB 로그 파일)는 데이터의 변경 사항을 데이터 파일에 쓰기 전에 먼저 기록하는 공간입니다. 트랜잭션의 원자성(Atomicity)과 영속성(Durability)을 보장하며, 시스템 장애 발생 시 데이터를 복구하는 데 사용됩니다.</p>"
            + "<h3>핵심 설정: <code>innodb_log_file_size</code></h3>"
            + "<ul>"
            + "<li>리두 로그의 전체 크기는 <code>innodb_log_file_size</code> * <code>innodb_log_files_in_group</code>(로그 파일 개수)로 결정됩니다.</li>"
            + "<li>리두 로그 파일이 크면, 변경된 데이터를 메모리에 더 오래 유지하다가 한 번에 디스크에 쓸 수 있어, 쓰기 중심의 워크로드 성능을 크게 향상시킵니다. 작은 쓰기 작업을 모아 큰 순차 쓰기(sequential write)로 바꾸는 효과가 있습니다.</li>"
            + "<li>단점은 로그 파일이 너무 크면 장애 발생 시 복구 시간이 길어질 수 있다는 것입니다.</li>"
            + "</ul>"
            + "<h3>전략:</h3>"
            + "<p>일반적으로 리두 로그의 전체 크기는 <b>약 1시간 동안의 쓰기 작업을 저장할 수 있는 크기</b>로 설정하는 것이 권장됩니다. 많은 최신 시스템에서 총 2GB에서 8GB 사이의 크기는 합리적인 시작점입니다.</p>"
            + "<pre><code># my.cnf 설정 예시 (총 4GB)\n[mysqld]\ninnodb_log_file_size = 2G\ninnodb_log_files_in_group = 2</code></pre>";
        model.addAttribute("content", content);
        return "explanation";
    }
}
