Why
----
试想一下，你做好了一个 Gradle 构建的 Web 应用，并且要分享给他人，让他人可以参与到开发中，但对方下载代码后安装了 Gradle 却发现应用未能正常使用。
经过多次长时候才发现原来是 Gradle 运行时版本不兼容。
怎样解决这个问题呢？

What
----
Gradle 包装器是 Gradle 的核心特性，能够让机器在没有安装 Gradle 运行时的情况下运行 Grade 构建。它也让构建脚本运行在一个指定的 Gradle 版本上。它是通过中心仓库下载对应版本的 Gradle 运行时来实现的。最终的目标是创造一个独立于系统、系统配置和 Gradle 版本的可靠的、可重复的构建。   

How
----
####配置包装器
1、创建一个包装器任务【此步骤也可跳过，Gradle会提供一个默认的 wrapper 任务，gradleVersion为当前机器上的版本】  
在 build.gradle 中添加以下代码：
```groovy
task wrapper(type:Wrapper){
    gradleVersion='3.4'
}
```
2、执行任务生成包装器文件
```bash
www.cdoerknock.com$ gradle wrapper
Starting a Gradle Daemon (subsequent builds will be faster)
> Configuring > 0/1 projects > root project > Compiling D:\Windows\Desktop\LearnGradle\Gradle包装器\project\build.gradle:wrapper

BUILD SUCCESSFUL

Total time: 14.749 secs
```
此时会生成 gradle 文件夹以及 gradlew、gradlew.bat 文件
```bash
|  //下面是 windows 或者 linux 执行 Gradle 命令的包装器脚本
│  gradlew
│  gradlew.bat
└──gradle
   └─wrapper
           //Gradle 包装器微类库，包含下载和解包 Gradle 运行时的逻辑
           gradle-wrapper.jar
           //包装器元信息，包含已下载 Gradle 运行时的存储位置和原始 URL
           gradle-wrapper.properties
```
获取到改代码，但是自己本机没有 Gradle 运行时的同事就可以执行对应操作系统的命令来执行，例如 Windows 系统使用 powershell【cmd也可以】：
```bash
//Linux 应该使用 .\gradlew appRun
PS D:\Windows\Desktop\LearnGradle\Gradle包装器\project>    .\gradlew.bat appRun
//从远程库下载对应版本的 Gradle 
Downloading https://services.gradle.org/distributions/gradle-3.4-bin.zip
//解压到默认目录 Linux 还会赋权限 
Unzipping C:\Users\zylia\.gradle\wrapper\dists\gradle-3.4-bin\aeufj4znodijbvwfbsq3044r0\gradle-3.4-bin.zip to C:\Users\zylia\.gradle\wrapper\dists\gradle-3.4-bin\aeufj4znodijbvwfbsq3044r0
:prepareInplaceWebAppFolder
:createInplaceWebAppFolder
:compileJava
:processResources NO-SOURCE
:classes
:prepareInplaceWebAppClasses
:prepareInplaceWebApp
:appRun
14:50:22 INFO  Jetty 9.2.15.v20160210 started and listening on port 9090
14:50:22 INFO  ToDo Application runs at:
14:50:22 INFO    http://localhost:9090/coderknock
Press any key to stop the server.
> Building 87% > :appRun
```
运行时的 Gradle 是通过 Gradle 项目的中心服务器下载并解压（该下载解压过程只会进行一次，之后会重用）到 C:\Users\用户名\.gradle\wrapper\dists (Linux是$HOME_DIR/.gradle/warpper/dists)目录下并且赋予相应权限后来进行构建的。这样就可以解决 `Why` 里提到的问题了。

Further
----
如果我们无法访问外网，或者不想解压到默认目录，我们可以通过在 build.gradle 脚本中添加一些自定义配置来修改这些内容【修改后需要重新执行`gradle wrapper`生成包装器】：
```groovy
task wrapper(type:Wrapper){
    gradleVersion='3.4'
    //获取 Gradle 安装包的 URL 这里可以通过下面的方式使用本地文件（从中央库下载比较慢，我们可以使用迅雷等工具下载后用这种方式比较快捷）
    distributionUrl='file:///D:/Windows/Desktop/LearnGradle/Gradle包装器/project/gradle-3.4-bin.zip'
    //C:\Users\用户名\.gradle linux下是 $HOME_DIR/.gradle 的相对路径
    distributionPath='gradle-dists'
}
```