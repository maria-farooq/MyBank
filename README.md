# MyBank
MyBank is an application for handling transaction log and statistics.

The project is based on a small web-service which is created using following technologies:

- Java 1.8
- Maven
- Spring Boot

## Roadmap
[Project roadmap](https://github.com/maria-farooq/MyBank/issues?utf8=%E2%9C%93&q=is%3Aissue)

## Building the project

### Prerequisites

For building and running the application you will need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

We can build the project using mvn as following:

```shell
mvn clean install
```

**Please Note!**

One of the tests sleeps for about a minute to test that statistics are relevant upto 60 seconds.
So `mvn clean install` or running the `StatisticsControllerTest` class can take a minute longer than normal.

## Running the application
We can run the application locally using various techniques mentioned below, 
which will start a webserver (Tomcat) on port 8080 (http://localhost:8080) and serves SwaggerUI where we can inspect and try endpoints.

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
![statsclassdiagram](https://github.com/maria-farooq/MyBank/blob/master/images/statsclassdiagram.png)

![statisticsmanager](https://github.com/maria-farooq/MyBank/blob/master/images/statisticsmanager.png)

- Above `sixtySecondStatisticsMetrics` is a **fixed sized** `Vector<Statistics>`.
- It can hold upto **Maximum 60 elements**.
- Each index in Metrics represent each **Second** in past one minute.
- Element at each index represents the **Statistics of all transactions** happened in that indexed second.

**Please note again the last point that this structure DOES NOT contain any transaction data.**

### Operational details/cost on Statistics Metrics
This section will explain how we perform various operations on `statisticsMetrics`.

#### Get Statistics
Get Statistics is needed for our `/statistics` endpoint where user wants to get statistics of transactions happened in last 60 seconds.

![getstatistics](https://github.com/maria-farooq/MyBank/blob/master/images/getstatistics.png)

- since each index represents the **Statistics of transactions** happened in that indexed second.
- We need to traverse and accumulate the complete metrics.

##### Time cost
Since: we loop only for CONSTANT numbers (60)
Hence:
```
O(1)
```

##### Space cost
Since: Our **Vector<Statistics>** at maximum will contain CONSTANT(60) elements
  Hence:
```
O(1)
```

#### Keep Only 60 seconds Stats and discard older
- With every passing second, each statistics in statisticsMetrics gets one second older.
- As we only need Statistics of last 60 seconds
- We need to continuously update statisticsMetrics
- and discard any states for transactions older than 60 seconds
- following diagram shows the high level design of algorithm to maintain this integrity.

![updatestatistics](https://github.com/maria-farooq/MyBank/blob/master/images/updatestatistics.png)

##### Time cost
Since: we loop only for CONSTANT numbers (60)
Hence:
```
O(1)
```

##### Space cost
Since: Our operation will create a **Vector<Statistics>** which at maximum will contain CONSTANT(60) elements
  Hence:
```
O(1)
```

#### Handle new transaction
- Whenever a transaction is posted on `/transaction` endpoint
- Following algorithm evaluates it
- if it is older than 60 seconds, we will discard it: meaning that it will have no impact on statistics.
- otherwise we evaluate its age in seconds (which will be our index in vector)
- and update the statistics on given index as per new transaction 

![submittransaction](https://github.com/maria-farooq/MyBank/blob/master/images/submittransaction.png)

##### Time cost
```
O(1)
```

##### Space cost
```
O(1)
```

## Acknowledgments

- To my beautiful family who supported me when I was working on this over the weekend.
