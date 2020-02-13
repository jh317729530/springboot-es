# springboot-es

## 结构说明

```
└── gunn
    └── springbootes
        ├── Application.java   启动类
        ├── CanalClientTest.java
        ├── Main.java
        ├── annotation   注解
        ├── business  业务相关，可理解为mybatis的mapper包
        ├── configuration 配置包
        ├── constant
        ├── elasticsearch  es封装
        │   ├── BaseDocument.java  entity包下的实体类都需要继承该类，包含所有除业务相关外的es字段
        │   ├── DocumentOperation.java  封装文档的基本操作，增删查改
        │   ├── IndexOperation.java 封装索引的基本操作
        │   └── Property.java 对应创建Mappings下的Property字段
        ├── entity  实体，即es中的文档
        └── util  工具
```

## 使用说明

-   entity为es文档的基本结构，每个entity类都需要继承BaseDocument。
-   DocumentOperation类包含了文档增、删、查、改的基本操作，更复杂的查询需要自行实现。