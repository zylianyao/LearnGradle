###目录结构
```
│  build.gradle
└─src
    └─main
        └─java
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
###build.gradle
```groovy
//使用 Java 插件
//默认在 src/main/java下查找源代码
apply plugin: 'java'
```
###执行构建命令
```shell
www.coderknock.com$ gradle build
Starting a Gradle Daemon, 1 incompatible and 1 stopped Daemons could not be reused, use --status for details
//编译 Java 代码
:compileJava	
//处理资源【将 src/main/resource 下文件拷贝到 classes 此处没有该文件夹所以被标记为 NO-SOURCE】
:processResources NO-SOURCE
:classes
//打 jar 包
:jar
:assemble
//编译 Java 测试代码
:compileTestJava NO-SOURCE
//处理测试资源【将 src/test/resource 下文件拷贝到 classes 此处没有该文件夹所以被标记为 NO-SOURCE】
:processTestResources NO-SOURCE
:testClasses UP-TO-DATE
//进行单元测试
:test NO-SOURCE
:check UP-TO-DATE
:build

BUILD SUCCESSFUL

Total time: 13.767 secs
```
每一行都是 Java 插件提供的一个可执行任务，`UP-TO-DATE` 代表任务被跳过。

###编译之后
```
│  build.gradle
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
├─build
│  ├─classes  【此目录即编译的 Java 的 class 文件的目录】
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
│  │      Project.jar 【打包的 jar 包，名称是项目的目录名】
│  │
│  └─tmp 【打 jar 包时使用的临时文件】
│      ├─compileJava
│      └─jar
│              MANIFEST.MF
│
└─src
    └─main
        └─java
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
###运行项目
```shell
www.coderknock.com$ java -cp build/classes/main/  com.manning.gia.todo.ToDoApp

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
> i
Please enter the name of the item:
> test
Successfully inserted to do item with ID 1.

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
> a
1: test [completed: false]

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
> exit
Please select a valid option!

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
> e
```
再来运行下 jar 
```shell
www.coderknock.com$ java -jar Project.jar
Project.jar中没有主清单属性
```
我们发现没有正确运行，下这是因为，我们没有在构建脚本中申明要生成清单文件MANIFEST.MF 下面我们来修改一下 build.gradle  相关的配置。
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
```
再次编译完成后，build/lib 目录下的 jar 包名为 Project-0.1.jar
```shell
www.coderknock.com$ java -jar Project-0.1.jar

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
```
可以看到，jar 包可以正常运行。

**[相关代码](https://github.com/zylianyao/LearnGradle/tree/master/%E4%BD%BF%E7%94%A8Gradle%E7%AC%AC%E4%B8%80%E6%AC%A1%E6%9E%84%E5%BB%BAJava%E7%A8%8B%E5%BA%8F)**