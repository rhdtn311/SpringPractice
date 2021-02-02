package hello.core.member;

import hello.core.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    AppConfig appconfig = new AppConfig();
    private final MemberService memberService = appconfig.memberService();

    @Test
    void join() {
        // given
        Member member = new Member(1L,"memberA",Grade.BASIC);
        // when
        memberService.join(member);
        Member findMember = memberService.findMember(1L);
        // then
        Assertions.assertThat(member).isEqualTo(findMember);
    }
}
