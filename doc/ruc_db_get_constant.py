##!coding:utf-8

import pymysql

conn = pymysql.connect(host='114.115.137.143', port=3306, user='hw_sxg', passwd='123456',db='ruc_test',charset='utf8')
cur = conn.cursor()
cur.execute("SELECT max(column_143), max(column_159), max(column_160), max(column_161), max(column_162), max(column_163), max(column_164), max( CASE column_285 WHEN 'FALSE' THEN 0 END ), max(column_297), max(column_298), max( CASE column_300 WHEN 'FALSE' THEN 0 END ), max(column_333), max(column_352), max(column_354), max(column_355), max(column_357), max(column_360), max(column_361), max(column_362), max(column_363), max(column_364), max(column_365), max(column_366), max(column_367), max(column_368), max(column_369), max(column_370), max(column_371), max(column_402), max(column_403), max(column_404), max(column_408), max(column_412), max(column_424), max(column_427), max(column_428), max(column_431), max(column_433), max(column_434), max(column_435), max(column_436), max(column_445), max(column_446), max(column_448), max(column_462), max(column_466), max(column_467), max(column_468), max(column_469), max(column_470), max(column_476), max(column_502), max(column_503), max(column_504), max(column_505), max(column_511), max(column_531), max(column_546) FROM ruc_test_3")
data=cur.fetchall()
cur.close()
conn.close()
count=1;
start='<sensor id="constant'
middle1='" function-ref-id="'
middleType='-mono" max="'
midele2='" min="'
end='" cycle="3600"></sensor>'
value=0
for i in data[0]:
    #'<sensor id="random1" function-ref-id="double-square" max="5" min="2" cycle="18200"></sensor>
    value=i
    if str(value).find('.')==-1:
        xml = start + str(int(count)) + middle1 + 'int' + middleType + str(value) + midele2 + str(value) + end
    else:
        xml = start + str(int(count)) + middle1+'double'+middleType + str(round(float(value), 4)) + midele2 + str(
            round(float(value), 4)) + end
    print(xml)

    count=count+1;



