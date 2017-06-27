package benchmark;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpTest {
	public static void main(String[] args) throws IOException {
		
		//1, 导包  
		//2, 得到OkHttp的客户端  
		try {
			String url="http://114.115.137.143:8086/write?db=ruc_perform";
			OkHttpClient client = new OkHttpClient();  
			//3, 发起新的请求, 得到响应对象  
			FormBody.Builder builder=new FormBody.Builder();
//			builder.add("name","haha");
//			FormBody body = builder.build();
			RequestBody body = MultipartBody.create(MediaType.parse("text/plain"),"cpu_load_short,host=server02 value=0.67");
			Request request = new Request.Builder()  
			.url(url).post(body)  
			.build();  
			Response response = client.newCall(request).execute();  
			System.out.println(response);
			if(response.isSuccessful())  
			{  	
				System.out.println(response);
				System.out.println(response.body().string());
				//获取要访问资源的byte数组  
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}

