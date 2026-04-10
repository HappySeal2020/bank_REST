<h1>Учебный проект "Система управления банковскими картами"</h1>


1. Описание
2. Technologies
3. How to run
4. Services
5. Endpoints

## 1. Описание
Система позволяет через REST-запросы выполнять операции:
- CRUD пользователей;
- CRUD карт;
- блокировка/разблокировка карт;
- изменение баланса карт;
- перевод между своими картами;
- предусмотрены роли: ADMIN и USER.

Для хранения данных используется PostgreSQL.

## 2. Technologies
Java 21  
Spring Boot 3  
Spring Data JPA  
PostgreSQL  
Liquibase  
Docker  
JUnit 5

## 3. How to run
git clone git@github.com:HappySeal2020/bank_REST.git  
cd bank_rest-main  
docker compose up -d  
mvn clean install  

## 4. Services
application http://localhost:8080  
Postgres localhost:5432

## 5. Endpoints
| N  | Адрес                                                        | Метод  | Параметры                                                                                                                                                                             | Роль  | Описание                                                                                                                                                                                                       |
|----|--------------------------------------------------------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1  | http://localhost:8080/bank-rest/auth/login                   | POST   | {"login": "admin", "password": "admin"}                                                                                                                                               | любая | получение JWT access токен                                                                                                                                                                                     |
| 2  | http://localhost:8080/bank-rest/auth/refresh                 | POST   |                                                                                                                                                                                       | любая | получение JWT refresh токен                                                                                                                                                                                    |
| 3  | http://localhost:8080/bank-rest/api-1.0/users                | GET    | ?page=0&size=10&login=nikolay                                                                                                                                                         | ADMIN | Просмотр пользователей: page-номер страницы, size-размер страницы, login-имя пользователя. Все параметры - необязательные.                                                                                     |
| 4  | http://localhost:8080/bank-rest/api-1.0/users                | POST   | {"id": 0, "login": "new-user", "password": "new-user", "role": "USER" }                                                                                                               | ADMIN | Добавление пользователя                                                                                                                                                                                        |
| 5  | http://localhost:8080/bank-rest/api-1.0/users/id             | PUT    | {"id": id, "login": "new-user", "password": "new-user", "role": "USER" }                                                                                                              | ADMIN | Изменение пользователя с номером id.                                                                                                                                                                           |
| 6  | http://localhost:8080/bank-rest/api-1.0/users/id             | DELETE |                                                                                                                                                                                       | ADMIN | Удаление пользователя с номером id (возможно только при отсутствии у пользователя карт).                                                                                                                       |
| 7  | http://localhost:8080/bank-rest/api-1.0/cards                | GET    | ?page=0&size=10&card=1234&owner=olga&login=olga                                                                                                                                       | ADMIN | Просмотр всех карт: page-номер страницы, size-размер страницы, card-последние 4 символа номера карты, owner-эмбоссированное на карту имя пользователя, login-имя пользователя. Все параметры - необязательные. |
| 8  | http://localhost:8080/bank-rest/api-1.0/cards                | POST   | { "user": { "id": 4}, "cardNum": "1111222233334444", "owner": "MRS OLGA", "validThru": "2035-12-23", "currentStatus": "ACTIVE", "requestedStatus": "ACTIVE", "balance": 0 }           | ADMIN | Добавление карты пользователю. Пользователь должен существовать.                                                                                                                                               |
| 9  | http://localhost:8080/bank-rest/api-1.0/cards/id             | PUT    | { "id": id, "user": { "id": 4}, "cardNum": "1111222233334444", "owner": "MRS OLGA", "validThru": "2035-12-23", "currentStatus": "ACTIVE", "requestedStatus": "ACTIVE", "balance": 0 } | ADMIN | Изменение карты пользователя с внутренним номером id.                                                                                                                                                          |
| 10 | http://localhost:8080/bank-rest/api-1.0/cards/id             | DELETE |                                                                                                                                                                                       | ADMIN | Удаление карты с внутренним номером id.                                                                                                                                                                        |
| 11 | http://localhost:8080/bank-rest/api-1.0/cards/status         | GET    |                                                                                                                                                                                       | ADMIN | Просмотр заявок на блокировку/разблокировку                                                                                                                                                                    |
| 12 | http://localhost:8080/bank-rest/api-1.0/cards/status         | PUT    | /id?action=lock                                                                                                                                                                       | ADMIN | Изменение статуса карты по заявке. id - внутренний номер карты.                                                                                                                                                |
| 13 | http://localhost:8080/bank-rest/api-1.0/client               | GET    | ?page=0&size=6&card=1234                                                                                                                                                              | USER  | Просмотр клиентом своих карт + суммарный баланс по всем картам. card-поиск по последним четырём символам номера карты. Все параметры - необязательные.                                                         |
| 14 | http://localhost:8080/bank-rest/api-1.0/client/cardstatus    | PUT    | /id?action=lock                                                                                                                                                                       | USER  | Заявка на блокировку карты. id - внутренний номер карты.                                                                                                                                                       |
| 15 | http://localhost:8080/bank-rest/api-1.0/client/changebalance | PUT    | ?src=id&&amount=380                                                                                                                                                                   | USER  | Пополнение или списание с карты id суммы (380).                                                                                                                                                                |
| 16 | http://localhost:8080/bank-rest/api-1.0/client/transfer      | PUT    | ?src=39&&dest=40&&amount=100                                                                                                                                                          | USER  | Перевод с карты 39 на карту 40 суммы 100.                                                                                                                                                                      |
| 17 | http://localhost:8080/bank-rest/swagger-ui/index.html        |        |                                                                                                                                                                                       | любая | Swagger                                                                                                                                                                                                        |