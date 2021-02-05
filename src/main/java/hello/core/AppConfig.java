package hello.core;

import hello.core.discount.DiscountPolicy;

import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberMemoryRepository;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 앱 전체를 설정하고 구상
@Configuration
public class AppConfig {
    // MemberService 역할
    @Bean   // 스프링 컨테이너에 등록
    public MemberService memberService() {
        System.out.println("call MemberService");
        return new MemberServiceImpl(memberRepository());
    }

    // MemberRepository 역할
    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call MemberRepository");
        return new MemberMemoryRepository();
    }

    // OrderService 역할
    @Bean
    public OrderService orderService() {
        System.out.println("call OrderService" +
                "");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
