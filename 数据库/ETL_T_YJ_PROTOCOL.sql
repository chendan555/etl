SET IDENTITY_INSERT "ETL"."T_YJ_PROTOCOL" ON;
INSERT INTO "ETL"."T_YJ_PROTOCOL"("ID","NAME","TABLE_NAME","FILE_TYPE","FILE_TYPE_NAME","SEPARATOR","SEPARATOR_NAME","TABLE_ID_TYPE","ID_COLUMN_NAME","JSON_FORM","TABLE_ID","REMARK","CREATE_BY","CREATE_DATE","UPDATE_BY","UPDATE_TIME") VALUES(1,'文件解析','TEST','txt','TXT',' ','空格','1','ID','[{"columnName":"name","columnComment":"名字","columnIndex":1,"matchList":[]},{"columnName":"age","columnComment":"年龄","columnIndex":2,"matchList":[]}]',null,'',null,null,null,'2026-05-21 23:45:56.663329');

SET IDENTITY_INSERT "ETL"."T_YJ_PROTOCOL" OFF;
