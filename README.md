# MyBank
MyBank is an application for handling transaction log and statistics.

The project is based on a small web-service which uses the following technologies:

- Java 1.8
- Maven
- Spring Boot

## Roadmap
[Project roadmap](https://github.com/maria-farooq/MyBank/issues?utf8=%E2%9C%93&q=is%3Aissue)

## Requirements

For building and running the application you will need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Building the project
We can build the project using mvn as following:

```shell
mvn clean install
```

**Please Note!**

One of the tests sleeps for about a minute to test that statistics are relevant upto 60 seconds.
So `mvn clean install` or running the `StatisticsControllerTest` class can take a minute longer than normal.

## Running the application
We can run the application locally using various techniques mentioned below, 
which will start a webserver (Tomcat) on port 8080 (http://localhost:8080) and serves SwaggerUI where can inspect and try endpoints.

### Run from IDE

We can start the application by executing `com.mybank.MybankApplication` class from your IDE.

### Run from Command line
To run the application from command line use [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) as following:

```shell
mvn spring-boot:run
```

## Solution
This section will explain high level solution design for the given case.

### DataStructure
- Following `Statistics Metrics` is a **fixed sized** `Vector`.
- It can hold upto **Maximum 60 elements**.
- Each index in Metrics represent each **Second** in past one minute.
- Element at each index represents the **Statistics of transactions** happened in that indexed second.

** Please note again the last point that this structure DOES NOT contain any transaction data.**

### Operational details/cost on Statistics Metrics
This section will explain how we perform various operations on `Statistics Metrics`.

#### Get Statistics
Get Statistics is need for our /statistics endpoint where user wants to see statistics of transactions happened in last 60 seconds.
- since each index represents the **Statistics of transactions** happened in that indexed second.
- We need to traverse and accumulate the complete metrics.
- We 

![getStatistics](https://github.com/maria-farooq/MyBank/blob/master/images/getstatistics.png)

#### Handle new transaction

#### Keep Only 60 seconds Stats and discard older


## Acknowledgments

- To my beautiful family who supported me when I was working on this over the weekend.
