@echo off
:1
title Gradle学习-HelloWorld

::&& pause 只是防止闪退
echo= &&echo gradle -q helloworld 执行结果：&& echo =============================== && gradle -q helloworld && echo= && echo gradle -q helloworldSort 执行结果：&& echo ================================== &&  gradle -q helloworldSort && echo= && echo gradle执行结果：&& echo ================================== && gradle && pause