package jp.dip.utakatanet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger extends Base{
	public static PrintWriter p, ep;

	// Class Method
	public static void setLogName(String filename){
		if(p != null) p.close();
		if(ep != null) ep.close();
		
		File dir = new File("log");
		dir.mkdir();

		try {
			File file = new File("log/" + filename + getDateString() + ".log");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			p = new PrintWriter(bw);
			
			File efile = new File("log/" + filename + getDateString() + ".err");
			FileWriter efw = new FileWriter(efile);
			BufferedWriter ebw = new BufferedWriter(efw);
			ep = new PrintWriter(ebw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void print(String str){
		System.out.print(str);
		p.print(str);
	}
	
	public static void println(String str){
		System.out.println(str);
		p.println(str);
		p.flush();
	}
	
	public static void error(String str){
		System.err.print(str);
		ep.print(str);
	}
	
	public static void errorln(String str){
		System.err.println(str);
		ep.println(str);
	}
	
	public static void close(){
		if(p != null) p.close();
		if(ep != null) ep.close();
	}
}