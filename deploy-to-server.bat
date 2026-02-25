@echo off

rem 传输脚本 - 将grape项目部署到远程服务器

echo 开始传输文件到 192.168.22.140...
echo.

rem 配置信息
set localJarPath=target\grape-0.0.1-SNAPSHOT.jar
set remoteServer=192.168.22.140
set remoteUser=grape
set remotePassword=huanle@2025!
set remotePath=/home/grape/grape-server

echo 本地JAR文件: %localJarPath%
echo 远程服务器: %remoteServer%
echo 远程路径: %remotePath%
echo.

rem 方法1: 使用WinSCP命令行工具 (推荐)
echo 方法1: 尝试使用WinSCP传输...
echo 请确保已安装WinSCP并将其添加到PATH环境变量
echo 下载地址: https://winscp.net/eng/download.php
echo 命令示例:
echo winscp.com /command "open scp://%remoteUser%:%remotePassword%@%remoteServer%/" "put ""%localJarPath%"" ""%remotePath/""" "exit"
echo.

rem 方法2: 使用PowerShell SSH模块
echo 方法2: 尝试使用PowerShell SSH传输...
echo 请确保已安装OpenSSH客户端
echo 命令示例:
echo ssh %remoteUser%@%remoteServer% -p 22 "mkdir -p %remotePath%"
echo scp "%localJarPath%" "%remoteUser%@%remoteServer%:%remotePath%/"
echo.

rem 方法3: 手动传输提示
echo 方法3: 手动传输
echo 如果上述方法都失败，请手动传输文件:
echo 1. 找到文件: %localJarPath%
echo 2. 使用FTP客户端(如FileZilla)连接到服务器:
echo    - 主机: %remoteServer%
echo    - 用户名: %remoteUser%
echo    - 密码: %remotePassword%
echo    - 端口: 22
echo 3. 将文件上传到: %remotePath%
echo.

echo 传输脚本执行完成!
pause