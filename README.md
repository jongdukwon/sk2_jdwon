# 행복 Reservation (식당 예약 서비스) / 개인과제

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
    
　  
   
### 추가 비기능 요구사항 검증

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

cd recommandation
mvn spring-boot:run 
```
    
　  
　  
   
### DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 Recommendation 마이크로 서비스)

![20210216_191136](https://user-images.githubusercontent.com/77368612/108048846-dc5fdd80-708a-11eb-9e49-6a581bff3cbc.png)
    
　  
　  
   
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

![20210216_191353](https://user-images.githubusercontent.com/77368612/108049048-2052e280-708b-11eb-8f8d-6d594c7d06d2.png)
    
　  
　  
   
- 적용 후 REST API 의 테스트
```
# recommendation 서비스의 추천점수 처리
```
![20210216_193605_3](https://user-images.githubusercontent.com/77368612/108051481-4b8b0100-708e-11eb-93f2-f808f523f0f3.png)
```
# recommendation 서비스의 추천상태 확인
```
![20210216_193605_4](https://user-images.githubusercontent.com/77368612/108051483-4c239780-708e-11eb-9b28-0b58b40f14c9.png)
```
# reservation 서비스의 추천완료 현황 확인
```
![20210216_193605_5](https://user-images.githubusercontent.com/77368612/108051485-4cbc2e00-708e-11eb-8eca-0f465c8d2014.png)
```
# customerservice 서비스의 전체 현황 확인
```
![20210216_193605_6](https://user-images.githubusercontent.com/77368612/108051488-4cbc2e00-708e-11eb-8975-e597317ae359.png)
　  
　  
   
   

# Polyglot

Reservation, Deposit, Customerservice는 H2로 구현하고 Restaurant, Recommendation 서비스의 경우 Hsql로 구현하여 MSA간의 서로 다른 종류의 Database에도 문제없이 작동하여 다형성을 만족하는지 확인하였다.

- reservation, deposit, customercenter의 pom.xml 파일 설정

![20210216_194247](https://user-images.githubusercontent.com/77368612/108052240-2e0a6700-708f-11eb-8e69-79fce45c73d8.png)
    

- restaurant, recommendation의 pom.xml 파일 설정

![20210216_194240](https://user-images.githubusercontent.com/77368612/108052238-2d71d080-708f-11eb-9e50-1ba5089111c4.png)
    
　  
    
　  
　  
   

# Req/Resp
```
1.예약(reservation)->예치금 결제(deposit)->추천(Recommendation) 간의 호출은 일관성을 유지하도록 동기식
트랜잭션으로 처리하기로 하였다. 

2. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 
호출하도록 한다. 
```
    
　  
    
    
- 추천서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현  (Recommendationservice.java)

![20210216_195617](https://user-images.githubusercontent.com/77368612/108053689-17650f80-7091-11eb-9937-4c7062db9a80.png)

    
　  
    

- 예치금 결재를 완료한 직후(@PostPersist) 추천정보 등록을 요청하도록 처리

![20210216_195624](https://user-images.githubusercontent.com/77368612/108053690-18963c80-7091-11eb-8829-77a43cb52622.png)
    
　  
　  
### 추천(Recommendation)서비스 장애시(Req/Resp)

```
# 추천(Recommendation) 서비스를 잠시 내려놓음
```
- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 추천(Recommendation) 시스템이 장애가 나면 예약도 못받는다는 것을 확인

![20210216_200734](https://user-images.githubusercontent.com/77368612/108055190-fc939a80-7092-11eb-95e8-2cdb96bd9a93.png)
    
　  
```
# 추천(Recommendation) 서비스 재기동
```

![20210216_200729](https://user-images.githubusercontent.com/77368612/108055188-fbfb0400-7092-11eb-9c3b-8216d12c1f3c.png)

　  
　  
   
# Gateway
- gateway > application.yml

![20210216_201337](https://user-images.githubusercontent.com/77368612/108055693-a96e1780-7093-11eb-9565-f94a72a8a3b7.png)

　  
- Gateway(8088 Port)로 Recommendation 서비스에 접근

![20210216_201724](https://user-images.githubusercontent.com/77368612/108055956-036edd00-7094-11eb-9621-f529eda2311c.png)

　  

# Deploy

- Deploy (API 방식)

```
# Namespace 생성
kubectl create ns sk2

