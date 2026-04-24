# 批量修改包声明的PowerShell脚本

# 定义要搜索的目录
$targetDir = "e:\huanle-project\grape-server\src"

# 定义要替换的包名
$oldPackage = "package com.grape4j"
$newPackage = "package com.grape.grape"

# 查找所有Java文件
$javaFiles = Get-ChildItem -Path $targetDir -Recurse -Filter "*.java"

# 统计修改的文件数
$modifiedCount = 0

# 遍历所有Java文件
foreach ($file in $javaFiles) {
    # 读取文件内容
    $content = Get-Content -Path $file.FullName -Encoding UTF8 -Raw
    
    # 检查是否包含旧包声明
    if ($content -match $oldPackage) {
        # 替换包声明
        $newContent = $content -replace $oldPackage, $newPackage
        
        # 写回文件
        Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8
        
        # 增加计数器
        $modifiedCount++
        
        # 输出修改信息
        Write-Host "Modified: $($file.FullName)"
    }
}

# 输出修改结果
Write-Host "\nModified $modifiedCount files."
