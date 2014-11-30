import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import jp.dip.utakatanet.*;

/**
 * CODE RUNNER 本専用クラス
 * @author yoshiki_utakata
 *
 */
public class Main extends Thread{
    
    static int[] stones = new int[25];

    static final String TOKEN = "CDOGAMK6XP6MEAOX60AQ2WWEV2PXN35U";
	static Random rand = new Random();
	
	public static void main(String[] args){
		Logger.setLogName("");
		try{
			while(true){
				try{
					//String res = httpGet("https://game.coderunner.jp/info.txt?token=" + TOKEN);
					status();
					// Main subThread = new Main(res);
					// subThread.start();
					change();
					summon();
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
        	res += line + "\n";
        }
        return res.trim();
    }
    
    // ここは手動で調整
    // maxstone:border のよさげな組み合わせ
    //5:10000 10:80000 15:150000 20:500000 25:800000;
    static final int border = 0;
    static final int maxstone = 25;
    static final int changeCount = 5;
    public static void summon() throws IOException{
    	String filestr = "monsters.txt";
		FileReader fr = new FileReader(filestr);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		
		// モンスターのスコアリストを取得
		String[] strs = httpGet("https://game.coderunner.jp/scorelist").split("\n");
		// p("モンスタースコアリスト");
		// for(int i = 0; i < strs.length; i++) p(strs[i]);

		int max = -1, maxid = -1;
		// 貴重な石は使わないように
		for(int i = maxstone ; i < 25; i++) stones[i] = 0;
		while((line = br.readLine()) != null){
			// 1行に1モンスター
			strs = line.split(",");
			// 召喚できるならフラグがtrue
			boolean flag = true;
			for(int i = 0; i < 25; i++){
				int cost = Integer.parseInt(strs[i+2]);
				if(cost > stones[i]){
					flag = false;
					break;
				}
			}
			// スコアをチェックしてmaxを見る
			int score = Integer.parseInt(strs[27]);
			if(flag && max < score){
				max = score;
				maxid = Integer.parseInt(strs[0]);
			}
		}

		// ボーダーを超えてたら召喚
		Logger.println("一番高いモンスター: " + max + "\tid:" + maxid);
		if(max >= border){
			Logger.println("召喚 ID: " + maxid);
			String res = httpGet("https://game.coderunner.jp/summon?monster=" + maxid + "&token=" + TOKEN);
			Logger.p("取引結果: " + res);
		}
    }
    
    /**
     * 石交換メソッド
     * @throws IOException 
     */
    static int count = 2;
    public static void change() throws IOException{
    	// まずランクで分ける
    	// 石8と
    	count++;
    	count = count % 3;
    	int[][] arr = {
    			{5, 6, 7, 8, 9},
    			{10, 11, 12, 13, 14},
    			{15, 16, 17, 18, 19},
    			{20, 21, 22, 23, 24}
    			};
    	int num = changeCount;
    	if(count == 3) num = 2;
    	int diff = 0;
    	diff = 1 - rand.nextInt(3);
    	decideAnotherChange(arr[count + diff], arr[count], 5);
    }
    
    public static void decideAnotherChange(int[] arr1, int arr2[], int num) throws IOException{
    	int min = 100000, max = -1, minindex = -1, maxindex = -1;
    	for(int i : arr1){
    		if(max < stones[i]){
    			max = stones[i];
    			maxindex = i;
    		}
    	}

    	for(int i : arr2){
    		if(min > stones[i]){
    			min = stones[i];
    			minindex = i;
    		}
    	}

    	p("max: " + max  + " min:" + min);
    	if(maxindex == -1 || minindex == -1) return;
    	if(max < 10) return;
    	if(max > min * 1.2) changeRequest(minindex, maxindex, num);
    }
    
    public static void decideChange(int[] arr, int num) throws IOException{
    	p("Decide Change");
    	int min = 100000, max = -1, minindex = -1, maxindex = -1;
    	// max と min を調べる
    	for(int i : arr){
    		if(max < stones[i]){
    			max = stones[i];
    			maxindex = i;
    		}else if(min > stones[i]){
    			min = stones[i];
    			minindex = i;
    		}
    	}
    	p("max: " + max  + " min:" + min);
    	if(maxindex == -1 || minindex == -1) return;
    	if(max > min * 1.2) changeRequest(minindex, maxindex, num);
    }
    
    public static void changeRequest(int in, int out, int num) throws IOException{
    	Logger.println("求:" + in + " 出:" + out + " 数:" + num);
    	String str = "https://game.coderunner.jp/trade?in=" + in + "&out=" + out + "&num="+ num + "&token=" + TOKEN;
    	p(str);
    	httpGet(str);
    }
    
    public static String status() throws IOException {
        URL url = new URL("https://game.coderunner.jp/info.txt?token=" + TOKEN);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET"); 

        // OK が帰ってこなかったら空文字列を返す\
        if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
        	System.err.println("Someting wrong in GET");
        	return "";
        }
        
    	// HttpURLConnection http_connection = (HttpURLConnection)url.openConnection();
    	// http_connection.connect();
    	// InputStreamReader isr = new InputStreamReader(http_connection.getInputStream(), "SHIFT_JIS");
    	// BufferedReader br = new BufferedReader(isr);

        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String res = "";
        String line = null;

        br.readLine(); // 名前を捨てる
        Logger.println("スコア: " + br.readLine()); // スコアの情報を捨てる
        String str = br.readLine();
        String[] strs = str.split(" ");
        for(int i = 0; i < 25; i++)
        	stones[i] = Integer.parseInt(strs[i]);
        Logger.println(str);
        return res.trim();
    }
    
	public static void p(Object obj){
		System.out.println(obj);
	}
}

/**
 * 拾ってきたJsonパースライブラリ。 JsonSample.parse(String json_str) でパースできる。 
 * 結果は map<String, Object> で返ってくる。
 * 参考: http://gootara.org/library/2014/04/javaapijsonjdk1618.html
 * @author yoshiki_utakata
 *
 */
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