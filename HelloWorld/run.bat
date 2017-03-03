@echo off
:1
title Gradle学习-HelloWorld

::&& pause 只是防止闪退
echo= &&echo gradle -q helloworld 执行结果：&& echo =============================== && gradle -q helloworld && echo= && echo gradle -q helloworldSort 执行结果：&& echo ================================== &&  gradle -q helloworldSort && echo= && echo gradle执行结果：&& echo ================================== && gradle  && echo= && echo gradle -q startSession执行结果：&& echo ================================== && gradle  -q startSession && echo= && echo gradle -q yayGradle0执行结果：&& echo ================================== && gradle  -q yayGradle0 && echo= && echo gradle -q yayGradle1执行结果：&& echo ================================== && gradle  -q yayGradle1 && echo= && echo gradle -q yayGradle2执行结果：&& echo ================================== && gradle  -q yayGradle2 && echo= && echo gradle -q groupTherapy执行结果：&& echo ================================== && gradle  -q groupTherapy && pause