本项目主要是shardingsphere-分库分表中间件学习相关，包含整合实践部分、原理学习部分。

参考官网文档：https://shardingsphere.apache.org/document/5.1.1/cn/reference/api-change-history/shardingsphere-proxy/



参考：
 - linux服务器安装sharding-proxy参考：https://blog.csdn.net/K_520_W/article/details/123967856
 - linux服务器安装mysql8.0参考：https://www.51cto.com/article/613756.html ps：安装前最好先更新下yum的源，否则可能报错，yum方式安装报错The GPG keys listed for the "MySQL 8.0 Community Server" repository are already installed but they are not correct for this package. Check that the correct key URLs are configured for this repository.，https://www.mariuszantonik.com/2022/03/the-gpg-keys-listed-for-the-mysql-8-0-community-server-repository-are-already-installed-but-they-are-not-correct/
 - linux服务器yum安装mysql8.0配置方法参考：https://blog.csdn.net/weixin_43849415/article/details/122843611 ,配置Navicate远程连接（别忘了先开安全组然后重启）https://zhuanlan.zhihu.com/p/587097435
 - 启动报错penJDK 64-Bit Server VM warning: Option UseConcMarkSweepGC was deprecated in version 9.0 and will likely be removed in a future release. Unrecognized VM option 'UseFastAccessorMethods' Error: Could not create the Java Virtual Machine.sharding-proxy依赖java,但是java升级版本移除了某些jvm参数，解决的话就是删除start.sh中过时的参数即可。



![sharding-proxy-demo](D:\myproject\my_idea_projects\xkw\learn-projects-demo\sharding-proxy-demo\assets\sharding-proxy-demo.png)
