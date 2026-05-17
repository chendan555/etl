@echo on
SET PROJ_NAME=dist
SET FILE_NAME=etl
SET BACKUP_DIRECTORY_NAME=backup
SET REAL_FILE_NAME=%SVN525_DEPLOY_HOME%\%PROJ_NAME%\%FILE_NAME%
SET BACKUP_DIRECTORY=%SVN525_DEPLOY_HOME%\%BACKUP_DIRECTORY_NAME%
SET TIMESTAMP=%date:~,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%%time:~9,3%
rem ================== SVN删除，避免文件缓存的问题 ==================
svn delete --force %REAL_FILE_NAME%
rem ================== 将dist复制到SVN映射本地盘目录 ==================
xcopy /y/e/h dist %REAL_FILE_NAME%\
rem ================== 切换到SVN映射本地盘目录 ==================
cd "%SVN525_DEPLOY_HOME%"
rem rd /s/q "%PROJ_NAME%\%FILE_NAME%\.svn"
rem for /r %REAL_FILE_NAME%\ %i in (*.svn) do rd /s /q 
forfiles /p %REAL_FILE_NAME%\ /m .svn /s /c  "cmd /c rd /s/q @path"
rem ================== 将dist备份到backup ==================
winrar a -r -m1 -ibck %BACKUP_DIRECTORY%\%FILE_NAME%-%TIMESTAMP%.zip %REAL_FILE_NAME%\


rem ================== SVN新增==================
svn add --username fengxianbo --password 123456 %PROJ_NAME%\%FILE_NAME%
rem ================== SVN新增备份文件==================
svn add --username fengxianbo --password 123456 %BACKUP_DIRECTORY%\%FILE_NAME%-%TIMESTAMP%.zip
rem ================== 执行SVN提交 ==================


svn ci --username fengxianbo --password 123456 %PROJ_NAME% -m "jenkins自动化部署"
svn ci --username fengxianbo --password 123456 %BACKUP_DIRECTORY% -m "jenkins自动部署包备份"
@exit