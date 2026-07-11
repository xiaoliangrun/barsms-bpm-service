# 环境变量说明

各 profile（dev / sit / pro）的敏感凭据已从 yml 中移除，改为通过环境变量注入。
启动前请确保以下环境变量已设置，否则 Spring 占位符解析会因无默认值而启动失败。

## 必需环境变量

| 变量名            | 说明                | dev 默认值                                      | sit/pro |
|-------------------|---------------------|-------------------------------------------------|---------|
| `DB_URL`          | MySQL JDBC 连接串   | `jdbc:mysql://127.0.0.1:3306/barsmsbpm?...`      | 必填    |
| `DB_USERNAME`     | MySQL 用户名        | `root`                                          | 必填    |
| `DB_PASSWORD`     | MySQL 密码          | 必填                                            | 必填    |
| `REDIS_HOST`      | Redis 主机          | `127.0.0.1`                                     | 必填    |
| `REDIS_PORT`      | Redis 端口          | `6379`                                          | 可选    |
| `REDIS_PASSWORD`  | Redis 密码          | 必填                                            | 必填    |
| `REDIS_DATABASE`  | Redis 库号          | `0`                                             | 可选    |

> dev 仅对非敏感连接参数提供默认值，密码类变量仍需显式设置。

## 本地开发示例

```bash
export DB_PASSWORD=your_local_db_password
export REDIS_PASSWORD=your_local_redis_password
```

或在 IDE 运行配置的 Environment variables 中填入上述变量。

## 切换 profile

```bash
java -jar barsms-bpm-web.jar --spring.profiles.active=sit
```

## 安全提示

- 请勿将真实密码提交到代码库或聊天工具中。
- sit / pro 的凭据应通过 CI/CD 流水线或运维平台注入，而非写在配置文件里。
- 若此前明文密码已泄露（如旧版 yml 中的数据库密码），应尽快在对应环境轮换。
