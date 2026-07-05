# barsms-bpm-service 项目编码规范

> 基于 barsms-bpm-service 项目实际代码结构整理，用于指导后续开发。

---

## 一、整体架构

### 模块分层（DDD 风格）

| 模块 | 职责 | 依赖 |
|------|------|------|
| `barsms-bpm-common` | 通用层：基础类、枚举、工具、异常、配置、Redis | 无内部依赖 |
| `barsms-bpm-infra` | 基础设施层：Mapper 接口、Mapper XML、实体类、MyBatis 配置 | common |
| `barsms-bpm-domain` | 领域层：Service 接口、Service 实现、DTO | common + infra |
| `barsms-bpm-web` | Web 层：Controller、启动类、配置文件 | domain + common |

技术栈：Spring Boot 2.7.18 + MyBatis-Plus 3.5.3.1 + Druid 1.2.20 + Lombok 1.18.30 + MySQL 8.0.33 + Spring Data Redis（Lettuce）+ Java 1.8。

### 包结构

根包 `com.cpic.barsms.bpm`，启动类直接放在根包下，使用 `@SpringBootApplication(scanBasePackages = "com.cpic.barsms.bpm")` 扫描全部模块。

```
com.cpic.barsms.bpm
├── common
│   ├── base          # ApiResult 统一返回结果
│   ├── config        # GlobalExceptionHandler 等全局配置
│   ├── constants     # BatchProperties 配置属性类
│   ├── enums         # 枚举类
│   ├── exception     # 自定义异常
│   ├── redis         # Redis 配置与分布式锁
│   ├── request       # 请求基类（PageRequest）
│   └── utils         # 工具类
├── domain
│   ├── dto           # 数据传输对象
│   ├── service       # Service 接口
│   └── serviceimpl   # Service 实现
├── infra
│   ├── dto           # 基础设施层 DTO（联表查询结果）
│   ├── mapper        # Mapper 接口
│   ├── model.entity  # 实体类
│   └── mybatis       # MyBatis-Plus 配置
└── controller        # Controller
```

---

## 二、注解使用规范

### 类级注解

| 类型 | 注解 | 示例 |
|------|------|------|
| Controller | `@Slf4j` + `@RestController` + `@RequestMapping("/xxx")` | BatchGenerateController |
| Service 实现 | `@Slf4j` + `@Service` | BatchGenerateServiceImpl |
| 通用 Bean | `@Slf4j` + `@Component` | RedisDistributedLock、BatchProperties |
| 配置属性类 | `@Data` + `@Component` + `@ConfigurationProperties(prefix = "batch")` | BatchProperties |
| 配置类 | `@Configuration` | RedisConfig、MybatisPlusConfig |
| 全局异常处理 | `@Slf4j` + `@RestControllerAdvice` | GlobalExceptionHandler |
| Mapper 接口 | `@Mapper` | BpmNodeInstanceMapper |
| 实体类 / DTO / 枚举 | `@Data` | BpmNodeInstance、BatchGenerateRequest |

### 依赖注入

- 统一使用**字段注入**（`@Autowired` 写在字段上），不使用构造器注入（`@RequiredArgsConstructor`）
- 每个依赖独占一行，`@Autowired` 注解在字段上方

```java
@Autowired
private BpmSceneMapper bpmSceneMapper;
@Autowired
private BpmNodeInstanceMapper bpmNodeInstanceMapper;
```

### 方法 / 参数注解

- Controller 入参：`@PostMapping` + `@Validated @RequestBody`
- 事务：`@Transactional(rollbackFor = Exception.class)`（显式指定 rollbackFor）
- MyBatis 参数：`@Param("xxx")` 逐一标注
- 校验：`@NotBlank(message = "xxx不能为空")` 等 JSR-303 注解

### Service 层 MyBatis-Plus 规范

**只有与数据库表直接映射的 Service** 才继承 MyBatis-Plus 的 `IService` 和 `ServiceImpl`，以获得通用 CRUD 能力。业务编排类 Service（如任务生成、引用节点更新等）即使注入了 Mapper，也不需要继承。

