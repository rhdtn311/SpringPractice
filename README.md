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

<br>

## 스프링 컨테이너

- `ApplicationContext`를 스프링 컨테이너라 한다.
- 스프링 컨테이너는 `@Configuraiton`이 붙은 `AppConfig`를 설정 정보로 사용한다. 여기서 `@Bean` 이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라고 한다.
- 스프링 빈은 `@Bean`이 붙은 메서드의 명을 스프링 빈의 이름으로 사용한다.
- 스프링 컨테이너를 통해서 필요한 스프링 빈을 찾을 수 있다. 스프링 빈은 `applicationContext.getBean()` 메서드를 사용하여 찾을 수 있다.

<br>

#### ***스프링 컨테이너 생성***

```java
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

- `ApplicationContext`를 스프링 컨테이너라 한다.
- `ApplicationContext`는 인터페이스이다.
  - 스프링 컨테이너는 XML을 기반으로 만들 수도 있고, 애노테이션 기반의 자바 설정 클래스로 만들 수도 있다.

<br>

#### ***스프링 컨테이너의 생성 과정***

1. **스프링 컨테이너 생성**

   `new AnnotationConfigAplicationContext(AppConfig.class)`

   

2. **스프링 빈 등록**

   - `AppConfig.class` 에서 우리가 작성한 메소드와 반환 객체가 각각 스프링 컨테이너에 빈 이름, 빈 객체로 저장된다.
     - 빈 이름 : 메서드 이름을 사용한다. (바꿀 수도 있음)

3. **스프링 빈 의존관계 설정 **

   - 설정 정보를 참고해서 의존관계를 주입(DI)한다.

<br>

#### ***컨테이너에 등록된 빈 조회***

```java
@Test
    @DisplayName("애플리케이션 빈 출력하기")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }
        }
    }
```

- `getBeanDefinitionNames()` : 스프링에 등록된 모든 빈 이름을 조회한다.
- `getBean()` : 빈 객체를 조회한다.
- `getRole()` 
  - `ROLE_APPLICATION` : 일반적으로 사용자가 정의한 빈
  - `ROLE_INFRASTRUCTURE` : 스프링이 내부에서 사용하는 빈

<br>

#### ***스프링 빈 조회***

```java
ac.getBean(빈 이름, 타입);	// 빈 이름 생략 가능
```

- 조회 대상 스프링 빈이 없으면 예외 발생
  - `NoSuchBeanDefinitionException : No bean named 'xxxxx' available`

```java
AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

@Test
@DisplayName("빈 이름으로 조회")
void findBeanName() {
    MemberService memberServide = ac.getBean("memberService", MemberService.class);
    Assertions.assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
}
```

- 통일한 타입이 둘 이상일 때 오류 발생 : `NoUniqueBeanDefinitionException`
  - 이름을 입력해준다.

- 특정 타입을 모두 조회하기
  - `getBeansOfType`을 이용한다.

```java
@Test
@DisplayName("특정 타입을 모두 조회하기")
void findAllBeanByType() {
    Map<String, MemberRepository> beansOfType = ac.getBeansOfType(MemberRepository.class);
    for (String key : beansOfType.keySet()) {
        System.out.println("key = " + key + " values = " + beansOfType.get(key));
        }
 }
```

<br>

#### ***스프링 빈 조회 - 상속 관계***

- 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
  - 모든 자바 객체의 최고 부모인 `Object` 타입으로 조회하면 모든 스프링 빈을 조회한다.

<br>

#### ***BeanFactory와 ApplicationContext***

![image](https://user-images.githubusercontent.com/68289543/106606480-86c50480-65a5-11eb-86f7-f5b341f5a114.png)

- **BeanFactory**
  - 스프링 컨테이너의 최상위 인터페이스이다.
  - 스프링 빈을 관리하고 조회하는 역할을 담당한다.
  - `getBean()`을 제공한다.
  - 대부분의 기능을 제공한다.

- **ApplicationContext**
  - BeanFactory의 기능을 모두 상속받아서 제공한다.
  - 빈 관리기능 + 편리한 **부가 기능**을 제공한다.
    - **MessageSource** : 한국어권이면 한국어가 나오고 영어권이면 영어가 나오는 기술
    - **EnvironmentCapable** : 환경변수로 로컬, 개발, 운영 등을 구분해서 처리
    - **ApplicationEventPublisher** : 이벤트를 발행하고 구독하는 모델을 편리하게 지원
    - **ResourceLoader** : 파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회
  - BeanFactory를 직접 사용할 일은 거의 없고 부가기능이 포함된 ApplicationContext를 사용한다.
  - 스프링 컨테이너라 한다.

<br>

## 싱글톤

- 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴

```java
public class SingletonService {
    
    // static 영역에 객체를 하나 생성한다.
    private static final SingletonService instance = new SingletonService();
    
