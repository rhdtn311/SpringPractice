인프런 김영한 < 스프링 핵심 원리 - 기본편 > 을 보고 작성한 코드와 배운 점을 정리
<br>
<br>
## 좋은 객체 지향 설계의 5가지 원칙 적용

#### ***SRP : 단일 책임의 원칙***

- **SRP** : 한 클래스는 하나의 책임만 가져아 한다.
  - 구현 객체를 생성하고 연결하는 책임은 `AppConfig`가 담당
  - 클라이언트 객체는 실행하는 책임만 담당

<br>

#### ***DIP : 의존관계 역전 원칙***

- 프로그래머는 추상화에 의존하되, 구체화에 의존하면 안된다.

```java
// OrderServiceImple.class
public class OrderServiceImpl implements OrderService{
    private final MemberRepository memberRepository = new MemberMemoryRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    ...
```

위 코드에서 `OrderService` 인터페이스의 구현 클래스인 `OrderServiceImpl` 는 `MemberRepository` 추상화 인터페이스와 `DiscountPolicy` 추상화 인터페이스에 의존하는 것 같지만, `MemberMemoryRepository`와 `FixDiscountPolicy`라는 구체화 구현 클래스도 함께 의존하고 있다. 따라서 만약 할인 정책이 `FixDiscountPolicy`에서 다른 구현 클래스인 `RateDiscountPolicy`로 바뀐다면, 위 코드를 다음과 같이 수정해 줘야 한다.

```java
// OrderServiceImple.class
public class OrderServiceImpl implements OrderService{
    private final MemberRepository memberRepository = new MemberMemoryRepository();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); // 클라이언트 코드가 변경
    ...
```

즉, 클라이언트 코드가 직접 수정된 것이다. 따라서 클라이언트 코드가 추상화 인터페이스에만 의존하도록 코드를 다음과 같이 변경하였다. 

```java
// OrderServiceImple.class
public class OrderServiceImpl implements OrderService{
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    
    OrderServiceImple(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
```

하지만 클라이언트 코드는 인터페이스만으로는 아무것도 할 수 없기 때문에 `AppConfig` 를 생성하여 다음과 같이 코드를 작성하였다.

```java
// AppConfig.class
public class AppConfig {
    // MemberRepository 역할
	private MemberRepository memberRepository {
        return new MemberMemoryRespsitory();
    }
    
    // DiscountPolicy 역할
    private DiscountPolicy discountPolicy {
        return new FixDiscountPolicy();
    }
}
```

이렇게 바꾸면 `AppConfig`가 `FixDiscountPolicy` 객체 인스턴스를 클라이언트 코드 대신 생성해서 클라이언트 코드에 의존 관계를 주입했다. 이렇게해서 `DIP` 원칙을 따르면서 문제를 해결했다. `AppConfig`는 다음과 같이 호출 할 수 있다.

```java
AppConfig appConfig = new AppConfig();

MemberRepository memberRepository = appConfig.memberRepository();	//  자동으로 MemberMemoryRepository 구현 클래스의 객체 생성
```

이제 만약 `FixDiscountPolicy`가 `RateDiscountPolicy`로 변경된다면 `AppConfig` 클래스의 코드만 다음과 같이 바꿔주면 된다.

```java
// AppConfig.class
public class AppConfig {
    // DiscountPolicy 역할
    private DiscountPolicy discountPolicy {
        return new RateDiscountPolicy();
    }
}
```

<br>

#### ***OCP*** 

소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다.

- 다형성을 사용하고 클라이언트가 DIP를 지키면 OCP를 지킨다는 가능성이 생긴다.
- 애플리케이션을 사용영역과 구성 영역으로 나누었다.
- `AppConfig`가 의존관계를 `FixDiscountPolicy` 에서 `RateDiscountPolicy`로 변경해서 클라이언트 코드에 주입하므로 클라이언트 코드는 변경하지 않아도 된다.
- 즉, 소프트웨어 요소를 새롭게 확장해도 사용 영역의 변경은 닫혀있다.
