# XWorker

XWorker是动态模型的开发平台，XWorker也提供了一些模型库。

## 动态模型

### 1.基于结构化数据的面向对象编程方法

动态模型是一种基于模型的面向对象的编程方法，其中模型可以用XML等来表示。

比如下面的XML就是一个模型程序，这个XML的每一个节点都是一个对象。可以点击这里运行模型， 运行后会打开一个窗口，窗口中有一个显示XWorker主页的浏览器。

```
<?xml version="1.0" encoding="utf-8"?>
 
<Shell name="shell" text="Hello World" descriptors="xworker.swt.widgets.Shell"
               width="800" height="600">
    <FillLayout name="FillLayout"></FillLayout>
    <Browser name="Browser" url="https://www.xworker.org"></Browser>
</Shell>
```
上面的模型是一个SWT应用，运行后截图如下。

![SWT](https://images.gitee.com/uploads/images/2019/1217/164635_22cd7199_493262.png "SWT应用")

### 2.动态模型的特点
-  **动态编程** 
使用动态模型可以实现动态编程，即在系统运行时实时编程，从而动态修改系统的功能。
 
- **快速编程**
使用动态模型可以实现快速编程。这是因为模型一般是对各种API的使用接口的封装，通过模型可以快速的使用这些API，并且使用动态模型也可以很容易的把不同的API整合在一起，从而更容易地开发出复杂的应用。

### 3.如何使用动态模型

#### 3.1.模型库
XWorker所使用的动态模型引擎X-Meta是使用Java开发，模型库也是通过Jar的形式发布的，一般在Java项目中引入相关的类库就可以使用动态模型了。

XWorker的模型库是发布到Maven上的，可以在[Maven里](https://mvnrepository.com/search?q=xworker)查看已发布的类库。

#### 3.2.如何编辑和运行模型
模型虽然可以用XML来表示，但手工编辑模型难度可能会比较大，所以一般使用模型编辑器来编辑模型。

XWorker提供了模型编辑器，可以在模型编辑器里编辑模型。由于动态模型一般是实时动态编程，所以模型编辑器一般也是你的程序的一部分，可以一边运行程序一般编辑模型。

#### 3.3.如何发布模型程序
同Java程序的打包和发布，模型可以打包到Jar中，动态模型的应用也是Java应用。

更多内容可以参看[XWorker主页](https://www.xworker.org)或[Wiki](./wikis/pages)

## 加入我们
XWorker是一个基于Apache2.0开源协议的平台，XWorker期待你的参与，请加入我们一起来完善它，QQ群：**312989786**。