    // 생성자를 private으로 선언해서 외부에서 new 키워드를 통한 객체 생성을 막는다.
    private SingletonService() {}
    
    // public으로 열어서 객체 인스턴스가 필요하면 static 메서드를 통해서만 조회하도록 허용
    public SingletonService getInstance() {
        return instance;
    }
}
```

<br>

- **싱글톤 패턴의 문제점**
  - 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다.
  - 의존관계상 클라이언트가 구체 클래스에 의존한다 -> DIP 위반
  - 테스트 하기 어렵다.
  - 내부 속성을 변경하거나 초기화하기 어렵다.
  - private 생성자로 자식 클래스를 만들기 어렵다.
  - 유연성이 떨어진다.

<br>

#### ***싱글톤 컨테이너***

- 스프링 컨테이너는 싱글톤 패턴의  문제점을 해결하면서 객체 인스턴스를 싱글톤으로 관리한다.
- 스프링 컨테이너는 싱글톤 컨테이너 역할을 한다. 이렇게 싱글톤 객체를 생성하고 관리하는 기능을 싱글톤 레지스트리라 한다.
- 스프링 컨테이너의 이런 기능 덕분에 **싱글턴 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수 있다.**
  - 싱글톤 패턴을 위한 지저분한 코드가 들어가지 않아도 된다.
  - DIP, OCP, 테스트, private 생성자로부터 자유롭게 싱글톤을 사용할 수 있다.

<br>

#### ***싱글톤 방식의 주의점***

- 싱글톤 방식은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 **싱글톤 객체는 상태를 유지하게 설계하면 안된다.**
- **무상태**로 설계해야 된다.
  - 특정 클라이언트에 의존적인 필드가 있으면 안된다.
  - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다. (가급적 읽기만)
  - 필드 대신에 자바에서 공유되지 않는 지역변수, 파라미터, ThreadLocal 등을 사용해야 된다.
- 스프링 빈의 필드에 **공유 값을 설정하면 정말 큰 장애가 발생할 수 있다.**

> 예시

```java
// StatefulService.class

public class StatefulService {

    private int price;  // 상태를 유지하는 필드

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

}
```

```java
public class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);

        // ThreadA : 사용자A 10000원 주문
        statefulService1.order("userA",10000);
        // ThreadB : 사용자B 20000원 주문
        statefulService2.order("userB",20000);

        // ThreadA : 사용자A 주문 금액 조회
        int price = statefulService1.getPrice();
        System.out.println("price = " + price);

        Assertions.assertThat(statefulService1.getPrice()).isEqualTo(20000);	// 테스트 통과
    }
    
    @Configuration
    static class TestConfig {
        
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}
```

- `StatefulService`의 `price` 필드는 공유되는 필드인데, 특정 클라이언트가 값을 변경한다.
- 따라서 사용자A의 주문금액은 10000원이 나와야 하는데 20000원이라는 결과가 나왔다.
- 그래서 스프링은 무상태로 설계해야 한다.

위 코드를 다음과 같이 수정해줘야 한다.

```java
// StatefulService.class

public class StatefulService {

    private int price;  // 상태를 유지하는 필드

    public int order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        return price;
    }
}
```

```java
public class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);

        // ThreadA : 사용자A 10000원 주문
        int userAPrice = statefulService1.order("userA",10000);
        // ThreadB : 사용자B 20000원 주문
        int userBPrice = statefulService2.order("userB",20000);

        // ThreadA : 사용자A 주문 금액 조회
        System.out.println("userAprice = " + userAPrice);
        System.out.println("userBPrice = " + userBPrice);

    }
    
    @Configuration
    static class TestConfig {
        
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}
```

<br>

#### ***@Confituration과 싱글톤***

```java
@Configuration
public class Appconfig {
    
    @Bean
    public MemberService memberSerivce() {
        return new MemberService(memberRepository());
    }
    
    @Bean
    public MemberRepository memberRepository() {
        return new MemberMemoryRepository();
    }
    
