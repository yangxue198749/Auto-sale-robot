# VMS

售货机运营管理系统（后台），Maven 多模块工程：vms-commons / vms-model / vms-orm / vms-service / vms-mq / vms-web。

技术栈：Java 8 + Spring 4.2 + Apache Cayenne 3.1 + Oracle + RabbitMQ + Shiro + Jetty 8。

### Development

```
$ git clone https://github.com/yangxue198749/Auto-sale-robot.git
$ cd Auto-sale-robot
```

#### 运行 cayenne-modeler（数据模型工具）
```
$ mvn cayenne-modeler:run
```

#### 本地启动工程
```
1. $ mvn clean install -DskipTests
2. $ mvn jetty:run
关闭：ctrl+c 或者 其他终端输入 mvn jetty:stop
```

#### 配置
启动前需要把 `vms-web/src/main/resources/setting.properties.example` 复制为
`setting.properties`，并填入你自己的 Oracle / RabbitMQ 连接信息。

#### 制作 docker 镜像
```
1. 使用 root 登陆到部署机
2. cd ~/vms
3. sh ./build.sh
4. 根据提示输入
```

### 3.5.0
> 

### 4.0.0
>