- **判断标准**：Service 接口命名是否与数据库表名对齐
  - 对齐（如 `BpmSceneService` → `bpm_scene` 表）：**必须**继承 `IService` / `ServiceImpl`
  - 不对齐（如 `DailyTaskGeneratorService`、`MonthlyTaskGeneratorSevice`、`BatchGenerateService`）：**不继承**
- **Service 接口**：`extends IService<实体类>`
  - import: `com.baomidou.mybatisplus.extension.service.IService`
- **Service 实现**：`extends ServiceImpl<对应Mapper, 实体类> implements XxxService`
  - import: `com.baomidou.mybatisplus.extension.service.impl.ServiceImpl`
- **命名对齐规则**：Service 接口和 ServiceImpl 的命名必须与数据库表一一对应
  - 表 `bpm_scene` → `BpmSceneService` / `BpmSceneServiceImpl`
  - 表 `bpm_batch_exec_log` → `BpmBatchExecLogService` / `BpmBatchExecLogServiceImpl`
  - 表 `bpm_node_instance` → `BpmNodeInstanceService` / `BpmNodeInstanceServiceImpl`

参照：`BpmSceneService`（接口）和 `BpmSceneServiceImpl`（实现）

```java
// Service 接口（与表对齐）
public interface BpmSceneService extends IService<BpmScene> {
    Long selectIdByName(String sceneName);
}

// Service 实现（与表对齐）
@Slf4j
@Service
public class BpmSceneServiceImpl extends ServiceImpl<BpmSceneMapper, BpmScene> implements BpmSceneService {
    @Autowired
    private BpmSceneMapper bpmSceneMapper;
}

// 业务编排类 Service（不与表对齐，不继承）
public interface DailyTaskGeneratorService {
    void generate(Date tDay, Date tDayBase, String versionName, Long sceneId, String nodeCodePrefix);
}
```

---

## 三、命名规范

### 类名

| 类型 | 规则 | 示例 |
|------|------|------|
| 实体类 | `Bpm` 前缀 + 表名业务名（驼峰） | BpmNodeInstance、BpmScene |
| Service 接口 | 业务名 + `Service` | BatchGenerateService |
| Service 实现 | 接口名 + `Impl` | BatchGenerateServiceImpl |
| Mapper 接口 | 实体名 + `Mapper` | BpmNodeInstanceMapper |
| DTO | 业务名 + `DTO` / `Request` / `ResultDTO` | BatchGenerateRequest、OrgRelationDTO |
| 枚举 | 业务名 + `Enum` | BatchStepEnum、OrgLevelEnum |
| 工具类 | 功能名 + `Utils` | DateFormatUtils |
| 配置类 | 功能名 + `Config` | RedisConfig |
| 异常类 | `Biz` 前缀 + `Exception` | BizException、BizBatchException |

### 方法名

- Controller：动词开头，如 `generate`
- Service：业务动词，如 `generate`、`resolveSceneId`、`existsSuccess`、`startLog`、`updateStep`
- Mapper：SQL 语义命名，如 `batchInsert`、`deleteByTargetMonth`、`selectTemplates`
- 私有方法：`do`/`build`/`resolve`/`get`/`query` 前缀

### 变量名

- 成员变量：驼峰，依赖注入对象用接口首字母小写
- 常量：全大写下划线，如 `LOCK_TIMEOUT`、`LOCK_PREFIX`
- 局部变量：见名知意，如 `instanceCount`、`templateMap`

### 数据库命名

- 表名：小写下划线，如 `bpm_node_instance`、`bpm_scene`
- 字段名：大写下划线，如 `T_DAY`、`SCENE_ID`、`NODE_TYPE_ID`、`ORG_BUSI_BRANCH`
- Java 属性：驼峰，如 `tDay`、`sceneId`、`createTime`

---

## 四、实体类规范

### BaseEntity（基类）

位置：`barsms-bpm-infra/.../infra/model/entity/BaseEntity.java`

```java
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField("CREATED_AT")
    private Date createTime;

    @TableField("UPDATED_AT")
    private Date updateTime;

    @TableField("CREATED_BY")
    private String createBy;

    @TableField("UPDATED_BY")
    private String updateBy;

    @TableField("DELETE_FLAG")
    private String deleteFlag;  // "0" 为有效
}
```

### 业务实体类

