# 행복 Reservation (식당 예약 서비스)

![image](https://user-images.githubusercontent.com/77368612/107967071-044e3300-6ff0-11eb-8d9f-8e4e05f9bf4d.png)
　  
   　 

# 서비스 시나리오

`기능적 요구사항`
1. 고객이 예약서비스에서 식사를 위한 식당을 예약한다.
1. 고객이 예약 보증금을 결제한다.
1. 보증금 결제가 완료되면 예약내역이 식당에 전달되고, 추천 서비스에도 전달 된다.
1. 식당에 예약정보가 전달되면 예약서비스에 예약상태를 완료 상태로 변경한다.
1. 고객이 예약 보증금에 대한 결제상태를 Deposit 서비스에서 조회 할 수 있다.
1. 예약이 완료되면 예약서비스에서 현재 예약상태를 조회할 수 있다.
1. 고객이 예약을 취소할 수 있으며, 예약을 취소하면 추천정보도 함께 삭제된다.
1. 고객이 예약한 식당에 대한 평가점수를 부여한다.
1. 고객이 모든 진행내역을 볼 수 있어야 한다.

  
  
`비기능적 요구사항`
1. 트랜잭션
    1. No Show를 방지하기 위해 Deposit이 결재되지 않으면 예약이 안되도록 한다.(Sync)
    1. Deposit이 결재되면 추천대상 예약정보를 반드시 등록하도록 한다.(Sync)
    1. 예약을 취소하면 Deposit을 환불하고 Restaurant에 예약취소 내역을 전달하며, 추천정보도 삭제한다.(Async)
1. 장애격리
    1. Deposit 시스템이 과중되면 예약을 받지 않고 잠시후에 하도록 유도한다(Circuit breaker, fallback)
    1. 추천시스템이 과중되면 예약 및 결재를 받지않고 잠시후에 하도록 유도하다.(Circuit breaker, fallback)
    1. Restaurant 서비스가 중단되더라도 예약은 받을 수 있다.(Asyncm, Event Dirven)
1. 성능
    1. 고객이 예약 및 추천 상황을 조회할 수 있도록 별도의 view로 구성한다.(CQRS)  
　    
  
  
# 체크포인트

1. Saga
1. CQRS
1. Correlation
1. Req/Resp
1. Gateway
1. Deploy/ Pipeline
1. Circuit Breaker
1. Autoscale (HPA)
1. Zero-downtime deploy (Readiness Probe)
1. Config Map/ Persistence Volume
1. Polyglot
1. Self-healing (Liveness Probe)  

　  
  
# 분석/설계

### Event Storming 결과
![20210216_180508](https://user-images.githubusercontent.com/77368612/108041331-b5e97480-7081-11eb-912e-ac4008b8b765.png)

　  
　     
### 추가 요구사항 검증

![20210216_190232](https://user-images.githubusercontent.com/77368612/108047794-8a6a8800-7089-11eb-82b4-bfe1924534ab.png)

    - 보증금 결제가 완료되면 예약내역이 식당에 전달되고, 추천 서비스에도 전달 된다.(OK)
    - 고객이 예약을 취소하면 보증금을 환불하고 식당 및 추천서비스에 취소내역을 전달한다.(OK)
    - 고객이 예약 보증금에 대한 결제상태를 Deposit 서비스에서 조회 할 수 있다.(OK)  

    - 고객이 예약한 식당에 대한 평가점수를 부여한다.(OK)
    - 예약사항에 대한 평가여부를 예약서비스에 전달한다.(OK)

    - 고객이 모든 진행내역을 볼 수 있어야 한다.(OK)
    
　  
   
### 추가된 비기능 요구사항 검증

    - 추천정보 등록이 되지 않으면 Deposit 결재가 안되도록 해아 한다.(Req/Res)
    - 예약(Reservation) 서비스가 중단되더라도 식당추천은 할 수 있어야 한다.(Pub/Sub)
    - 추천(Recommendation) 시스템이 과중되면 예약 및 예치금 결재를 받지 않고 잠시 후에 하도록 유도한다(Circuit breaker)
    - 추천을 완료하면 예약 서비스에 추천완료 상태를 업데이트해야 한다.(SAGA)
    - 추천 상황을 조회할 수 있도록 별도의 view로 구성한다.(CQRS)  
    
　  
　  
       
# 구현

서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8085 이다)

```
cd reservation
mvn spring-boot:run

cd deposit
mvn spring-boot:run  

cd customercenter
mvn spring-boot:run 

cd restaurant
mvn spring-boot:run 
```

cd recommandation
mvn spring-boot:run 
```
    
　  
　  
   
### DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 reservation 마이크로 서비스)

![20210215_120254](https://user-images.githubusercontent.com/77368612/107901177-5c504f80-6f86-11eb-94af-48fa5a03d79e.png)
    
　  
　  
   
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

![20210215_120624](https://user-images.githubusercontent.com/77368612/107901239-7f7aff00-6f86-11eb-8cc0-17d18e75b2cb.png)
    
　  
　  
   
- 적용 후 REST API 의 테스트

```
# reservation 서비스의 예약처리
http localhost:8081/reservations restaurantNo=1 day=20210215

# reservation 서비스의 예약상태 확인
http localhost:8081/reservations/1

# restaurant 서비스의 예약현황 확인
http localhost:8084/restaurant/1

```
    
　  
　  
   

# Polyglot

Reservation, Deposit, Customerservice는 H2로 구현하고 Restaurant 서비스의 경우 Hsql로 구현하여 MSA간의 서로 다른 종류의 Database에도 문제없이 작동하여 다형성을 만족하는지 확인하였다.

- reservation, deposit, customercenter의 pom.xml 파일 설정

![20210215_151200_10](https://user-images.githubusercontent.com/77368612/107911566-359f1280-6fa0-11eb-98ff-a15e7f95d942.png)
    
　  
 
- restaurant의 pom.xml 파일 설정

![20210215_151200_9](https://user-images.githubusercontent.com/77368612/107911570-3637a900-6fa0-11eb-818e-df269a61ae2d.png)
    
　  
    
　  
　  
   

# Req/Resp
```
1. 분석단계에서의 조건 중 하나로 예약(reservation)->예치금 결제(deposit) 간의 호출은 동기식 일관성을 유지하는
트랜잭션으로 처리하기로 하였다. 

2. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 
호출하도록 한다. 
```
    
　  
    
    
- 예치금 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현  (Depositservice.java)

![20210215_152121_11](https://user-images.githubusercontent.com/77368612/107912260-8d8a4900-6fa1-11eb-801d-61eaf1bf8fa0.png)

    
　  
    

- 예약을 받은 직후(@PostPersist) 예치금 결제를 요청하도록 처리

![20210215_152121_12](https://user-images.githubusercontent.com/77368612/107912264-8ebb7600-6fa1-11eb-8f14-3468a9a51478.png)
    
　  
　  
### 동기식 결제 장애시

```
# 결제 (deposit) 서비스를 잠시 내려놓음
# 예약 처리
kubectl delete deploy deposit -n skteam02
```
- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 예치금 결제 시스템이 장애가 나면 예약도 못받는다는 것을 확인
![20210215_152729_13](https://user-images.githubusercontent.com/77368612/107912870-aa734c00-6fa2-11eb-9b22-b78f27d39cb9.png)
    
　  
　  
```
# 결재(deposit)서비스 재기동
kubectl create deploy deposit --image=skteam02.azurecr.io/deposit:latest -n skteam02
```
![20210215_152729_14](https://user-images.githubusercontent.com/77368612/107912865-a9421f00-6fa2-11eb-80f8-309050271489.png)
    
　  
　  
    
　  
　  
   
# Gateway
- gateway > application.yml
![20210215_154035_15](https://user-images.githubusercontent.com/77368612/107913732-43569700-6fa4-11eb-96e4-5ffac8ad85cd.png)
    
　  
　  

- Gateway의 External-IP 확인

![20210215_154035_16](https://user-images.githubusercontent.com/77368612/107913733-43ef2d80-6fa4-11eb-98b4-dbe191a93c83.png)
    
　  
　  
- External-IP 로 Reservation서비스에 접근

![20210215_154035_17](https://user-images.githubusercontent.com/77368612/107913727-42be0080-6fa4-11eb-90b5-cf7b0e0cbe04.png)
    
　  
　      
　  
　  

# Deploy

- Deploy API 호출

```
# Namespace 생성
kubectl create ns skteam02

# 소스를 가져와 각각의 MSA 별로 빌드 진행

# 도커라이징 : Azure Registry에 Image Push 
az acr build --registry skteam02 --image skteam02.azurecr.io/reservation:latest .  
az acr build --registry skteam02 --image skteam02.azurecr.io/deposit:latest . 
az acr build --registry skteam02 --image skteam02.azurecr.io/restaurant:latest .   
az acr build --registry skteam02 --image skteam02.azurecr.io/customercenter:latest .   
az acr build --registry skteam02 --image skteam02.azurecr.io/gateway:latest . 

# 컨테이터라이징 : Deploy, Service 생성
kubectl create deploy reservation --image=skteam02.azurecr.io/reservation:latest -n skteam02
kubectl expose deploy reservation --type="ClusterIP" --port=8080 -n skteam02
kubectl create deploy deposit --image=skteam02.azurecr.io/deposit:latest -n skteam02
kubectl expose deploy deposit --type="ClusterIP" --port=8080 -n skteam02
kubectl create deploy restaurant --image=skteam02.azurecr.io/restaurant:latest -n skteam02
kubectl expose deploy restaurant --type="ClusterIP" --port=8080 -n skteam02
kubectl create deploy customercenter --image=skteam02.azurecr.io/customercenter:latest -n skteam02
kubectl expose deploy customercenter --type="ClusterIP" --port=8080 -n skteam02
kubectl create deploy gateway --image=skteam02.azurecr.io/gateway:latest -n skteam02
kubectl expose deploy gateway --type=LoadBalancer --port=8080 -n skteam02

#kubectl get all -n skteam02
```
    
　  
　  
- Deploy 확인

![20210215_155905_18](https://user-images.githubusercontent.com/77368612/107914935-c7118300-6fa6-11eb-83c3-169869bcd5ce.png)
    
　  
　  
      
      
    
　  
　  
    

# Circuit Breaker
```
1. 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함.  
2. 시나리오는 예약(reservation)-->예치금 결제(deposit) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 예치금 결제 요청이 과도할 경우 CB 를 통하여 장애격리.  
3. Hystrix 를 설정: 요청처리 쓰레드에서 처리시간이 300 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```

    
　  
　  


- application.yml 설정

![20210215_160633_19](https://user-images.githubusercontent.com/77368612/107915501-f379cf00-6fa7-11eb-9134-0aa25f7ce18b.png)

    
　  

- 피호출 서비스(예치금 결제:deposit) 의 임의 부하 처리  Reservation.java(entity)

![20210215_160633_20](https://user-images.githubusercontent.com/77368612/107915504-f4126580-6fa7-11eb-97a6-9c5f58ca0a46.png)

    
　  
　  

`$ siege -c100 -t60S -r10 -v --content-type "application/json" 'http://52.231.94.89:8080/reservations POST {"restaurantNo": "10", "day":"20210214"}'`

- 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인 (동시사용자 100명, 60초 진행)

![20210215_160633_7](https://user-images.githubusercontent.com/77368612/107916124-1bb5fd80-6fa9-11eb-8ee7-8a340d7a7682.png)
```
* 요청이 과도하여 CB를 동작함 요청을 차단
* 요청을 어느정도 돌려보내고나니, 기존에 밀린 일들이 처리되었고, 회로를 닫아 요청을 다시 받기 시작
* 다시 요청이 쌓이기 시작하여 건당 처리시간이 610 밀리를 살짝 넘기기 시작 => 회로 열기 => 요청 실패처리
```
    
　  
　  
![20210215_152121_8](https://user-images.githubusercontent.com/77368612/107915450-d93ff100-6fa7-11eb-8ac6-78c508828b29.png)

`운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌`
    
　  
    
　  
　  
   
# Auto Scale(HPA)
```
1. 앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 
이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다.  
2. 예치금 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 
넘어서면 replica 를 10개까지 늘려준다.
```

- 테스트를 위한 리소스 할당(reservation > deployment.yml)

![20210215_170036_22](https://user-images.githubusercontent.com/77368612/107920178-dcd77600-6faf-11eb-829a-afd2be2be901.png)
    
　  
　  

### autoscale out 설정 

- kubectl autoscale deploy reservation --min=1 --max=10 --cpu-percent=15 -n skteam2

![20210215_170036_21](https://user-images.githubusercontent.com/77368612/107920351-2aec7980-6fb0-11eb-9e2a-98bc26e3c503.png)
    
　  
　  
- CB 에서 했던 방식대로 워크로드를 1분 동안 걸어준다.

`$ siege -c100 -t60S -r10 -v --content-type "application/json" 'http://52.231.94.89:8080/reservations POST {"restaurantNo": "10", "day":"20210214"}' `

    
　  
　  
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:

`watch kubectl get all -n skteam02`

    
　  
　  
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:

![20210215_170036_23](https://user-images.githubusercontent.com/77368612/107920537-77d05000-6fb0-11eb-9a64-ebcb5525793e.png)
    
　  
　  
    
　  
　  
   
# Zreo-Downtown Deploy

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscale 나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.

`siege -c100 -t80S -r10 -v --content-type "application/json" 'http://52.231.94.89:8080/reservations POST {"restaurantNo": "10", "day":"20210214"}'`
    
　  
　  

- 새버전으로의 배포 시작
```
kubectl set image deploy reservation reservation=skteam02.azurecr.io/reservation:r1 -n skteam02
```
    
　  
　  
### readiness 옵션이 없는 경우 배포 중 서비스 요청처리 실패

![20210215_174012_25](https://user-images.githubusercontent.com/77368612/107923856-6b022b00-6fb5-11eb-83ec-d9aff7aab485.png)
    
　  
　  
   
### readiness 옵션 추가

- deployment.yaml 의 readiness probe 의 설정

![20210215_174655](https://user-images.githubusercontent.com/77368612/107924141-d6e49380-6fb5-11eb-98e9-73c36346fca8.png)
    
　  
　  
```
# readiness 적용 이미지 배포
kubectl apply -f kubernetes/deployment.yaml
# 이미지 변경 배포 한 후 Availability 확인:
```
![20210215_174012_27](https://user-images.githubusercontent.com/77368612/107924279-0dbaa980-6fb6-11eb-985b-0891124e9e24.png)
    
　  
　  
- 배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

![20210215_174012_28](https://user-images.githubusercontent.com/77368612/107924289-114e3080-6fb6-11eb-935f-a21ea1d7b33c.png)
    
　  
　      
    
　  
　  
   　  
　  
# Self-healing (Liveness Probe)

- deployment.yml 에 Liveness Probe 옵션 추가

![image](https://user-images.githubusercontent.com/77368612/107970557-99532b00-6ff4-11eb-82bf-312a9f8f3c8b.png)
    
　  
　  
- reservation pod에 liveness가 적용된 부분 확인

![20210215_181110_32](https://user-images.githubusercontent.com/77368612/107926561-37c19b00-6fb9-11eb-9fc0-98b22505b3bd.png)

    
　  
　  
- reservation 서비스의 liveness가 발동되어 3번 retry 시도 한 부분 확인

![20210215_180742_31](https://user-images.githubusercontent.com/77368612/107926211-c255ca80-6fb8-11eb-93b5-200e3e2c36a0.png)

