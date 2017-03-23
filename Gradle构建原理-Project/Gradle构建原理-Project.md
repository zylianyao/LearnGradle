
基本构建块
------
每个 Gradle 构建都包含三个基本概念：project、task 和 property。
每个构建包含至少一个 project、一个或多个 task。project 和 task 暴露的属性可以用来控制构建。
Gradle 的核心组件直接的依赖关系如下：![Gradle 的核心组件直接的依赖关系](https://static.oschina.net/uploads/img/201703/17170826_VwaE.png "Gradle 的核心组件直接的依赖关系")
在多项目构建中，一个 project 可以依赖于其他的 project 。在同一个 project  中一个 task 可以依赖一个或多个 task。

###Project
`org.gradle.api.Project` 是主要的与 Gradle 构建文件交换的接口，提供了 Gralde 所有特征的编程访问方式（例如tTask的创建以及依赖的管理）。在调用对应API时无需使用 project 变量，因为 Gradle 会默认你使用的是 Project 的实例，

```groovy
setDescription("project 描述")//在不显式使用 project 变量的情况下设置项目描述
println "项目 $name 的描述："+ description //在不使用 project 变量的情况下通过 Groovy 语法来访问 name 以及  description 属性 【这两个属性都是Project对象的属性】
```

输出如下：

```shell
coderknock@Sanchan:/mnt/d/Windows/Desktop/LearnGradle/Gradle构建原理$ gradle build
Starting a Gradle Daemon (subsequent builds will be faster)
> Configuring > 0/1 projects > root project > Compiling /mnt/d/Windows/Desktop/LearnGradle/Gradle构建原理/build.gradle i项目 Gradle构建原理 的描述：project 描述
:buildEnvironment

------------------------------------------------------------
Root project - project 描述
------------------------------------------------------------

classpath
No dependencies

BUILD SUCCESSFUL

Total time: 15.786 secs
```

可以看到，我们确实可以访问到相关的内容。

一个 `Project` 可以创建新的 `Task`，添加依赖关系和配置，并应用插件和其他的构建脚本。

####生命周期

“build.gradle” 文件与`Project` 实例是一一对应的。在构建初始化时，Gradle 为每个参与到构建过程的项目都创建了一个 `Project` 对象，操作如下：

* 为构建过程创建一个`org.gradle.api.initialization.Settings`实例 
* 检查`settings.gradle`脚本，如果存在，这对上面创建的`Settings`实例进行相应的配置
* 使用`Settings`实例作为配置信息创建`Project`层次的实例
* 最后，循环检擦每个相关的项目，如果存在"build.gradle"文件，则根据该文件对项目对应的`Project`对象进行配置。项目的检查是横向进行的，这样一个项目总是会在其子项目之前进行检测、配置。这个顺序可以通过调用`evaluationDependsOnChildren() `进行修改、或者通过`evaluationDependsOn(String)`方法添加一个明确的检查依赖关系

####Tasks（任务）

在默认情况下，每个新创建的 Task 都是`org.gradle.api.DefaultTask`类型。`DefaultTask`里所有的属性都是`private`的，但是提供了`getter`、`setter`。Groovy提供的语法糖可以直接使用属性名使用属性。

一个项目基本上是一个`Task`对象的集合。每个`Task`的执行一块儿基本的工作，如编译类文件，或运行单元测试，或压缩war文件。我们可以通过实现`org.gradle.api.task.TaskContainer`接口的类的名为`create`的方法（该方法是一个多重载的方法）来添加`Task`，例如`TaskContainer.create(String)`，还可以使用`TaskContainer`中的一些方法来查找已经存在的`Task`，例如`TaskCollection.getByName(String)`。
在[第一个 Gradle 脚本及简单命令](https://coderknock.com/blog/2017/03/03/第一个%20Gradle%20脚本及简单命令.html	"第一个 Gradle 脚本及简单命令") 的学习中我们对 Task 就已经有过接触，并且使用过其中一些较为重要的功能：任务动作（task action）以及任务依赖（task dependency）。
#####Task action（任务动作）
任务动作定义了一个任务执行时的最小工作单元，可以是简单的输出，也可以是诸如编译等较为复杂的工作。例如[第一个 Gradle 脚本及简单命令](https://coderknock.com/blog/2017/03/03/第一个%20Gradle%20脚本及简单命令.html	"第一个 Gradle 脚本及简单命令") 中的：
```Groovy
task helloworldSort {
	//doLast 就是 Task 中的一个任务动作
	doLast{
    	print 'Hello world!'
    }
}
```
#####Task dependency（任务依赖）
但一个任务运行时需要先运行另一个任务，这两个任务间就需要有任务依赖。例如[第一个 Gradle 脚本及简单命令](https://coderknock.com/blog/2017/03/03/第一个%20Gradle%20脚本及简单命令.html	"第一个 Gradle 脚本及简单命令") 中的：
```Groovy
// 任务依赖
yayGradle0.dependsOn startSession
/* 任务执行的顺序 startSession -> yayGradle0 -> yayGradle1 -> yayGradle2 -> groupTherapy */
// 任务依赖
task groupTherapy(dependsOn: yayGradle2) << {
    println 'groupTherapy'
}
```
以上就是任务依赖的两种使用方法。
下面是 `Task` 的API：
![任务Task](https://static.oschina.net/uploads/img/201703/20173240_PoNL.png "任务Task")

####Dependencies（依赖项）
一个项目为了完成构建工作，通常会有数个依赖。此外，项目通常会产生一系列的其他项目可以使用的工件。这些依赖项按配置分组，可以从资料库检出或上传自己的依赖项到资料库。`getConfigurations()`方法返回的`ConfigurationContainer`用于管理配置相关信息。 `getDependencies()`方法返回的`DependencyHandler`用来管理依赖项相关信息。 `ArtifactHandler.getArtifacts()`方法返回管理工件相关信息。 `getRepositories()`方法返回的`RepositoryHandler`用来管理存储库相关信息。

####多项目构建（Multi-project Builds）
多项目会被排成的一个层次结构。一个项目有一个名称以及能够唯一标识该层次结构中的完全限定的路径。

####插件（Plugins）
插件可以用于***模块化*** 以及重用项目配置。可以使用`PluginAware.apply(java.util.Map)`方法，应用插件或通过使用插件脚本块。
```Groovy
 plugins {
     id "org.company.myplugin" version "1.3"
 }
```
以上就是一个简单的插件脚本块。
####属性（Properties）
每个`Project`和`Task`实例都提供了可以通过`getter`和`setter`方法访问的属性。
Gradle 执行项目的构建文件来配置对应的`Project`实例。任何属性或您的脚本使用的方法是通过授予关联的`Project`对象来实现的。这意味着，你可以在您的脚本直接使用`Project`接口上的任何方法和属性。
例如︰
```Groovy
 defaultTasks('some-task')  // Project.defaultTasks()
 reportsDir = file('reports') // Project.file() and the Java Plugin
```
您也可以访问使用该属性的实例来访问其属性，在某些情况下，这可以使脚本更清晰。例如，您可以使用`project.name`来访问该项目的名称。
一个项目有 6个属性 “范围”用于搜索属性。您可以通过构建文件中的名称或通过调用项目的[`property(String)`](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#property(java.lang.String))方法访问这些属性。5个属性“范围”是：
1. `Project`对象本身。此范围包括`Project`实现类声明的属性的getter和setter。例如，[`getRootProject()`](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/Project.html#getRootProject())可作为`rootProject`的属性访问方式。此范围的属性是可读或可写的，存在对相应 getter 和 setter 方法。

2. 项目的额外属性。每个项目都维护一个额外属性的映射，可以包含任意 `名称 - >值` 对。一旦定义，该范围的属性是可读和可写的。有关详细信息，请参阅[其他属性](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/Project.html#extraproperties)。

3. 通过添加插件将*扩展*添加到项目中。每个扩展都是只读属性，与扩展具有相同的名称。

4. 通过插件将*约定*属性添加到项目中。插件可以通过项目的[`Convention`](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/plugins/Convention.html)对象向项目添加属性和方法。此范围的属性可以是可读或可写的，这取决于约定对象。

5. 项目的任务。可以通过使用其名称作为属性名称来访问任务。此范围的属性是只读的。例如，调用的任务`compile`可作为`compile` 属性访问。

6. 继承自父级项目的扩展属性和惯例属性，递归到根项目。此作用域的属性为只读。

   当读取属性时，项目按顺序搜索上述范围，并从其找到属性的第一个范围返回值。如果未找到，将抛出异常。查看[`property(String)`](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/Project.html#property(java.lang.String))更多详细信息。

   编写属性时，项目按顺序搜索上述范围，并将其属性设置在第一个作用域中，该属性位于其中。如果未找到，将抛出异常。查看[`setProperty(String, Object)`](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/Project.html#setProperty(java.lang.String, java.lang.Object))更多详细信息。

#### 扩展属性

   Gradle对的很多领域模型类提供了特别的属性支持，在内部，这些属性以键值对的形式存储。所有扩展的属性必须通过“ext”命名空间进行定义。一旦扩展的属性被定义，它可以直接在所有的对象（在下面的情况下分别是`Project`，`Task`和`subprojects`）可用，并且可以被读取和更新。只需要在最初宣布通过命名空间来完成。
```Groovy
project.ext.prop1 = "foo"
ext{
  prop2="test"
}
task doStuff {
     ext.prop3 = "bar"
 }
//下面的方法官方文档中这样写，但是实验时会报错  "ext.$({ -> ... })" is a method call expression, but it should be a variable expression at line: 10 column: 28. File: _BuildScript_ @ line 10, column 28.
//subprojects { ext.${prop4} = false }
ext.prop5=23

println prop1 //会正常输出
println prop2 //会正常输出

//println prop3 //这样调用会报错 Could not get unknown property 'prop3' for root project 'Gradle构建原理' of type org.gradle.api.Project.这也证实了默认使用的是 project 实例
println doStuff.prop3 //会正常输出
println prop5 //会正常输出    
```
通过“ext”或通过拥有的对象来读取扩展的属性。
```Groovy
ext.isSnapshot = version.endsWith("-SNAPSHOT")
if (isSnapshot) {
     // do snapshot stuff
}
```
扩展属性也可以通过属性文件来提供。
####属性文件
Gradle 属性可以通过在 gradle.properties文件中什么直接添加到项目中，这个文件位于<USER_HOME>/.gradle（这个路径可以配置【配置后以配置后的目录为准】，后续教程会提及） 目录或项目的根目录下。这些属性可以通过 project 实例访问。

gradle.properties：

```properties
projectTestPropValue=D:\Windows\Desktop\LearnGradle\projectTestPropValue
userTestpPopValue=E:\gradle_repo\.gradle\projectTestPropValue
```

使用示例：

```groovy
println projectTestPropValue
println userTestpPopValue
```



如果在项目根目录及 <USER_HOME>/.gradle 下  gradle.properties 中有相同的属性的话以 <USER_HOME>/.gradle 中为准。

#### 声明属性的其他方式

对于扩展属性以及属性文件两种方式外，还有声明自定义变量及值的方式以及下面这些方式声明属性：

* 项目属性通过 -P 命令行选项提供：
  运行命令时使用：gradle build -P rootTestPropValue=123
* 系统属性通过 -D 命令行选项提供
  运行命令时使用：gradle build -D rootTestPropValue=123
* 环境属性通过系统环境变量设置：
  ORG_GRADLE_PROJECT_propertyName=值

####动态方法
一个项目有5种方法“范围”，它搜索方法：
1. `Project`对象本身。
2. 构建文件。该项目搜索在构建文件中声明的匹配方法。
3. 插件添加到项目的扩展。每个扩展可用作接受闭包或Action作为参数的方法。
4. 通过插件将约定方法添加到项目中。插件可以通过项目的[`Convention`](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/plugins/Convention.html)对象向项目添加属性和方法。
5. 项目的任务。为每个任务添加一个方法，使用任务的名称作为方法名称并获取单个闭包或[`Action`](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/Action.html)参数。该方法[`Task.configure(groovy.lang.Closure)`](https://docs.gradle.org/3.4.1/javadoc/org/gradle/api/Task.html#configure(groovy.lang.Closure))
6. 使用提供的闭包调用关联任务的方法。例如，如果项目有一个被调用的任务compile，那么将添加一个方法，并带有以下签名：void compile(Closure configureClosure)。
7. 父项目的方法，递归到根项目。
8. 项目的属性，其值为闭包。封闭被视为一种方法，并使用提供的参数进行调用。该物业的位置如上所述。

下面是 `Project` 的API：
![Project API](https://static.oschina.net/uploads/img/201703/20175742_cyGT.png "Project API")

下面是 `PluginAware` 的API：

![PluginAware API](https://static.oschina.net/uploads/img/201703/20175803_tY9c.png "PluginAware API")