- `@Data` + `@TableName("bpm_xxx")` + `extends BaseEntity`
- 主键：`@TableId(value = "ID", type = IdType.AUTO)`
- **所有字段必须显式标注 `@TableField`**，即使命名匹配
- 枚举字段：直接用枚举类型作为属性，配合枚举上的 `@EnumValue`
- 子类覆盖 BaseEntity 字段时，需重新声明 `@TableField`（如 `BpmOrgInfo` 用 `@TableField("IS_DELETED")` 映射 `deleteFlag`）

```java
@Data
@TableName("bpm_batch_exec_log")
public class BpmBatchExecLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("CURRENT_STEP")
    private BatchStepEnum currentStep;
}
```

---

## 五、Mapper XML 规范

### 文件组织

- 位置：`barsms-bpm-infra/src/main/resources/sqlmapper/`
- 文件名与 Mapper 接口同名
- `namespace` 为 Mapper 接口全限定名
- `application.yml` 配置：`mybatis-plus.mapper-locations: classpath*:/sqlmapper/*.xml`

### SQL 编写

- 标签 `id` 与接口方法名一致
- 批量插入：`<foreach collection="list" item="item" separator=",">`
- 条件分支：`<if test="xxx != null and xxx != ''">`，提供相反分支兜底
- IN 查询：`<foreach collection="xxx" item="id" open="(" separator="," close=")">`
- 参数引用：`#{paramName}`（预编译）或 `${batch.xxx}`（全局变量）
- 大于号转义：`&gt;`
- 表别名用短名：`t1`、`t_s`

---

## 六、枚举设计规范

位置：`barsms-bpm-common/.../common/enums/`

### 结构

- 普通 Java enum，不使用 Lombok
- 双字段设计：`code`（存储/传输值）+ `desc`/`description`（中文描述）
- 与 MyBatis-Plus 集成时，`code` 字段标注 `@EnumValue`
- 枚举值命名：全大写下划线
- 提供静态转换方法（如 `toShortName` / `toFullName`）

### 现有枚举

| 枚举 | 用途 | code 示例 |
|------|------|-----------|
| `BatchStepEnum` | 批次执行步骤 | `clear_data_1`、`insert_monthly_2` |
| `OrgLevelEnum` | 机构层级 | fullName + shortName（"总公司"/"总"） |
| `ProcDateTypeEnum` | 任务周期 | `每月`、`每日` |
| `ResponseCodeEnum` | 响应码 | 200、400、500 |

```java
public enum OrgLevelEnum {
    ROOT_COMPANY("总公司", "总"),
    BRANCH_COMPANY("分公司", "分"),
    CENTER_BRANCH_COMPANY("中支", "中"),
    SUB_BRANCH_COMPANY("支公司", "支");

    private final String fullName;
    private final String shortName;

    public static String toShortName(String fullName) { ... }
    public static String toFullName(String shortName) { ... }
}
```

### 使用规范

- SQL LIKE 模式必须使用枚举拼接，不硬编码字符串：
  ```java
  // 正确
  "%" + OrgLevelEnum.ROOT_COMPANY.getFullName() + "%"
  // 错误
  "%总公司%"
  ```

---

## 七、异常处理规范

### 自定义异常

位置：`barsms-bpm-common/.../common/exception/`

- 继承 `RuntimeException`，使用 `@Getter`
- 持有 `private final Integer code` 字段
- 三个构造器：`(String message)`、`(ResponseCodeEnum)`、`(Integer code, String message)`
- 业务区分：`BizException`（通用）、`BizBatchException`（批量跑批专用）

### 全局异常处理

`GlobalExceptionHandler`（`@RestControllerAdvice`）分级处理，统一返回 `ApiResult<Void>`：

| 异常类型 | 日志级别 | 返回码 |
|---------|---------|--------|
| `BizBatchException` | warn | ERROR(500) |
| `BizException` | warn | ERROR(500) |
| `MethodArgumentNotValidException` | - | BAD_REQUEST(400) |
| `Exception`（兜底） | error | ERROR(500) |

### 业务层抛异常

```java
throw new BizBatchException("场景不存在: " + sceneName);
throw new BizBatchException("当月同版本已生成过，请先删除历史数据");
```

---

## 八、日志规范

