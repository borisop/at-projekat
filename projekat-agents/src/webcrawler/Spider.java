package webcrawler;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileSystemView;

import filewriter.WriteFile;

public class Spider {
	
	private static final int MAX_PAGES_TO_SEARCH = 30;
	private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();
	
	private String nextUrl() {
		String nextUrl;
		do {
			nextUrl = this.pagesToVisit.remove(0);
		} while (this.pagesVisited.contains(nextUrl));
		
		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}
	
	public void search(String url, String searchWord) {
		int counter = 0;
		while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
			String currentUrl;
			SpiderLeg leg = new SpiderLeg();
			if (this.pagesToVisit.isEmpty()) {
				currentUrl = url;
				this.pagesVisited.add(url);
			} else {
				currentUrl = this.nextUrl();
			}
			leg.crawl(currentUrl);
			
			boolean success = leg.searchForWord(searchWord);
			if (success) {
				System.out.println(String.format("**Success** Word %s found at %s", searchWord, currentUrl));
				
				String text = leg.getDocumentText();
				File home = FileSystemView.getFileSystemView().getHomeDirectory();
				String path = home.getAbsolutePath() + "\\spider"+ counter +".txt";
				++counter;
				System.out.println(String.format("**Saving file at %s", path));
				WriteFile wf= new WriteFile(path);
				try {
					wf.writeToFile(text);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			this.pagesToVisit.addAll(leg.getLinks());
		}
		System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
	}
	
}
