###自定义配置
War 插件默认使用 GBK 编码做为代码编码。当我们的代码中有中文字符，并且代码编码为非 GBK 编码时会报错`编码GBK的不可映射字符`此时我们需要自定义项目编译编码
 ```groovy
 //设置编码
 [compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
```
约定的项目结构也许并不能满足我们项目的情景，我们可以通过自定义配置来使得 Gradle 不使用约定配置编译项目：
我们随意变更一下上一篇[使用Gradle第一次构建Web应用](https://coderknock.com/blog/2017/03/13/%E4%BD%BF%E7%94%A8Gradle%E7%AC%AC%E4%B8%80%E6%AC%A1%E6%9E%84%E5%BB%BAWeb%E5%BA%94%E7%94%A8.html)中的项目结构，变更后的结构如下：
```bash
├─srcdiv
│  └─com
│      └─manning
│          └─gia
│              └─todo
│                  ├─model
│                  │      ToDoItem.java
│                  │
│                  ├─repository
│                  │      InMemoryToDoRepository.java
│                  │      ToDoRepository.java
│                  │
│                  └─web
│                          ToDoServlet.java
│
├─static
│  └─css
│          base.css
│          bg.png
│
└─webfiles
    ├─jsp
    │      index.jsp
    │      todo-list.jsp
    │
    └─WEB-INF
            web.xml
```
我们需要在 build.gradle 中添加如下配置：
```groovy
 //改变项目默认结构
 sourceSets {
 
     //设置源代码所在目录
     main {
         java {
             srcDirs = ['srcdiv']
         }
     }
 
     //设置测试代码所在目录
     test {
         java {
             srcDirs = ['testdiv']
         }
     }
 }
 
 //改变 Web 应用的源代码目录
 webAppDirName = 'webfiles'
 
 //将静态文件放到 static 路径，但是需要打包到 WAR 文件根目录下
 war {
     from 'static'
 }
```
这样我们的项目就可以正常编译并打包为可用 WAR 文件。

