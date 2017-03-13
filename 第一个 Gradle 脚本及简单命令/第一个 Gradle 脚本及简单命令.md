### 第一个 Gradle 脚本及简单命令
```Groovy
task helloworld {
    doLast {
        print 'Hello world!'
    }
}
/* <<这种方式将在 Gradle5.0 中删除，不建议使用 */
task helloworldSort << {
    print 'Hello world!'
}

task startSession << {
    println 'startSession'
    chant()
}

def chant() {
    /*使用 gradle -q task名称 的方式运行会没有效果
    * 使用 gradle task名称 的方式运行才有效果*/
    ant.echo(message: 'Ant 任务的引用')
    println 'chant'
}

3.times {
    task "yayGradle$it" << {
        println '动态任务的定义' + it
    }
}

/* dependsOn 说明 task 之间的依赖关系，Gradle 会确保被依赖的 task 总会在定义该依赖的 task 之前执行 */
yayGradle0.dependsOn startSession
yayGradle2.dependsOn yayGradle1, yayGradle0
/* 任务执行的顺序 startSession -> yayGradle0 -> yayGradle1 -> yayGradle2 -> groupTherapy */
task groupTherapy(dependsOn: yayGradle2) << {
    println 'groupTherapy'
}
```
`gradle -q task名称` 使Gradle只显示对应 task 相关信息

`task` 代表一个任务 `doLast` 代表 task 最后执行的一个 action  `doLast` 可以使用 `<< `更简单的来表达

`gradle task --all` 查询所有的可以知晓的任务

``` shell
www.coderknock.com$ gradle task --all
The Task.leftShift(Closure) method has been deprecated and is scheduled to be removed in Gradle 5.0. Please use Task.doLast(Action) instead.
        at build_88ntbwtq2gz525a8g5uy5gmc2.run(D:\Windows\Desktop\LearnGradle\HelloWorld\build.gradle:7)
:tasks

------------------------------------------------------------
All tasks runnable from root project
------------------------------------------------------------
```

 构建的 setip 任务，帮助初始化 Gradle 的构建（比如生成 build.gradle 文件）

``` shell
Build Setup tasks
-----------------
init - Initializes a new Gradle build. [incubating]
wrapper - Generates Gradle wrapper files. [incubating]
```

帮助任务组，这里列出了任务的名称以及对应描述

``` shell
Help tasks
----------
buildEnvironment - Displays all buildscript dependencies declared in root project 'HelloWorld'.
components - Displays the components produced by root project 'HelloWorld'. [incubating]
dependencies - Displays all dependencies declared in root project 'HelloWorld'.
dependencyInsight - Displays the insight into a specific dependency in root project 'HelloWorld'.
dependentComponents - Displays the dependent components of components in root project 'HelloWorld'. [incubating]
help - Displays a help message.
model - Displays the configuration model of root project 'HelloWorld'. [incubating]
projects - Displays the sub-projects of root project 'HelloWorld'.
properties - Displays the properties of root project 'HelloWorld'.
tasks - Displays the tasks runnable from root project 'HelloWorld'.
```

没有分类的任务【目前列出的是我们刚刚声明的任务，自己生命的任务也可以设置任务组】，后期会学习如何添加描述信息

``` shell
Other tasks
-----------
groupTherapy
helloworld
helloworldSort
startSession
yayGradle0
yayGradle1
yayGradle2

BUILD SUCCESSFUL

Total time: 1.29 secs
```
每个构建脚本都会默认暴露 Help tasks 任务组，如果某个 task 不属于一个任务组，就会显示在 Other tasks 中。

### 任务执行

```bash
www.coderknock.com$ gradle yayGradle0 groupTherapy
:startSession
startSession
[ant:echo] Ant 任务的引用
chant
:yayGradle0
动态任务的定义task ':yayGradle0'
:yayGradle1
动态任务的定义task ':yayGradle1'
:yayGradle2
动态任务的定义task ':yayGradle2'
:groupTherapy
groupTherapy

BUILD SUCCESSFUL

Total time: 1.61 secs
```

```bash
www.coderknock.com$ gradle groupTherapy
:startSession
startSession
[ant:echo] Ant 任务的引用
chant
:yayGradle0
动态任务的定义task ':yayGradle0'
:yayGradle1
动态任务的定义task ':yayGradle1'
:yayGradle2
动态任务的定义task ':yayGradle2'
:groupTherapy
groupTherapy

BUILD SUCCESSFUL

Total time: 1.614 secs
```
我们可以看到 `gradle yayGradle0 groupTherapy`与`gradle groupTherapy`两者的运行结果是相同的，这是因为 `groupTherapy`与`yayGradle0`直接有依赖关系，这样可以保证被依赖的任务只执行一次，并且任务间的执行顺序按照依赖关系进行。

