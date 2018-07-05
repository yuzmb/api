# api

[![Build Status](https://travis-ci.com/mtdhb/api.svg?branch=master)](https://travis-ci.com/mtdhb/api)
[![GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)

https://www.mtdhb.com 服务端，需配合 [mtdhb/get](https://github.com/mtdhb/get) 使用

## Environment

MySQL 5.7

Redis 4.0.9

JDK 1.8+

Nginx 1.12.2

## MySQL

```
CREATE DATABASE api;
USE api;
\. 绝对路径ddl.sql
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

#### EMAIL

[src/main/resources/mail0.properties](src/main/resources/mail0.properties)

```
mail.transport.protocol=smtp
mail.smtp.host=你的邮箱的 SMTP 服务器地址
mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
mail.smtp.socketFactory.port=465
mail.smtp.auth=true

# Custom
com.mtdhb.mail.personal=\u6BCF\u5929\u5927\u7EA2\u5305
com.mtdhb.mail.user=你的邮箱账号
com.mtdhb.mail.password=你的邮箱密码
```

支持多邮箱配置，你只需在 [src/main/resources/](src/main/resources/) 目录下新增 `mail*.properties` 并保证文件名最后的索引数字是连续递增的即可。

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
curl -X POST http://127.0.0.1:9000/monitor/shutdown
```

## Nginx

部分 nginx 配置（与本站服务器不完全一致，删减了部分配置，仅供参考）

```nginx
map $http_origin $corsHost {
    default 0;
    "~https://www.mtdhb.com" https://www.mtdhb.com;
    "~http://localhost:4001" http://localhost:4001;
    "~http://127.0.0.1:4001" http://127.0.0.1:4001;
}

server {
    listen 443 ssl default_server;
    server_name api.mtdhb.com;

    add_header Access-Control-Allow-Credentials true;
    add_header Access-Control-Allow-Origin $corsHost;
    add_header Access-Control-Allow-Headers X-User-Token;
    add_header Access-Control-Allow-Methods 'GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE';

    expires -1;

    if ($request_method = 'OPTIONS') {
        return 204;
    }

    if ($server_port !~ 443) {
        rewrite ^(/.*)$ https://$host$1 permanent;
    }

    location = /notice.json {
        expires 1m;
        root /存放公告的目录;
    }

    location / {
        proxy_pass http://127.0.0.1:8080;
    }
}
```
