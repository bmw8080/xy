# XY - 第三方平台 API 中转服务

[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

基于 Spring Boot 的第三方平台 API 中转代理服务，统一封装微信、抖音、支付宝等平台的接口调用，提供 token 管理和请求转发能力。

## 技术栈

- **Java 17**
- **Spring Boot 4.0.6**
- **SpringDoc OpenAPI** (Swagger UI)
- **Caffeine** 本地缓存
- **Lombok**
- **Bean Validation**

## 功能特性

- 🔑 **统一鉴权**：自动管理各平台 access_token，缓存 2 小时
- 🔄 **请求中转**：统一转发 GET/POST 请求到各平台 API
- 📝 **API 文档**：内置 Swagger UI，开箱即用
- 🛡️ **全局异常处理**：统一错误响应格式
- 📊 **请求日志**：自动记录请求和响应日志

## 快速开始

### 运行

```bash
# Maven 启动
./mvnw spring-boot:run

# 或打包后运行
./mvnw clean package
java -jar target/xy-0.0.1-SNAPSHOT.jar
```

### 访问文档

启动后访问 Swagger UI：

```
http://localhost:8080/swagger-ui.html
```

## API 接口

### 微信平台 `/api/wechat`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/wechat/auth` | POST | 获取 access_token（appid + secret） |
| `/api/wechat/invoke` | GET | 携带 token 转发 GET 请求 |
| `/api/wechat/invoke` | POST | 携带 token 转发 POST 请求 |

### 抖音平台 `/api/douyin`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/douyin/auth` | POST | 获取 access_token（clientKey + clientSecret） |
| `/api/douyin/invoke` | GET | 携带 token 转发 GET 请求 |
| `/api/douyin/invoke` | POST | 携带 token 转发 POST 请求 |

### 支付宝平台 `/api/alipay`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/alipay/auth` | POST | 注册凭据（appId + privateKey） |
| `/api/alipay/invoke` | POST | 自动签名并转发请求 |

## 请求示例

### 微信获取 access_token

```bash
curl -X POST http://localhost:8080/api/wechat/auth \
  -H "Content-Type: application/json" \
  -d '{"appid": "your_appid", "secret": "your_secret"}'
```

### 微信接口调用

```bash
curl -X POST http://localhost:8080/api/wechat/invoke \
  -H "Content-Type: application/json" \
  -d '{"appid": "your_appid", "path": "/cgi-bin/user/info", "params": {"openid": "xxx"}}'
```

## 项目结构

```
src/main/java/cn/xm/xy/
├── config/          # 配置类（Swagger、缓存、拦截器等）
├── controller/      # 控制器层
├── service/         # 业务接口
│   └── impl/       # 业务实现
├── dto/            # 数据传输对象
│   ├── wechat/
│   ├── douyin/
│   └── alipay/
└── common/         # 公共模块
    ├── result/     # 统一响应封装
    └── exception/  # 异常处理
```

## 配置说明

配置文件：`src/main/resources/application.yml`

- 日志级别：`cn.xm.xy` 默认为 DEBUG
- 日志文件：`logs/xy.log`，单文件最大 50MB，保留 30 天
- Swagger 扫描路径：`/api/**`

## 打包部署

```bash
./mvnw clean package -DskipTests
java -jar target/xy-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
