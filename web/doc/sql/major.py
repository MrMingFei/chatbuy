# -*-coding: utf-8 -*-
import re

firstWord = 'A'
result = dict()
rf = open('专业科目（首字母）.txt')
wf = open('new_major_data.sql', 'w')

tpl = r'INSERT INTO dic_major(majorName,validity,fLetter,updateTime,createTime) VALUES("{major}","1","{firstLetter}",null,now());'

for line in rf.readlines():
    if re.match('^[a-zA-Z]', line):
        firstWord = line[0:1]
    else:
        sql = re.sub('{major}', line.strip(), tpl)
        sql = re.sub('{firstLetter}', firstWord, sql)
        print(sql)
        wf.write(sql + '\n')
rf.close()
wf.close()
