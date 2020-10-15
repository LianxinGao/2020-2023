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
    
### 查看相关
1. ll -h  查看当前目录文件大小与权限