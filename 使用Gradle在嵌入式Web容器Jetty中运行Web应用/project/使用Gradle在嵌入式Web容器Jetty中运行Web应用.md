在[使用Gradle第一次构建Web应用](https://coderknock.com/blog/2017/03/13/%E4%BD%BF%E7%94%A8Gradle%E7%AC%AC%E4%B8%80%E6%AC%A1%E6%9E%84%E5%BB%BAWeb%E5%BA%94%E7%94%A8.html)的代码基础上我们进行修改
###Jetty 插件
在 Maven 等构建的项目中，我们要使用 Jetty 做嵌入式 Web 容器运行 Web 应用，通常需要添加 Jetty 相关依赖以及进行类似下面代码配置：
```java
    package com.coderknock.jettystudy; 
      
    import org.eclipse.jetty.server.Server;  
    import org.eclipse.jetty.webapp.WebAppContext;  
      
    public class WebAppContextWithFolderServer {  
        public static void main(String[] args) throws Exception {  
            Server server = new Server(8080);  
      
            WebAppContext context = new WebAppContext();  
            context.setContextPath("/myapp");  
            context.setDescriptor("E:/share/test/struts2-blank/WEB-INF/web.xml");  
            context.setResourceBase("E:/share/test/struts2-blank");  
            context.setParentLoaderPriority(true);  
            server.setHandler(context);  
      
            server.start();  
            server.join();  
        }  
    }  
```    
在 Gradle 构建的项目中，我们可以使用 Jetty 插件从而省略相关依赖的引入以及上面代码的编写 build.gradle：
```groovy
apply plugin:'jetty'
```
通过 Gradle 的 API 一个插件可以访问另一个插件的配置，所以就可以减少相当部分的代码。
在添加了 Jetty 插件后我们运行项目【为了避免不必要的麻烦，我们将项目的目录改为了 project 避免使用中文】：
```groovy
www.coderknock.com$ gradle jettyRun
 Starting a Gradle Daemon (subsequent builds will be faster)
 The Jetty plugin has been deprecated and is scheduled to be removed in Gradle 4.0. Consider using the Gretty (https://github.com/akhikhl/gretty) plugin instead.
         at build_6ecrowvh1t5jyzhh29knepzxf.run(D:\Windows\Desktop\LearnGradle\使用Gradle在嵌入式Web容器Jetty中运行Web应 用\project\build.gradle:2)
 :compileJava
 :processResources NO-SOURCE
 :classes
 > Building 75% > :jettyRun > Running at http://localhost:8080/project
```
此时我们通过  http://localhost:8080/project 就可以访问我们的项目了，通过 `Ctrl + c` 可以停止项目。
我们可以通过一些配置来修改 Jetty 的配置例如下面配置可以修改启动的项目名以及端口：
```groovy
jettyRun {
    httpPort = 9091
    contextPath = 'coderknockJetty'
}
```
###Gretty 插件
通过上面的编译输出我们可以看到 Jetty 插件在 Gradle 4.0 中将会被删除，推荐使用 [Gretty 插件](http://akhikhl.github.io/gretty-doc/Getting-started.html)，我们再次修改项目 build.gradle 将 `apply plugin:'jetty'` 更改为 `apply from: 'https://raw.github.com/akhikhl/gretty/master/pluginScripts/gretty.plugin'`并删除或注释掉`jettyRun相关配置`，然后运行项目【需要联网下载相关依赖】：
```bash
 www.coderknock.com$ gradle appRun
 :prepareInplaceWebAppFolder UP-TO-DATE
 :createInplaceWebAppFolder UP-TO-DATE
 :compileJava UP-TO-DATE
 :processResources NO-SOURCE
 :classes UP-TO-DATE
 :prepareInplaceWebAppClasses UP-TO-DATE
 :prepareInplaceWebApp UP-TO-DATE
 :appRun
 15:52:07 INFO  Jetty 9.2.15.v20160210 started and listening on port 8080
 15:52:07 INFO  ToDo Application runs at:
 15:52:07 INFO    http://localhost:8080/project
 Press any key to stop the server.
 > Building 87% > :appRun 
```
Gretty 自定义配置与 Jetty 大致相同：
```groovy
//gretty 配置 更详细的文档可以查看 http://akhikhl.github.io/gretty-doc/
    gretty {
        httpPort = 9090
        contextPath = 'coderknock'
    }
```
[相关代码](https://github.com/zylianyao/LearnGradle/tree/master/%E4%BD%BF%E7%94%A8Gradle%E5%9C%A8%E5%B5%8C%E5%85%A5%E5%BC%8FWeb%E5%AE%B9%E5%99%A8Jetty%E4%B8%AD%E8%BF%90%E8%A1%8CWeb%E5%BA%94%E7%94%A8/project)
