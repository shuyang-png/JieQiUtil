# JieQiUtil

JieQiUtil 是一个用于计算二十四节气交接时刻的 Java 工具库。

当前实现使用截断 VSOP87 地球日心经度计算太阳视黄经，并加入章动、光行差、Delta T 修正，再通过二分搜索定位目标黄经到达的时间。默认输出为北京时间（UTC+8），精确到秒。

## 功能

- 查询指定年份、指定节气的交接时刻
- 支持返回 UTC 秒级时间戳
- 支持将结果转换到指定 `ZoneOffset`
- 提供二十四节气名称、黄经和常见日期范围
- 提供节气所在区间判断：冬至后夏至前 / 夏至后冬至前

## 环境要求

- JDK 8 或更高版本
- Maven 3.6 或更高版本

## 快速开始

```java
import io.github.shuyang.util.JieQiUtil;

import java.time.LocalDateTime;

public class Demo {
    public static void main(String[] args) {
        LocalDateTime liChun = JieQiUtil.getJieQi(2024, "立春");
        System.out.println(liChun); // 2024-02-04T16:27:05
    }
}
```

## API

### 获取北京时间节气

```java
LocalDateTime time = JieQiUtil.getJieQi(2024, "冬至");
```

### 获取指定时区节气

```java
import java.time.ZoneOffset;

LocalDateTime utcTime = JieQiUtil.getJieQi(2024, "冬至", ZoneOffset.UTC);
```

### 获取 UTC 时间戳

```java
long epochSecond = JieQiUtil.getJieQiEpochSecond(2024, "冬至");
```

### 获取所有节气名称

```java
for (String name : JieQiUtil.getJieQiNames()) {
    System.out.println(name + " " + JieQiUtil.getJieQi(2024, name));
}
```

### 判断节气区间

```java
int period = JieQiUtil.checkPeriod("立春");
```

返回值：

- `0`：冬至后，夏至前
- `1`：夏至后，冬至前
- `2`：非法节气

## 构建与验证

```bash
mvn test
```

也可以直接使用 JDK 编译并运行 smoke test：

```bash
javac -encoding UTF-8 -d target/test-classes \
  src/main/java/io/github/shuyang/entity/*.java \
  src/main/java/io/github/shuyang/util/*.java \
  src/test/java/io/github/shuyang/util/JieQiUtilSmokeTest.java

java -cp target/test-classes io.github.shuyang.util.JieQiUtilSmokeTest
```

打印指定年份的二十四节气：

```bash
java -cp target/test-classes io.github.shuyang.util.JieQiUtilSmokeTest print 2024
```

## 算法说明

二十四节气由太阳视黄经每隔 15 度的位置定义：

- 春分：0 度
- 清明：15 度
- 夏至：90 度
- 秋分：180 度
- 冬至：270 度
- 立春：315 度

`SolarCalculationEngine` 的计算流程：

1. 将 UTC 时间转换为儒略日。
2. 使用 Delta T 将 UTC 近似转换为 TT。
3. 使用截断 VSOP87 计算地球日心经度。
4. 转换为太阳地心真黄经。
5. 修正章动、光行差和 FK5 小项，得到太阳视黄经。
6. 在节气常见日期范围内对目标黄经做秒级二分搜索。

这种实现不依赖在线服务，也不需要外部天文库，适合工具库直接使用。若后续需要更高精度，可继续扩展 VSOP87 项数或接入更完整的历算模型。

## 示例输出

2024 年部分节气（北京时间）：

```text
立春 2024-02-04T16:27:05
春分 2024-03-20T11:06:27
夏至 2024-06-21T04:50:51
秋分 2024-09-22T20:43:35
冬至 2024-12-21T17:20:24
```

## 许可证

本项目使用 MIT License。
