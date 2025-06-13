package app.config;

import java.io.FileInputStream;
import java.util.Properties;

public class SettingLoader {
	
	
	public AppSettingDto load(String path) {
		Properties proList = new Properties();
		try (FileInputStream file = new FileInputStream(path)){
			proList.load(file);
			
			
		
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
