XWorker
============
## Introduction
    Xworker is the development platform of dynamic model. With xworker, you can edit, run and publish model programs.

    Xworker's official website is [xworker's official website] (http://https://www.xworker.org). You can download and install xworker on the official website, or view documents, samples, tutorials, etc.

## Dynamic Model 
    Dynamic model is a programming method. It uses model to program, and the model can be expressed in XML.

    For example, the following XML code is a model program.

```
<?xml version="1.0" encoding="utf-8"?>

<Shell name="HelloWorld" descriptors="xworker.swt.widgets.Shell" text="Hello World" RESIZE="true"
     width="800" height="600">
    <FillLayout name="FillLayout"></FillLayout>
    <Browser name="Browser" url="https://www.xworker.org" WEBKIT="true"></Browser>
</Shell>
```
    The above model is a SWT application, and the screenshot after running is as follows.
![SWT](https://images.gitee.com/uploads/images/2019/1217/164635_22cd7199_493262.png "SWT")

## Compiling and running xworker
### Compile
    Xworker contains several eclipse projects that need to be imported into eclipse and compiled automatically by eclipse.

    It's better to import the dynamic model project, so as not to fail the compilation.

    gitee: https://gitee.com/xworker/x-meta.git, github: https://github.com/x-meta/x-meta.git.

### Run 
    After the compilation, run xworker.ide.XWorkerExplorer under xworker_explorer project. The screenshot of xworker after running is as follows.

![ThingManager](http://git.oschina.net/uploads/images/2016/0623/192549_72e935b0_493262.png "ThingManager")

### Package
    You can run mvn_install_nopause.cmd in xworker directory to package xworker. The packed xworker is in xworker_explorer/target/xworker directory.

### Multilingual
    Xworker supports Chinese and English, but most of the model documents are in Chinese, and there is no corresponding English.