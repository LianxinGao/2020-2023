### 概念相关
Linux/Unix的文件调用权限分三级：owner、group、other group
### 修改相关
1. 修改权限用户
    1. chown(change owner) username
    2. chgrp(change group) username
2. 修改文件权限
    1. chmod(change mode) +-= rwx（r读w写x可执行）
    2. 
3. 解压文件
    1. tar -z(gzip|ungizp)x(extract)v(verbose显示执行过程)f(file指定文件) wenjian.tar.gz
4. 打包jar
    1. jar -cvfM cypher-shell.jar .
### 查看相关
1. ll -h  查看当前目录文件大小与权限

### 后台运行
1. nohup  
```
nohup path-to-your-script > your-log.log 2>&1 &  
>:重定向，2：错误输出，&1标准输出
```

### 挂载硬盘
1. mount [/dev/sdh]（你要挂载的硬盘） [/data5/]（你要挂到哪个目录下）
2. 查看有哪些硬盘：lsblk 