# Github(jongdukwon/sk2_jdwon)에서 소스를 가져온 후 각각의 MSA 디렉토리로 이동한 및 Packaging(mvn package) 진행

# 도커라이징 : Azure Registry에 Image Push 
az acr build --registry jdwon --image jdwon.azurecr.io/reservation:latest . 
az acr build --registry jdwon --image jdwon.azurecr.io/deposit:latest . 
az acr build --registry jdwon --image jdwon.azurecr.io/restaurant:latest . 
az acr build --registry jdwon --image jdwon.azurecr.io/customercenter:latest .  
az acr build --registry jdwon --image jdwon.azurecr.io/recommendation:latest . 
az acr build --registry jdwon --image jdwon.azurecr.io/gateway:latest . 

# 컨테이터라이징 : Deploy, Service 생성
kubectl create deploy reservation --image=jdwon.azurecr.io/reservation:latest -n sk2
kubectl expose deploy reservation --type="ClusterIP" --port=8080 -n sk2
kubectl create deploy deposit --image=jdwon.azurecr.io/deposit:latest -n sk2
kubectl expose deploy deposit --type="ClusterIP" --port=8080 -n sk2
kubectl create deploy restaurant --image=jdwon.azurecr.io/restaurant:latest -n sk2
kubectl expose deploy restaurant --type="ClusterIP" --port=8080 -n sk2
kubectl create deploy customercenter --image=jdwon.azurecr.io/customercenter:latest -n sk2
kubectl expose deploy customercenter --type="ClusterIP" --port=8080 -n sk2
kubectl create deploy recommendation --image=jdwon.azurecr.io/recommendation:latest -n sk2
kubectl expose deploy recommendation --type="ClusterIP" --port=8080 -n sk2
kubectl create deploy gateway --image=jdwon.azurecr.io/gateway:latest -n sk2
kubectl expose deploy gateway --type=LoadBalancer --port=80 -n sk2

```
　  
- Deploy 확인
```
#kubectl get all -n sk2 -o wide
```

![20210217_095728](https://user-images.githubusercontent.com/77368612/108141168-95afc900-7106-11eb-9076-0c420dd99295.png)

    
　 
    
　  
　  
    

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
Recommendation 서비스에 대해 CPU 사용량이 15%를 넘어서면 replica 를 10개까지 동적으로 늘려주도록 HPA 를 설정한다.
```

- 테스트를 위한 리소스 할당(recommendation > deployment.yml)

![20210217_092448](https://user-images.githubusercontent.com/77368612/108139321-3308fe00-7103-11eb-83cc-f010d8d988cc.png)
    
　  
　  

### autoscale out 설정 

`- kubectl autoscale deploy recommendation --min=1 --max=10 --cpu-percent=15 -n sk2`

![20210217_100036](https://user-images.githubusercontent.com/77368612/108141442-12db3e00-7107-11eb-83d4-7d9cf6c6df16.png)
    
　  
　  
- CB 에서 했던 방식대로 워크로드를 1분 동안 걸어준다.

`$ siege -c100 -t60S -r10 -v --content-type "application/json" 'http://54.141.22.82/reservations POST {"restaurantNo": "10", "day":"20210214"}'`

    
　  
　  
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:

`watch kubectl get all -n sk2`

    
　  
　  
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

- recommendation > deployment.yml 에 Liveness Probe 옵션 추가

![20210216_223059](https://user-images.githubusercontent.com/77368612/108069582-e0015d80-70a6-11eb-89fc-9f3d04719fed.png)
    
　  
　  
- recommendation pod에 liveness가 적용된 부분 확인

![20210216_223330](https://user-images.githubusercontent.com/77368612/108069716-06bf9400-70a7-11eb-92ed-5869c62ae214.png)

    
　  
- recommendation 서비스의 liveness가 발동되어 5번 retry 시도 한 부분 확인

![20210216_223050](https://user-images.githubusercontent.com/77368612/108069802-2060db80-70a7-11eb-8422-1a9c9ffdb509.png)

