# XWorker
Xworker is the development platform of dynamic model, and xworker also provides some model libraries.

## Dynamic model
### 1. Object oriented programming method based on structured data
Dynamic model is a model-based object-oriented programming method, in which the model can be represented by XML and so on.

For example, the following XML is a model program, and each node of the XML is an object. You can click here to run the model. After running, a window will open with a browser displaying the xworker home page.
```
<?xml version="1.0" encoding="utf-8"?>
 
<Shell name="shell" text="Hello World" descriptors="xworker.swt.widgets.Shell"
               width="800" height="600">
    <FillLayout name="FillLayout"></FillLayout>
    <Browser name="Browser" url="https://www.xworker.org"></Browser>
</Shell>
```
The above model is a SWT application. The screenshot after running is as follows.
![SWT](https://images.gitee.com/uploads/images/2019/1217/164635_22cd7199_493262.png  "SWT application")

### 2. Characteristics of dynamic model
- **dynamic programming**
Dynamic programming can be realized by using dynamic model, that is, real-time programming when the system is running, so as to dynamically modify the function of the system.
- **quick programming**
Fast programming can be achieved using dynamic models. This is because the model generally encapsulates the use interfaces of various APIs. These APIs can be used quickly through the model, and different APIs can be easily integrated by using the dynamic model, so as to develop complex applications more easily.

### 3. How to use dynamic model
#### 3.1. Use occasion
As a programming method, dynamic models can be used in Java projects, or they can be programmed directly.

#### 3.2. Model library
The dynamic model engine x-meta used by xworker is developed in Java, and the model library is also published in the form of jar. Generally, the dynamic model can be used by introducing relevant class libraries into Java projects.
Xworker's model library is published on Maven and can be found in [Maven](https://mvnrepository.com/search?q=xworker) View published class libraries.

#### 3.3. How to edit and run a model
Although the model can be expressed in XML, it may be difficult to edit the model manually, so the model editor is generally used to edit the model.
Xworker provides a model editor where you can edit models. Because the dynamic model is usually real-time dynamic programming, the model editor is generally a part of your program. You can edit the model while running the program.

#### 3.4. How to publish model programs
With the packaging and publishing of Java programs, the model can be packaged into jar, and the application of dynamic model is also a Java application.

#### 3.5. Documentation and examples
See [xworker home page](https://www.xworker.org)  or [wiki](https://gitee.com/xworker/xworker/wikis/pages) for documentation. See [apps](https://gitee.com/xworker/apps) for application examples。 Examples of specific models and documents can generally be found in the model editor.

## Join us
Xworker is a platform based on Apache 2.0 open source protocol. Xworker looks forward to your participation. Please join us to improve it. QQ group: ** 312989786 **.