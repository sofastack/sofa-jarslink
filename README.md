
![https://techforum-img.cn-hangzhou.oss-pub.aliyun-inc.com/1522738732879/1200-628-第3幅.jpg](https://techforum-img.cn-hangzhou.oss-pub.aliyun-inc.com/1522738732879/1200-628-第3幅.jpg)

# JarsLink Feature


- Class isolation

Class isolation can be achieved by using a separate ClassLoader for the loading of each module's class, where each module can rely on different versions of a same framework.

- Instance isolation

Instance isolation can be achieved by having the framework create a separate Spring context for each module to load the bean in the module. Failure to instantiate then does not affect other modules.

- Resource isolation

Using separate CPU and memory resources for each module means that in the future, resource isolation between modules can be supported.

- Dynamic publishing

This means modules can be dynamically loaded into the system at runtime, without rebooting and the publishing of system newly added functions. The framework must support breaking through the parental delegation mechanism. Classes that have been loaded by the parent class loader are loaded at runtime, enabling the module upgrade dependence package to not require the system to publish.

- Dynamic unloading

This means that modules can be dynamically unloaded cleanly at run time, enabling unneeded functions to go offline quickly.

- Accessible, versatile, and flexible API

This would be provided for interaction between the system and modules.

Besides identifying potential features and functions for development frameworks, a different development approach was also identified by Alibaba.

# A modular development approach

A modular development method can bring a number of advantages over a traditional application development method. The two methods are compared in the figure below.
 
Comparison of the application and modular development methods for development frameworks.

In the modular development method, an application consists of multiple modules, which can be detached and assembled. These modules can be quickly migrated and deployed in multiple systems, and adding or modifying functions is done on a module-by-module basis. Modular development enables isolation between modules, thereby also achieving fault isolation. This is not possible in the application development method.

The potential conflicting codes associated with the application development method cannot occur with the modular approach as a module corresponds to a branch. Furthermore, modules are deployed to applications at run time, so failures can be quickly fixed and publishing efficiency is improved.

With multi-version deployment, developers can deploy both old and new versions of a module at run time to perform AB TEST. Also, resource consumption is reduced due to the reduction in the number of applications and machines through module deployment.

Based on a deep understanding of what was missing in available development frameworks and realizing the importance of the flexibility and efficiency of the modular development method, the Alibaba tech team propose the JarsLink modular development framework.

# DOCUMENT

- [中文版](https://github.com/alibaba/jarslink/wiki/index-cn)
- [English](https://github.com/alibaba/jarslink/wiki/index)

# DOWNLOAD

- [1.5.0.20171107](http://ifeve.com/wp-content/uploads/2018/02/jarslink.zip)
- [For more version](https://oss.sonatype.org/#nexus-search;quick~com.alipay.jarslink)


# License

JarsLink is released under the [Apache 2.0 license](https://github.com/alibaba/jarslink/blob/master/LICENSE).

```
Copyright 1999-2017 Alibaba Group Holding Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at following link.

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
