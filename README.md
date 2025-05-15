# Spring Reactive проект
## Описание

REST API для управления базой данных системы управления учебными курсами.

### Сущности:  
Student (Студент)  
Course (Курс)  
Teacher (Преподаватель)  
Department (Кафедра)  

## Инструкция по запуску

Для запуска проект необходимо его собрнать при помощи Gradle:
```bash
  gradle build
```

После чего запустить Jar файл
```bash
  java -jar -Dspring.profiles.active=<СЕРВЕР> myapp-reactive.jar
```

### Проект поддерживает 3 сервера:
1. tomcat
2. jetty
3. netty