### 日志门面

- 全部使用 Lombok `@Slf4j`，不手动声明 `LoggerFactory`

### 日志级别

| 级别 | 场景 | 示例 |
|------|------|------|
| `info` | 关键业务节点、步骤完成、计数 | `log.info("总公司月度任务生成 {} 条", instances.size())` |
| `warn` | 业务异常、获取锁失败 | `log.warn("批量跑批业务异常: {}", e.getMessage())` |
| `error` | 系统异常、批量失败 | `log.error("批量生成失败", e)` |
| `debug` | 细粒度操作 | `log.debug("获取锁成功: {}", lockKey)` |

- 使用 `{}` 占位符
- 异常对象作为最后参数，不带 `{}`

---

## 九、配置规范

### 文件组织

- `application.yml`：主配置（Web 模块 `src/main/resources/`）
- `batch.properties`：业务自定义配置，通过 `@PropertySource` 加载
- `logback-spring.xml`：日志配置

### application.yml 关键配置

```yaml
server:
  port: 8080
  context-path: /api/bpm

mybatis-plus:
  mapper-locations: classpath*:/sqlmapper/*.xml
  type-aliases-package: com.cpic.barsms.bpm.infra.model.entity
  type-enums-package: com.cpic.barsms.bpm.common.enums
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### batch.properties

- 前缀 `batch.` + kebab-case 命名（如 `batch.head-office-code`）
- 列表用逗号分隔字符串
- 配置属性类：`@Data` + `@Component` + `@ConfigurationProperties(prefix = "batch")`
- 每个字段有默认值和 Javadoc 注释

---

## 十、其他规范

### 统一返回结果 ApiResult\<T\>

- 泛型包装，默认 `isSuccess=true`、`code=200`、`message="操作成功"`
- 静态工厂方法：`ok()` / `ok(data)` / `error(code, message)` 等
- 自动附带 `threadTraceId`

### Redis 分布式锁

- `RedisDistributedLock` 标注 `@Component`
- 锁 key 统一前缀 `batch:lock:`
- `setIfAbsent` + 过期时间（默认 300 秒）
- 释放锁用 Lua 脚本校验 value（UUID）防误删
- 模板方法：`executeWithLock(lockKey, timeout, LockCallback)`，支持 Lambda

### 事务规范

- `@Transactional(rollbackFor = Exception.class)` 显式指定
- 批量操作先删后插，失败记录执行日志后重新抛出异常触发回滚

### 幂等与执行日志

- 批量任务执行前先 `existsSuccess` 幂等校验
- 执行过程通过 `BpmBatchExecLogService` 记录步骤、时间、计数

### 工具类

- 静态方法为主，私有构造器
- `DateFormatUtils`：`parseDate` / `formatDate` / `addOneMonth` / `getNextMonthFirstDay`
- `RandomUtils`：`SecureRandom.getInstanceStrong()`，提供业务 ID 生成

---

## 十一、硬约束（不可违反）

1. **数据库字段不可修改**
2. **依赖注入**：使用 `@Autowired` 字段注入，不使用 `@RequiredArgsConstructor` 构造器注入
3. **Service 层**：必须接口-实现分离，接口在 `domain.service`，实现在 `domain.serviceimpl`
4. **实体类**：所有字段必须标注 `@TableField`
5. **主键**：统一 `@TableId(value = "ID", type = IdType.AUTO)`
6. **数据库列名**：大写下划线风格（如 `NODE_TYPE_ID`）
7. **通用 Bean**：`@Component` 标注以启用 Spring 自动检测（如 `RedisDistributedLock`）
8. **SQL LIKE 模式**：必须使用枚举拼接，不硬编码中文字符串
9. **任务周期/机构层级**：必须通过枚举管理（`ProcDateTypeEnum`、`OrgLevelEnum`）
10. **Service 层继承**：只有与数据库表命名对齐的 Service 接口才 `extends IService<实体>`，实现类 `extends ServiceImpl<对应Mapper, 实体>`；业务编排类 Service（如 `DailyTaskGeneratorService`、`MonthlyTaskGeneratorSevice`、`BatchGenerateService`）不继承。Service 接口和 ServiceImpl 命名必须与数据库表一一对应
