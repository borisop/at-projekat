package filewriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteFile {

	private String path;
	
	public WriteFile(String filePath) {
		this.path = filePath;
	}
	
	public void writeToFile(String textLine) throws IOException {
		FileWriter write = new FileWriter(path, false);
		PrintWriter printLine = new PrintWriter(write);
		printLine.printf("%s" + "%n", textLine);
		printLine.close();
	}
}
