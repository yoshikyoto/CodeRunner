import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import jp.dip.utakatanet.*;


public class Main extends Thread{
    static final String TOKEN = "CDOGAMK6XP6MEAOX60AQ2WWEV2PXN35U";
    
	public static void main(String[] args){
		Logger.setLogName("");
		try{
			while(true){
				try{
					String res = httpGet("http://utakatanet.dip.jp");
					p(res);
					Main subThread = new Main(res);
					subThread.start();
				}catch(Exception e){
					e.printStackTrace();
				}
				Thread.sleep(1000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Logger.close();
		}
	}
	
	// 結果の解析に必要な値はイニシャライザを通して渡す
	public String res;
	Main(String res){
		this.res = res;
	}
	
	/**
	 * ここで結果の解析を行う
	 */
	public void run(){
		p("解析");
		Logger.print(res);
	}
	
	/**
	 * HTTPリクエストを送る関数
	 * @param urlstr
	 * @return HTTP OK 以外が帰ってきたら空文字列
	 * @throws IOException
	 */
    public static String httpGet(String urlstr) throws IOException {
        URL url = new URL(urlstr);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET"); 

        // OK が帰ってこなかったら空文字列を返す\
        if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
        	System.err.println("Someting wrong in GET");
        	return "";
        }

        System.out.println("HTTP Status: OK");
        
    	// HttpURLConnection http_connection = (HttpURLConnection)url.openConnection();
    	// http_connection.connect();
    	// InputStreamReader isr = new InputStreamReader(http_connection.getInputStream(), "SHIFT_JIS");
    	// BufferedReader br = new BufferedReader(isr);

        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        String res = "";
        String line = null;

        while((line = reader.readLine()) != null){
        	res += line; // + "\n";
        }
        return res.trim();
    }
    
	public static void p(Object obj){
		System.out.println(obj);
	}
}