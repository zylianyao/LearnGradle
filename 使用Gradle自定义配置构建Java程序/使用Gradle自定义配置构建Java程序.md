### 自定义源代码路径、编译输出路径

某些情况下默认的源代码路径等可能不符合我们项目的结构，这时除了修改项目结构外，我们还可以自定义源代码路径等配置。

build.gradle

```groovy
//使用 Java 插件
//默认在 src/main/java下查找源代码
apply plugin: 'java'
//定义项目的版本
version = 0.1
//设置 Java 兼容版本
sourceCompatibility = 1.8
//jar 包相关配置
jar {
    //清单文件配置
    manifest {
        //启动类
        attributes 'Main-Class': 'com.manning.gia.todo.ToDoApp'
    }
}
//改变项目默认结构
sourceSets {

    //设置源代码所在目录
    main {
        java {
            srcDirs = ['src']
        }
    }

    //设置测试代码所在目录
    test {
        java {
            srcDirs = ['test']
        }
    }
}

//改变编译目录
buildDir = 'out'
```

我们将源代码从默认配置的路径转移到 src 下，然后进行编译（gradle build）

```bash
│  build.gradle
│  使用Gradle自定义配置构建Java程序.md
│
├─.gradle
│  ├─3.4
│  │  ├─file-changes
│  │  │      last-build.bin
│  │  │
│  │  ├─fileContent
│  │  │      fileContent.lock
│  │  │
│  │  └─taskHistory
│  │          fileHashes.bin
│  │          fileSnapshots.bin
│  │          taskHistory.bin
│  │          taskHistory.lock
│  │
│  └─buildOutputCleanup
│          built.bin
│          cache.properties
│          cache.properties.lock
│
├─out
│  ├─classes
│  │  └─main
│  │      └─com
│  │          └─manning
│  │              └─gia
│  │                  └─todo
│  │                      │  ToDoApp.class
│  │                      │
│  │                      ├─model
│  │                      │      ToDoItem.class
│  │                      │
│  │                      ├─repository
│  │                      │      InMemoryToDoRepository.class
│  │                      │      ToDoRepository.class
│  │                      │
│  │                      └─utils
│  │                              CommandLineInput.class
│  │                              CommandLineInputHandler$1.class
│  │                              CommandLineInputHandler.class
│  │
│  ├─libs
│  │      使用Gradle自定义配置构建Java程序-0.1.jar
│  │
│  └─tmp
│      ├─compileJava
│      └─jar
│              MANIFEST.MF
│
└─src
    └─com
        └─manning
            └─gia
                └─todo
                    │  ToDoApp.java
                    │
                    ├─model
                    │      ToDoItem.java
                    │
                    ├─repository
                    │      InMemoryToDoRepository.java
                    │      ToDoRepository.java
                    │
                    └─utils
                            CommandLineInput.java
                            CommandLineInputHandler.java

```

此时我们可以看到，编译输出目录变更为 out ，并且正常编译，且可以执行相关程序。

### 配置和使用外部依赖

 下面我们使用 Apache Commons Lang 库来替换我们的代码（这部分内容不涉及到gradle，故不详细阐述）

很显然，我们引入了第三方库，需要告知 Gradle 否则编译是不能通过的，会被以下错误：

```bash
www.coderknock.com$ gradle build
:compileJava
D:\Windows\Desktop\LearnGradle\使用Gradle自定义配置构建Java程序\src\com\manning\gia\todo\ToDoApp.java:5: 错误: 程序包org.apache.commons.lang3不存在
import org.apache.commons.lang3.CharUtils;
                               ^
D:\Windows\Desktop\LearnGradle\使用Gradle自定义配置构建Java程序\src\com\manning\gia\todo\ToDoApp.java:17: 错误: 找不到符号
            command = CharUtils.toChar(input, DEFAULT_INPUT);
                      ^
  符号:   变量 CharUtils
  位置: 类 ToDoApp
2 个错误
:compileJava FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava'.
> Compilation failed; see the compiler error output for details.

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

BUILD FAILED

Total time: 1.517 secs
```

我们可以使用 Gradle 的依赖来实现。

在 build.gradle 添加

```groovy
//定义仓库，此处使用的是 Maven 的仓库
//可以在 http://mvnrepository.com/ 或者 http://search.maven.org 【可能需要翻墙】 来查询相关依赖
repositories {
    mavenCentral()
}

/**
 定义依赖相当于maven中的
 <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
 <dependencies>
     <dependency>
         <groupId>org.apache.commons</groupId>
         <artifactId>commons-lang3</artifactId>
         <version>3.5</version>
     </dependency>
 </dependencies>
 */
dependencies {
    //group对应 groupId name 对应 artifactId  compile指定的是依赖使用的范围
    compile group: 'org.apache.commons', name: 'commons-lang3', version: '3.5'
}
```

再次进行编译

```groovy
www.coderknock.com$ gradle build
//解析依赖，如果依赖不存在，则会在使用到该依赖时下载该依赖【再次构建时就不会下载了，除非手动删除了下载的依赖或者更新了依赖的版本】
> Configuring > 0/1 projects > root project > Compiling D:\Windows\Desktop\LearnGradle\使用Gradle自定义配置构建Java程序\:compileJava
//此处就是从Maven中央库下载相关依赖
Download https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.5/commons-lang3-3.5.pom
Download https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.5/commons-lang3-3.5.jar
:processResources NO-SOURCE
:classes
:jar UP-TO-DATE
:assemble UP-TO-DATE
:compileTestJava NO-SOURCE
:processTestResources NO-SOURCE
:testClasses UP-TO-DATE
:test NO-SOURCE
:check UP-TO-DATE
:build UP-TO-DATE

BUILD SUCCESSFUL

Total time: 40.259 secs
```

此时就可以正常编译及运行程序了。
[相关代码](https://github.com/zylianyao/LearnGradle/tree/master/%E4%BD%BF%E7%94%A8Gradle%E8%87%AA%E5%AE%9A%E4%B9%89%E9%85%8D%E7%BD%AE%E6%9E%84%E5%BB%BAJava%E7%A8%8B%E5%BA%8F)