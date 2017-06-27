package benchmark;

import java.sql.Timestamp;

import cn.edu.ruc.utils.BizUtils;


public class Test1 {
	public static void main(String[] args) throws Exception {
		String sql="insert into perform_only_write(db_type,batch_id,target_devices_per,target_lines_per,actual_lines_per,operate_time) values(?,?,?,?,?,?)";
		Object[] params={1,2,3,4,5,new Timestamp(System.currentTimeMillis())};
		BizUtils.insertBySqlAndParam(sql, params);
	}
}
