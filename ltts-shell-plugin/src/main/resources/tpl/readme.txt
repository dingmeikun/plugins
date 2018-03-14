1.配置一个应用需要在etc目录下建立与vmid一致的目录
2.vmid目录下存有关配置文件ltts.conf,ltts_log_server.xml,setting.properties
3.ltts.conf可配置相关jvm参数,启动插件,启动参数等(ltts.conf为可选,默认有约定的配置参数)


# 标准目录结构
Application(应用顶层目录)
	|
	|-etc(配置文件目录)
	|  |-vm1
	|  |-vm2
	|  
	|-bin
	|
	|-lib(jar包目录)
	|
	|-log
	   |-vm1
	   |-vm2


# 命令清单
ltts help <subcmd>  命令帮助
ltts start vmid	    指定vmid启动应用,有控制台日志回显,启动完毕自动返回状态码(0:成功 1:失败)
ltts console vmid   指定vmid控制台启动应用,Ctrl+C退出将停止应用
ltts consoled vmid  指定vmid控制台启动应用,Ctrl+C退出不停止应用
ltts stop vmid      指定vmid关闭应用
ltts status <vmid>    指定vmid或查看所有应用运行状态
ltts restart vmid   指定vmid重启应用
ltts dump vmid      指定vmid dump线程信息
