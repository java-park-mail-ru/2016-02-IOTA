# 2016-02-IOTA
Настольная игра IOTA

Желательный способ запуска:
```
mvn
```
Если нужно изменять параметры запуска:
```
mvn clean generate-sources package
java -Ddb.user=root -Ddb.password=password -Ddb.name=iotadb -jar target/IotaBackend-1.0-SNAPSHOT.jar 8080
```
В IntelliJ IDEA рекомендуется настроить запуск через maven с целью по умолчанию. (!!)
