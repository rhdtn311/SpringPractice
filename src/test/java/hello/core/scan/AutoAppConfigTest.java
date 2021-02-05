package hello.core.scan;

import hello.core.AutoAppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;

public class AutoAppConfigTest {

    @Test
    void basicScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class);
        MemberRepository memberMemoryRepository = ac.getBean("memberMemoryRepository", MemberRepository.class);
        MemberServiceImpl memberServiceImpl = ac.getBean("memberServiceImpl", MemberServiceImpl.class);
        System.out.println(memberMemoryRepository);
        System.out.println(memberServiceImpl);
    }
}

