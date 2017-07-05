##!coding:utf-8

import pymysql

conn = pymysql.connect(host='114.115.137.143', port=3306, user='hw_sxg', passwd='123456',db='ruc_test',charset='utf8')
cur = conn.cursor()
cur.execute("SELECT max(column_290), min(column_290), max(column_377), min(column_377), max(column_418), min(column_418), max(column_419), min(column_419), max(column_432), min(column_432), max(column_437), min(column_437), max(column_438), min(column_438), max(column_439), min(column_439), max(column_440), min(column_440), max(column_441), min(column_441), max(column_442), min(column_442), max(column_443), min(column_443), max(column_444), min(column_444), max(column_447), min(column_447), max(column_449), min(column_449), max(column_450), min(column_450), max(column_451), min(column_451), max(column_452), min(column_452), max(column_453), min(column_453), max(column_454), min(column_454), max(column_455), min(column_455), max(column_456), min(column_456), max(column_457), min(column_457), max(column_458), min(column_458), max(column_459), min(column_459), max(column_460), min(column_460), max(column_461), min(column_461), max(column_463), min(column_463), max(column_464), min(column_464), max(column_465), min(column_465), max(column_472), min(column_472), max(column_473), min(column_473), max(column_475), min(column_475), max(column_477), min(column_477), max(column_478), min(column_478), max(column_479), min(column_479), max(column_480), min(column_480), max(column_481), min(column_481), max(column_482), min(column_482), max(column_483), min(column_483), max(column_484), min(column_484), max(column_485), min(column_485), max(column_486), min(column_486), max(column_487), min(column_487), max(column_488), min(column_488), max(column_489), min(column_489), max(column_490), min(column_490), max(column_491), min(column_491), max(column_492), min(column_492), max(column_493), min(column_493), max(column_494), min(column_494), max(column_495), min(column_495), max(column_496), min(column_496), max(column_500), min(column_500), max(column_501), min(column_501), max(column_506), min(column_506), max(column_512), min(column_512), max(column_513), min(column_513), max(column_514), min(column_514), max(column_515), min(column_515), max(column_516), min(column_516), max(column_517), min(column_517), max(column_518), min(column_518), max(column_519), min(column_519), max(column_520), min(column_520), max(column_521), min(column_521), max(column_522), min(column_522), max(column_523), min(column_523), max(column_524), min(column_524), max(column_525), min(column_525), max(column_526), min(column_526), max(column_527), min(column_527), max(column_528), min(column_528), max(column_529), min(column_529), max(column_530), min(column_530), max(column_532), min(column_532), max(column_533), min(column_533), max(column_534), min(column_534), max(column_535), min(column_535), max(column_539), min(column_539), max(column_540), min(column_540), max(column_541), min(column_541), max(column_542), min(column_542), max(column_544), min(column_544), max(column_548), min(column_548), max(column_549), min(column_549) FROM ruc_test_3")
data=cur.fetchall()
cur.close()
conn.close()
count=1;
start='<sensor id="random'
middle1='" function-ref-id="'
middleType='-square" max="'
midele2='" min="'
end='" cycle="18200"></sensor>'
min=0
max=0
for i in data[0]:
    #'<sensor id="random1" function-ref-id="double-square" max="5" min="2" cycle="18200"></sensor>
    if count%2==1:
        max=i
   # print(i)
    if count%2==0:
        min=i
        if str(max).find('.')==-1&str(min).find('.')==-1:
            xml = start + str(int(count / 2)) + middle1 + 'int' + middleType + str(max) + midele2 + str(min) + end
        else:
            xml = start + str(int(count / 2)) + middle1+'double'+middleType + str(round(float(max), 4)) + midele2 + str(
                round(float(min), 4)) + end
        print(xml)

    count=count+1;



