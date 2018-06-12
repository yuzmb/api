# api

[![Build Status](https://travis-ci.com/mtdhb/api.svg?branch=master)](https://travis-ci.com/mtdhb/api)
[![GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)

https://www.mtdhb.com 服务端，需配合 [mtdhb/get](https://github.com/mtdhb/get) 使用

## Environment

MySQL 5.7

Redis 4.0.9

JDK 1.8+

## MySQL

```
CREATE DATABASE api;
USE api;
\. 绝对路径src/main/resources/table.sql
```

## Redis

默认配置

## Java

### Configuration
#### DATASOURCE

[src/main/resources/application.yml](src/main/resources/application.yml)

```
spring:
    datasource:
        url: jdbc:mysql://127.0.0.1:3306/api?useUnicode=true&characterEncoding=UTF-8&useSSL=false
        username: 你的数据库用户名
        password: 你的数据库密码
```

#### EMAIL（此部分代码有待优化）

由于免费企业邮箱有发送数量和频率的限制，不足以支撑我们网站的发件量，而我们又买不起收费的，曾导致大量注册我们网站的用户收不到注册邮件。所以我们才用通过配置多个免费企业邮箱的方式解决

配置多个企业邮箱需更改代码 
[src/main/java/com/mtdhb/api/util/Mails.java](src/main/java/com/mtdhb/api/util/Mails.java)

```
/**
 * 企业邮箱配置的数量
 */
private static final int SIZE = 4;
```
该数值需要与 [src/main/resources/](src/main/resources/) 目录下的 mail*.properties 文件数量相对应并更改配置文件内容

### Package

```
mvn clean package
```

### Run

```
nohup java -jar api-1.0.0.jar > console.log 2>&1 &
```

### Shutdown

```
curl -X POST http://127.0.0.1:9000/shutdown
```



