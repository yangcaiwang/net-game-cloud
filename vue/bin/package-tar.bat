@echo off
echo.
echo [��Ϣ] ��װWeb���̣�����node_modules�ļ���
echo.

%~d0
cd %~dp0

cd ../dist

tar -zcvf dist.tar.gz *.*

pause