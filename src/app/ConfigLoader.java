package app;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigLoader {
	
	
	public ConfigDto load(String path) {
		Properties proList = new Properties();
		try (FileInputStream file = new FileInputStream(path)){
			proList.load(file);
			
			ConfigDto config = new ConfigDto();
		
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
