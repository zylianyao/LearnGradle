###Gradle Web 插件
Gradle 提供了打包 war 包的插件，可以将 Web 应用部署到本地 Servlet 容器中。
####使用 War 和 Jetty 插件
Gradle 对构建和运行 Web 应用提供了对应扩展的支持。
下面我们介绍 War 和 Jetty 插件。
War 插件扩展自 Java 插件，为 Web 应用部署和组装 War 包添加了约定和支持。
Jetty 是一个流行的轻量级开源 Web 容器，Gradle 提供了 Jetty 插件，方便 Jetty 的使用。Jetty 扩展自 War 插件，为部署一个 Web 应用和运行 Web 应用提供了对应的任务。
#####War插件
War 插件扩展自 Java 插件，所以我们在引入 War 插件后无需再显式引入 Java插件（引入也不会影响，应用插件是一个幂等【任意多次执行所产生的影响均与一次执行的影响相同】操作，某一个指定的插件只会运行一次）
使用插件只需：
```groovy
apply plugin:'war'
```
对于项目本身，除了 Java 插件的约定外，还会应用 Web 应用文件，并打包项目为 WAR 文件而不是 JAR 文件。Web 应用默认约定的源代码目录是`src/main/webapp`
添加相关 Web 文件之后我们的文件目录如下所示：
```
└─src
    └─main
        ├─java
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
        └─webapp
            ├─css
            │      base.css
            │      bg.png
            │
            ├─jsp
            │      index.jsp
            │      todo-list.jsp
            │
            └─WEB-INF
                    web.xml

```
当我们构建的时候会发现报错：
```bash
www.coderknock.com$ gradle build
:compileJava
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:7: 错误: 程序包javax.servlet不存在
import javax.servlet.RequestDispatcher;
                    ^
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:8: 错误: 程序包javax.servlet不存在
import javax.servlet.ServletException;
                    ^
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:9: 错误: 程序包javax.servlet.http不存在
import javax.servlet.http.HttpServlet;
                         ^
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:10: 错误: 程序 包javax.servlet.http不存在
import javax.servlet.http.HttpServletRequest;
                         ^
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:11: 错误: 程序 包javax.servlet.http不存在
import javax.servlet.http.HttpServletResponse;
                         ^
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:16: 错误: 找不 到符号
public class ToDoServlet extends HttpServlet {
                                 ^
  符号: 类 HttpServlet
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:22: 错误: 找不 到符号
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                           ^
  符号:   类 HttpServletRequest
  位置: 类 ToDoServlet
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:22: 错误: 找不 到符号
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                                                       ^
  符号:   类 HttpServletResponse
  位置: 类 ToDoServlet
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:22: 错误: 找不 到符号
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                                                                                            ^
  符号:   类 ServletException
  位置: 类 ToDoServlet
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:29: 错误: 找不 到符号
    private String processRequest(String servletPath, HttpServletRequest request) {
                                                      ^
  符号:   类 HttpServletRequest
  位置: 类 ToDoServlet
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:21: 错误: 方法 不会覆盖或迪殖嘈偷姆椒?
      @Override
    ^
D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\src\main\java\com\manning\gia\todo\web\ToDoServlet.java:25: 错误: 找不 到符号
        RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        ^
  符号:   类 RequestDispatcher
  位置: 类 ToDoServlet
12 个错误
:compileJava FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava'.
> Compilation failed; see the compiler error output for details.

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

BUILD FAILED

Total time: 1.21 secs
```
实现 Web 应用所需要的类并未包括在标准 JDK 中，例如 javax.servlet.HttpServlet。所以在运行构建前我们需要添加外部依赖。我们为 War 插件引入两个依赖，Servlet 依赖使用到的是 `providedCompile`，它表示该依赖在编译时需要，但实际运行时由运行时的环境提供。在本项目中就是 Jetty 提供，这样改依赖的相关文件就不会打包到 WAR 文件中了（避免 WAR 包过大以及可能出现的 JAR 包重复从而导致找不到相关类）。向 JSTL 库等在编译时不需要，运行时需要的库，我们需要标记为 runtime ，这样他们会被包含到 WAR 文件中以供运行时调用。我们在 build.gradle 中添加下面依赖配置：
```groovy
dependencies{
     providedCompile 'javax.servlet:javax.servlet-api:3.1.0' //书中是该值，'javax.servlet:servlet-api:2.5' 但是该库比较旧，我们使用最新的 Servlet 依赖  
     runtime 'javax.servlet:jstl:1.1.2'
 }
```
这时我们运行构建命令，发现还是有报错：
```bash
www.coderknock.com$ gradle build
> Configuring > 0/1 projects > root project > Compiling D:\Windows\Desktop\LearnGradle\用Gradle做Web开发\build.gradle in:compileJava FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Could not resolve all dependencies for configuration ':compileClasspath'.
> Cannot resolve external dependency javax.servlet:servlet-api:3.0.1 because no repositories are defined.
  Required by:
      project :

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

BUILD FAILED

Total time: 1.332 secs
```
这是因为我们没有什么资源库，在 build.gradle 中继续添加：
```groovy
repositories {
    mavenCentral()
}
```
继续编译，编译成功
```bash
www.coderknock.com$ gradle build
:compileJava
:processResources NO-SOURCE
:classes
:war//由 War 插件提供的任务来打 WAR 包
:assemble
:compileTestJava NO-SOURCE
:processTestResources NO-SOURCE
:testClasses UP-TO-DATE
:test NO-SOURCE
:check UP-TO-DATE
:build

BUILD SUCCESSFUL

Total time: 7.904 secs
```
War 插件确保了打包的 WAR 文件遵循由 Java EE 规范定义的标准结构。其中主要做的任务如下：
1. 将 Web 应用源代码目录 src/main/webapp 的内容拷贝到 WAR 文件的根目录
2. 将编译后的 class 文件放入到 WEB-INF/classes 下
3. 通过依赖定义的运行时类库（如上面的）`javax.servlet:jstl:1.1.2` 拷贝到 WEB-INF/lib 下

