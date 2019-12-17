XWorker
============
## 项目简介
    XWorker是动态模型的开发平台，使用XWorker可以编辑、运行和发布模型程序。

    XWorker的官网是[XWorker官网](http://https://www.xworker.org)，可以在官网上下载和安装XWorker，也可以查看文档、示例和教程等。

## 动态模型简介
    动态模型是一种编程方法，它使用模型来编程，模型可以用XML来表示。

    比如下面的XML代码是一个模型程序。

```
<?xml version="1.0" encoding="utf-8"?>

<Shell name="HelloWorld" descriptors="xworker.swt.widgets.Shell" text="Hello World" RESIZE="true"
     width="800" height="600">
    <FillLayout name="FillLayout"></FillLayout>
    <Browser name="Browser" url="https://www.xworker.org" WEBKIT="true"></Browser>
</Shell>
```
    上面的模型是一个SWT应用，运行后截图如下。
![SWT](https://images.gitee.com/uploads/images/2019/1217/164635_22cd7199_493262.png "SWT应用")

## 编译和运行XWorker
### 编译
    XWorker包含几个Eclipse项目，都需要导入到Eclipse中，由Eclipse自动编译。

    最好也把动态模型的项目导入进来，以免编译不通过。

    gitee: https://gitee.com/xworker/x-meta.git, github: https://github.com/x-meta/x-meta.git.

### 运行
    编译通过后，运行xworker_explorer项目下的xworker.ide.XWorkerExplorer。XWorker运行后的截图如下。

![事物管理器](http://git.oschina.net/uploads/images/2016/0623/192549_72e935b0_493262.png "模型管理器")

### 打包
    可以运行XWorker目录下的mvn_install_nopause.cmd来打包XWorker，打包后的XWorker在xworker_explorer/target/xworker目录下。

### 多语言
    XWorker支持中文和英文，不过大部分文档都是中文的，没有对应的英文。 