```bash
www.coderknock.com$ gradle gT
:startSession
startSession
[ant:echo] Ant 任务的引用
chant
:yayGradle0
动态任务的定义task ':yayGradle0'
:yayGradle1
动态任务的定义task ':yayGradle1'
:yayGradle2
动态任务的定义task ':yayGradle2'
:groupTherapy
groupTherapy

BUILD SUCCESSFUL

Total time: 1.43 secs
```
Gradle 提供了以驼峰式缩写在命令行上运行命令的方式，我们可以通过如上面一样的`gradle gT`这样的命令来达到`gradle groupTherapy`这样的效果。其中要确保任务名字的缩写是唯一的不然会有如下报错效果：
```bash
www.coderknock.com$ gradle yG

FAILURE: Build failed with an exception.

* What went wrong:
Task 'yG' is ambiguous in root project 'HelloWorld'. Candidates are: 'yayGradle0', 'yayGradle1', 'yayGradle2'.

* Try:
Run gradle tasks to get a list of available tasks. Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

BUILD FAILED

Total time: 1.296 secs
```
我们应该使用`yG0`来代替`yayGradle0`
```bash
www.coderknock.com$ gradle groupTherapy -x yG0
:yayGradle1
动态任务的定义task ':yayGradle1'
:yayGradle2
动态任务的定义task ':yayGradle2'
:groupTherapy
groupTherapy

BUILD SUCCESSFUL

Total time: 1.378 secs
```
使用`-x`参数，我们可以智能排除任务中对应的依赖任务及其相关依赖

使用`gradle -h`我们可以查询`gradle可用参数`
```bash
www.coderknock.com$ gradle -h

USAGE: gradle [option...] [task...]

-?, -h, --help          显示此帮助消息。Shows this help message.
-a, --no-rebuild        不重新生成项目依赖项。Do not rebuild project dependencies.
-b, --build-file        指定生成文件。Specifies the build file.
-c, --settings-file     指定的设置文件。Specifies the settings file.
--configure-on-demand   只有相关的项目中运行此生成配置。这意味着更快的生成，实现大型多项目生成。[孵化中]Only relevant projects are configured in this build run. This means faster build for large multi-project builds. [incubating]
--console               指定哪种类型的控制台输出生成。值是 '普通'、 '自动' （默认值） 或 '详细'。Specifies which type of console output to generate. Values are 'plain', 'auto' (default) or 'rich'.
--continue              当一个任务执行失败后继续执行任务。Continues task execution after a task failure.
-D, --system-prop       设置系统属性的 JVM (例如 Dmyprop = myvalue)。Set system property of the JVM (e.g. -Dmyprop=myvalue).
-d, --debug             在调试模式下打印日志（包括异常堆栈）。 Log in debug mode (includes normal stacktrace).
--daemon                使用 Gradle 守护进程来进行构建，如果没有运行的守护进程则启动一个。Uses the Gradle Daemon to run the build. Starts the Daemon if not running.
--foreground            在前台启动 Gradle 守护进程。[孵化中]Starts the Gradle Daemon in the foreground. [incubating]
-g, --gradle-user-home  指定 gradle 用户的主目录。Specifies the gradle user home directory.
--gui                   启动 Gradle GUI。Launches the Gradle GUI.
-I, --init-script       指定初始化脚本。Specifies an initialization script.
-i, --info              将日志级别设置为info。Set log level to info.
--include-build         Includes the specified build in the composite. [incubating]
-m, --dry-run           禁用所有任务的操作来进行构建。Runs the builds with all task actions disabled.
--max-workers           配置 Gradle 允许使用的并发的数目。[孵化]Configure the number of concurrent workers Gradle is allowed to use. [incubating]
--no-daemon             不适用守护进程构建。Do not use the Gradle Daemon to run the build.
--no-scan               Disables the creation of a build scan. [incubating]
--offline               构建而不访问网络资源（使用本地缓存）。The build should operate without accessing network resources.
-P, --project-prop      设置项目属性的生成脚本 (例如 Pmyprop = myvalue)。Set project property for the build script (e.g. -Pmyprop=myvalue).
-p, --project-dir       Gradle 指定的起始目录。默认为当前目录。Specifies the start directory for Gradle. Defaults to current directory.
--parallel              并行构建项目。Gradle将尝试测定最佳线程数。Build projects in parallel. Gradle will attempt to determine the optimal number of executor threads to use. [incubating]
--profile               配置构建生成时间，并且在<build_dir>/reports/profile 生成对应的报告。Profiles build execution time and generates a report in the <build_dir>/reports/profile directory.
--project-cache-dir     指定特定于项目的缓存目录。默认值为.gradle 的项目根目录中。Specifies the project-specific cache directory. Defaults to .gradle in the root project directory.
-q, --quiet             Log errors only.
--recompile-scripts     重新编译构建脚本。Force build script recompiling.
--refresh-dependencies  刷新依赖关系的状态。Refresh the state of dependencies.
--rerun-tasks           忽略以前缓存的任务。Ignore previously cached task results.
-S, --full-stacktrace   打印出所有异常堆栈信息（非常详细）。Print out the full (very verbose) stacktrace for all exceptions.
-s, --stacktrace        打印出所有异常堆栈信息。Print out the stacktrace for all exceptions.
--scan                  创建一个构建扫描。如果尚未应用构建扫描插件，Gradle 将失败的生成。[孵化中]Creates a build scan. Gradle will fail the build if the build scan plugin has not been applied. [incubating]
--status                显示运行的和最近停止的守护进程的状态信息。 Gradle Daemon(s)。Shows status of running and recently stopped Gradle Daemon(s).
--stop                  如果有守护进程在运行则停止守护进程。Stops the Gradle Daemon if it is running.
-t, --continuous        允许连续构建。Gradle在构建完成后不退出，并在任务文件变化时重新构建。Enables continuous build. Gradle does not exit and will re-execute tasks when task file inputs change. [incubating]
-u, --no-search-upward  不在父文件夹中搜索 settings.gradle。Don't search in parent folders for a settings.gradle file.
-v, --version           打印版本信息。Print version info.
-x, --exclude-task      指定要排除执行的任务。Specify a task to be excluded from execution.
```
