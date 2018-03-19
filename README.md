#cleate mine plugins repository

##usage:

##1.命令行上传|创建repository|新建代码库 
echo "# plugins" >> README.md  
git init  
git add README.md  
git commit -m "first commit"  
git remote add origin https://github.com/dingmeikun/plugins.git  
git push -u origin master  

##2.命令行将一个已存在的repository上上传上去  
git remote add origin https://github.com/dingmeikun/plugins.git  
git push -u origin master  

##3.从其他repository上传code  
You can initialize this repository with code from a Subversion, Mercurial, or TFS project.

##4.本地上传代码或者文件  
git add .  
git commit -m "提交备注"  
git push -u origin master  

##5.首先克隆远程项目  
到本地文件夹  
登录：git config  
执行：git clone https://github.com/dingmeikun/plugins.git  
使用客户端 bit bash here  

##6.删除项目
到本地文件夹  
git rm [file1] [file2]  
git commit -m [message]  
git push -u origin master  

##7.更改项目名字
git mv [file-original] [file-renamed]  
git commit -m [message]  
git push -u origin master 

##8.以上操作之前，可以先更新代码
git pull  

##9.username password
dingmeikun a360 
