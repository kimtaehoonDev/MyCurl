# CURL 만들기
HTTP의 이해를 올리기 위해 만들어보는 CURL 프로그램입니다.<br>
CURL 프로그램은 terminal을 사용해 HTTP 요청을 보내는 프로그램입니다. <br>
-d 플래그를 통해 데이터를 넣으면 application/json 타입으로 메세지를 보냅니다. <br> 


구현할 기능은 다음과 같습니다.

- [x] 입력한 args 내의 파라미터 파싱
- [x] 입력한 URL로 HTTP 요청을 보내 응답을 받아오기
- [x] HTTP 메서드를 지정하기 (플래그 -X)
- [x] 헤더 지정하기 (플래그 -H)
- [x] 데이터 입력하기 (플래그 -d)

사용 예시는 다음과 같습니다.<br>
```-X POST -H accept:*/* -H User-Agent:curl/7.79.1 -d name=taehoon&pwd=12345 localhost:8080/hello```
- POST localhost:8080/hello 요청을 보냄
```agsl
{
    "name":"taehoon",
    "pwd":"12345"
}
```

```-H accept:*/* -H User-Agent:curl/7.79.1 localhost:8080```
- GET localhost:8080 요청을 보냄

project에 대한 자세한 설명은 [해당 글](https://velog.io/@iamtaehoon/series/HTTP-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-CURL%EC%9D%84-%EA%B5%AC%ED%98%84%ED%95%B4%EB%B3%B4%EC%9E%90)을 참고해주세요