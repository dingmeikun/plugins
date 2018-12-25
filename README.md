#cleate mine plugins repository

##usage: 注意，新创建项目，会有提示git config --global user.name "xxx" 需要提前执行这个命令

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
git config --global user.name "丁美坤"
git config --global user.email "dingmk@sunline.cn"
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


=================================增加res key加密 https://blog.csdn.net/jingtingfengguo/article/details/51892864 ===========
1、首先我得重新在git设置一下身份的名字和邮箱（因为当初都忘了设置啥了，因为遇到坑了）进入到需要提交的文件夹底下（因为直接打开git Bash，在没有路径的情况下，根本没！法！改！刚使用git时遇到的坑。。。）

git config --global user.name "yourname"
git config --global user.email“your@email.com"

注：yourname是你要设置的名字，your@email是你要设置的邮箱。
2、删除.ssh文件夹（直接搜索该文件夹）下的known_hosts(手动删除即可，不需要git）位置是：c:/Users/user/.ssh/ 下面

3、git输入命令
$ ssh-keygen -t rsa -C "your@email.com"（请填你设置的邮箱地址）

接着出现：
Generating public/private rsa key pair.
Enter file in which to save the key (/Users/your_user_directory/.ssh/id_rsa):

请直接按下回车

然后系统会自动在.ssh文件夹下生成两个文件，id_rsa和id_rsa.pub，用记事本打开id_rsa.pub，并且将全部的内容复制

4、打开https://github.com/，登陆你的账户，进入设置
  1 选择“SSH and GPG keys” ，再选择new SSH key
  2 填写title 在把保存的key粘贴上去
  3 点击保存：add ssh key
 
5、在命令行输入：ssh -T git@github.com 好像没啥用，就是验证下有没有成功。
6、这时候就可以：git push origin master 了！！