    @Bean public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
}
```

위 `@Configuration`을 스프링 빈으로 등록했을 경우  `memberService()` 를 호출하면 리턴값의 파라미터로`memberRepository()`가 호출됨으로써 새로운 `MemberMemoryRepository()`가 생성되고  `memberRepository()`를 호출하면 리턴값으로 또 새로운 `MemberMemoryRepository()`가 생성되고 `orderService()`를 호출하면 리턴 값의 파라미터로 새로운 `MemberMemoryRepository()`가 생성된다. 이것은 마치 스프링빈이 싱글톤 객체를 생성하지 않는 것처럼 보인다.

```java
@Configuration
public class AppConfig {
    @Bean
    public MemberService memberService() {
        System.out.println("call MemberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call MemberRepository");
        return new MemberMemoryRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call OrderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
```

`@Configuration`을 위와 같이 수정하고 다음과 같이 호출했다.

```java
public class SingletonNewTest {
    @Test
    void checkSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        ac.getBean("memberService", MemberService.class);
        ac.getBean("memberRepository", MemberRepository.class);
        ac.getBean("orderService", OrderService.class);
    }
}
```

예상대로 라면 `MemberRepository()`가 총 3번 호출 될 것이라 생각했는데 예상과 달리 한 번 호출 되었다.

```java
call MemberService
12:57:08.719 [main] DEBUG org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'memberRepository'
call MemberRepository
12:57:08.729 [main] DEBUG org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'orderService'
call OrderService
12:57:08.729 [main] DEBUG org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'discountPolicy'
```

스프링은 직접 설정하지 않는 한, 싱글톤을 보장해 주는 것을 알 수 있다.

<br>

#### ***@Configuration과 바이트코드 조작***

- `@Configuration`을 붙이지 않는다면 바이트코드를 조작하는 CGLIB 기술이 적용되지 않아서 싱글톤이 보장되지 않는다. (스프링 빈에는 등록이 된다.)

<br>

#### ***컴포넌트 스캔과 의존관계 자동 주입***

- 스프링은 설정 정보가 없어도 자동으로 스프링 빈을 등록하는 **컴포넌트 스캔**이라는 기능을 제공한다.
- 또한 의존관계도 자동으로 주입하는 `@Autowired` 라는 기능도 제공한다.

**컴포넌트 스캔의 사용**

- 컴포넌트 스캔을 사용하려면 먼저 `@ComponentScan`을 설정 정보에 붙여주면 된다.

```java
// AutoAppConfig.java

@Configuration
@ComponentScan
public class AutoAppConfig {
    
}
```

- 참고로 컴포넌트 스캔을 사용하면 `@Component`가 붙은 설정 정보도 자동으로 등록된다.
  - 앞서 작성했던 `AppConfig`와 `TestConfig`가 자동으로 등록된다.
- 원하는 클래스가 컴포넌트 스캔의 대상이 되도록 `@Component` 어노테이션을 붙여준다.

```java
// MemberMemoryRepository

@Component
public class MemberMemoryRepository implememts MemberRepository {
     ... 
}
```

```java
// RateDiscountPolicy

@Component
public class RateDiscountPolicy implements DiscountPolicy {
    ...
}
```

```java
// MemberServiceImpl

@Component
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    
    @Autowired
    public MemberServiceImpl(MemberRepository memberRipository) {
        this.memberRepository = memberRepository;
    }
}
```

- `AppConfig`에서는 `@Bean`으로 직접 설정 정보를 작성했고 의존관계도 직접 명시했으나 컴포넌트 스캔에는 이런 설정 정보 자체가 없기 때문에 `@Autowired`를 이용하여 의존 관계를 주입해야 한다.

**@Autowired 의존관계 자동 주입**

- 생성자에 `@Autowired`를 지정하면 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한다.
- 생성자의 파라미터가 많아도 찾아서 자동으로 주입한다.

<br>

**탐색 위치와 기본 스캔 대상**

- 필요한 위치부터 탐색하도록 시작 위치를 지정할 수 있다.

``` java
@ComponentScan(
	basePackages = "hello.core"
)
```

- `basePackages` : 탐색할 패키지의 시작 위치를 지정한다. 이 패키지를 포함하여 하위 패키지를 모두 탐색한다.
  - `basePackages = {"hello.core", "hello.service"}` 와 같이 여러 시작 위치를 지정할 수도 있다.
- `basePackageClasses` : 지정한 클래스의 패키지를 탐색 시작 위치로 지정한다.
- 지정하지 않으면 `@ComponentScan`이 붙은 설정 정보 클래스의 패키지가 시작 위치가 된다.

**<< 권장하는 방법 >> **

- 패키지 위치를 지정하지 않고, 설정 정보 클래스의 위치를 프로젝트 최상단에 두는 것이다.

  - `com.hello`
  - `com.hello.service`
  - `com.hello.repository` 

  - 프로젝트의 구조가 위와 같다면 `com.hello`에 `AppConfig`와 같은 메인 설정 정보를 두고 `@ComponentScan` 애노테이션을 붙이고 `basePackages` 지정은 생략한다.

<br>

#### ***컴포넌트 스캔 기본 대상***

- `@Component` : 컴포넌트 스캔에서 사용
- `@Controller` : 스프링 MVC 컨트롤러에서 사용
  - 스프링 MVC 컨트롤러로 인식
- `@Service` : 스프링 비즈니스 로직에서 사용
  - 특별한 처리를 하지 않지만 개발자들이 핵심 비즈니스 로직이 여기에 있다고 인식할 수 있다.
- `@Repository` : 스프링 데이터 접근 계층에서 사용
  - 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환한다.
- `@Configuration` : 스프링 설정 정보에서 사용
  - 스프링 설정 정보로 인식하고, 스프링 빈이 싱글톤을 유지하도록 추가 처리를 한다.

<br>













<br>






