打包后的目录如下：
```bash
│
├─.gradle
│  ├─3.4
│  │  ├─file-changes
│  │  │      last-build.bin
│  │  │
│  │  ├─fileContent
│  │  │      annotation-processors.bin
│  │  │      fileContent.lock
│  │  │
│  │  └─taskHistory
│  │          fileHashes.bin
│  │          fileSnapshots.bin
│  │          jvmClassSignatures.bin
│  │          taskHistory.bin
│  │          taskHistory.lock
│  │
│  └─buildOutputCleanup
│          built.bin
│          cache.properties
│          cache.properties.lock
│
├─build
│  ├─classes
│  │  └─main
│  │      └─com
│  │          └─manning
│  │              └─gia
│  │                  └─todo
│  │                      ├─model
│  │                      │      ToDoItem.class
│  │                      │
│  │                      ├─repository
│  │                      │      InMemoryToDoRepository.class
│  │                      │      ToDoRepository.class
│  │                      │
│  │                      └─web
│  │                              ToDoServlet$ToDoListStats.class
│  │                              ToDoServlet.class
│  │
│  ├─libs
│  │      用Gradle做Web开发.war
│  │
│  └─tmp
│      ├─compileJava
│      └─war
│              MANIFEST.MF
│
│
└─src
    └─main
        ├─java
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
        └─webapp
            ├─css
            │      base.css
            │      bg.png
            │
            ├─jsp
            │      index.jsp
            │      todo-list.jsp
            │
            └─WEB-INF
                    web.xml
```
其中打包好的 WAR 文件的目录结构如下：
```bash
├─css
│      base.css
│      bg.png
│
├─jsp
│      index.jsp
│      todo-list.jsp
│
├─META-INF
│      MANIFEST.MF
│
└─WEB-INF
    │  web.xml
    │
    ├─classes
    │  └─com
    │      └─manning
    │          └─gia
    │              └─todo
    │                  ├─model
    │                  │      ToDoItem.class
    │                  │
    │                  ├─repository
    │                  │      InMemoryToDoRepository.class
    │                  │      ToDoRepository.class
    │                  │
    │                  └─web
    │                          ToDoServlet$ToDoListStats.class
    │                          ToDoServlet.class
    │
    └─lib
            jstl-1.2.jar
```
可以看到其结构正如我们分析的一样。
