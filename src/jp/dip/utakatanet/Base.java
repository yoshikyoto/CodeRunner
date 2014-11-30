package jp.dip.utakatanet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * for CODE RUNNER
 * @author admin
 *
 */
public class Base{
	public static void p(Object obj){
		System.out.println(obj);
	}
	
	public static void println(Object obj){
		System.out.println(obj);
	}
	
	public static void print(Object obj){
		System.out.print(obj);
	}
	
	public static PrintWriter getPrintWriter(String file) throws IOException{
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		return pw;
	}
	
	public static BufferedReader getFileBr(String file) throws FileNotFoundException{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		return br;
	}
	
	public static BufferedReader getBufferedReader(){
		return new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static String getStringFromFile(String file) throws IOException{
		BufferedReader br = getFileBr(file);
		String line, result = "";
		while((line = br.readLine()) != null)
			result += line + "\n";
		br.close();
		return result;
	}
	
	public static ArrayList<File> listFiles(String dirStr){
		File dir = new File(dirStr);
		ArrayList<File> files = new ArrayList<File>();
		for(File file : dir.listFiles()){
			if(file.getName().indexOf(".") == 0) continue;
			files.add(file);
		}
		return files;
	}
	
	public static ArrayList<File> listDirs(String dirStr){
		File dir = new File(dirStr);
		ArrayList<File> files = new ArrayList<File>();
		for(File file : dir.listFiles()){
			if(file.getName().indexOf(".") == 0) continue;
			if(file.isDirectory()) files.add(file);
		}
		return files;
	}
	

	/**************************************************
	 * Date
	 **************************************************/
	
	public static String getDateString(){
		return getDateString("yyyyMMddhhmmss");
	}
	
	public static String getDateString(String format){
	    Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(cal.getTime());
        return date;
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
    
	
	/**
	 * Code Runner 予選Bの時に使ったGET関数
	 * @param _url
	 * @return
	 * @throws IOException
	 */
    public static String[] info(String _url) throws IOException {
        URL url = new URL(_url);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET"); 
        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String res[] = new String[9];
            for(int i = 0; i < 7; i++){
            	res[i] = reader.readLine();
            }
            
            String line = reader.readLine();
            while((line = reader.readLine()) != null){
                if(line.equals("history")) break;
                res[7] += line + " ";
            }
         
            while((line = reader.readLine()) != null){
                res[8] += line + " ";
            }
            return res;
        }
        return null;
    }
    
	
	/**************************************************
	 * PrintArray
	 **************************************************/
	public static void print(double[] arr){
		for(int i = 0; i < arr.length - 1; i++){
			System.out.print(arr[i] + " ");
		}
		System.out.println(arr[arr.length - 1]);
	}
	
	public static void print(int[] arr){
		for(int i = 0; i < arr.length - 1; i++){
			System.out.print(arr[i] + " ");
		}
		System.out.println(arr[arr.length - 1]);
	}
	
	/**************************************************
	 * AroundArray
	 **************************************************/
	public static double[] plus(double[] arr1, double[] arr2){
		if(arr1.length != arr2.length) return null;
		double[] result = new double[arr1.length];
		for(int i = 0; i < arr1.length; i++)
			result[i] = arr1[i] + arr2[i];
		return result;
	}
	
	// destructive plus
	public static double[] destPlus(double[] arr1, double[] arr2){
		if(arr1.length != arr2.length) return null;
		for(int i = 0; i < arr1.length; i++)
			arr1[i] += arr2[i];
		return arr1;
	}
	
	public static int[] destPlus(int[] arr1, int[] arr2){
		if(arr1.length != arr2.length) return null;
		for(int i = 0; i < arr1.length; i++)
			arr1[i] += arr2[i];
		return arr1;
	}
	
	public static double[] minus(double[] arr1, double[] arr2){
		if(arr1.length != arr2.length) return null;
		double[] result = new double[arr1.length];
		for(int i = 0; i < arr1.length; i++)
			result[i] = arr1[i] - arr2[i];
		return result;
	}

	public static double[] mult(double[] arr, double value){
		double[] result = new double[arr.length];
		for(int i = 0; i < arr.length; i++)
			result[i] = arr[i] * value;
		return result;
	}
	
	public static int[] destMult(int[] arr, int cons){
		for(int i = 0; i < arr.length; i++)
			arr[i] *= cons;
		return arr;
	}
	
	public static double max(double[] arr){
		if(arr.length == 0) return 0.0;
		double result = arr[0];
		for(int i = 0; i < arr.length; i++)
			if(arr[i] > result) result = arr[i];
		return result;
	}
	
	public static int maxIndex(double[] arr){
		if(arr.length == 0) return -1;
		int result = 0;
		for(int i = 0; i < arr.length; i++)
			if(arr[result] < arr[i]) result = i;
		return result;
	}
	
	public static int max(int[] arr){
		if(arr.length == 0) return 0;
		int result = arr[0];
		for(int i = 0; i < arr.length; i++)
			if(arr[i] > result) result = arr[i];
		return result;
	}
	
	public static double min(double[] arr){
		if(arr.length == 0) return 0.0;
		double result = arr[0];
		for(int i = 0; i < arr.length; i++)
			if(arr[i] < result) result = arr[i];
		return result;
	}
	
	public static int min(int[] arr){
		if(arr.length == 0) return 0;
		int result = arr[0];
		for(int i = 0; i < arr.length; i++)
			if(arr[i] < result) result = arr[i];
		return result;
	}
}
