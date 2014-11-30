import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import jp.dip.utakatanet.*;


public class Main extends Thread{
    static final String TOKEN = "CDOGAMK6XP6MEAOX60AQ2WWEV2PXN35U";
    
	public static void main(String[] args){
		Logger.setLogName("");
		try{
			while(true){
				try{
					String res = httpGet("http://utakatanet.dip.jp");
					
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

class JsonSample {
    public static Map<String, Object> parse(String script) throws Exception {
        // 起動時にオプションを指定しなかった場合は、このサンプルデータを使用する。
        //String script = "{ \"key1\" : \"val1\", \"key2\" : \"val2\", \"key3\" : { \"ckey1\" : \"cval1\", \"ckey2\" : [ \"cval2-1\", \"cval2-2\" ] } }";
        
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        // ScriptEngine の eval に JSON を渡す時は、括弧で囲まないと例外が発生します。eval はセキュリティ的には好ましくないので、安全であることが不明なデータを扱うことは想定していません。
        Object obj = engine.eval(String.format("(%s)", script));
        // Rhino は、jdk1.6,7までの JavaScript エンジン。jdk1.8は「jdk.nashorn.api.scripting.NashornScriptEngine」
        Map<String, Object> map = jsonToMap(obj, engine.getClass().getName().equals("com.sun.script.javascript.RhinoScriptEngine"));
        System.out.println(map.toString());
        return map;
    }
    
    static Map<String, Object> jsonToMap(Object obj, boolean rhino) throws Exception {
        // Nashorn の場合は isArray で obj が配列かどうか判断できますが、特に何もしなくても配列番号をキーにして値を取得し Map に格納できるので、ここでは無視しています。
        // Rhino だとインデックスを文字列として指定した場合に値が返ってこないようなので、仕方なく処理を切り分けました。
        // 実際は HashMap なんか使わずに自分で定義したクラス（配列はそのオブジェクトの List プロパティ）にマップすることになると思うので、動作サンプルとしてはこんなもんでよろしいかと。
        boolean array = rhino ? Class.forName("sun.org.mozilla.javascript.internal.NativeArray").isInstance(obj) : false;
        Class scriptObjectClass = Class.forName(rhino ? "sun.org.mozilla.javascript.internal.Scriptable" : "jdk.nashorn.api.scripting.ScriptObjectMirror");
        // キーセットを取得
        Object[] keys = rhino ? (Object[])obj.getClass().getMethod("getIds").invoke(obj) : ((java.util.Set)obj.getClass().getMethod("keySet").invoke(obj)).toArray();
        // get メソッドを取得
        Method method_get = array ? obj.getClass().getMethod("get", int.class, scriptObjectClass) : (rhino ? obj.getClass().getMethod("get", Class.forName("java.lang.String"), scriptObjectClass) : obj.getClass().getMethod("get", Class.forName("java.lang.Object")));
        Map<String, Object> map = new HashMap<String, Object>();
        for (Object key : keys) {
            Object val = array ? method_get.invoke(obj, (Integer)key, null) : (rhino ? method_get.invoke(obj, key.toString(), null) : method_get.invoke(obj, key));
            if (scriptObjectClass.isInstance(val)) {
                map.put(key.toString(), jsonToMap(val, rhino));
            } else {
                map.put(key.toString(), val.toString()); // サンプルなので、ここでは単純に toString() してますが、実際は val の型を有効に活用した方が良いでしょう。
            }
        }
        return map;
    }
}