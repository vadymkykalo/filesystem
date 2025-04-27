# filesystem

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/filesystem-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Filesystem S3 Service

Сервис для загрузки и выгрузки файлов с использованием S3-совместимого хранилища (MinIO).

## Функциональность

- Загрузка файлов в S3 хранилище
- Получение файлов по ID
- Удаление файлов
- Получение списка всех файлов
- Автоматическое создание бакета при запуске

## Технологии

- Quarkus
- MinIO (S3-совместимое хранилище)
- PostgreSQL (для хранения метаданных)
- jOOQ
- REST API с документацией OpenAPI

## Запуск приложения

### Подготовка

Для запуска требуется Docker и Docker Compose. Все необходимые сервисы запускаются с помощью команды:

```shell script
docker-compose up -d
```

Это запустит:
- PostgreSQL (порт 5432)
- MinIO (порты 9000 для API и 9001 для веб-интерфейса)

### Запуск в режиме разработки

```shell script
./gradlew quarkusDev
```


## Related Guides

- Minio Client extension ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-minio/dev/index.html)): Integrates MinIO Java SDK for Amazon S3 Compatible Cloud Storage
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and more
- Liquibase ([guide](https://quarkus.io/guides/liquibase)): Handle your database schema migrations with Liquibase
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
