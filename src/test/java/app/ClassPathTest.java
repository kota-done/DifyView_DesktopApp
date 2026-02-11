package app;

import java.net.URL;

import app.util.CommonFunction;

public class ClassPathTest {


	    public static void main(String[] args) {
//	        System.out.println("現在のクラスパス：");
//	        String classpath = System.getProperty("java.class.path");
//	        for (String path : classpath.split(System.getProperty("path.separator"))) {
//	            System.out.println(path);
//	        }
//
//	        System.out.println("\nリソースルートに対するClassLoaderの動作確認：");
//	        ClassLoader classLoader = ClassPathTest.class.getClassLoader();
//	        System.out.println("リソース 'app.properties' のURL: " +
//	            classLoader.getResource("resources/app.properties"));
	    	URL resourceUrl = CommonFunction.class.getResource("/resources/app.properties");
	    	System.out.println("Resource URL: " + resourceUrl);
//	    	InputStream test = CommonFunctionTest.class.getResourceAsStream("/resources/app.properties");
//	    	System.out.println("testresult:"+test);
	    }
	

}
