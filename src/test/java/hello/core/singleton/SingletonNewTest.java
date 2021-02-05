package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.order.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SingletonNewTest {
    @Test
    void checkSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        ac.getBean("memberService", MemberService.class);
        ac.getBean("memberRepository", MemberRepository.class);
        ac.getBean("orderService", OrderService.class);
    }
}
