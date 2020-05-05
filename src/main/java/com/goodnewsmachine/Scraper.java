package com.goodnewsmachine;

//Import all of Jaunt as it's a relatively small library
import com.jaunt.*;
import java.util.ArrayList;

public class Scraper {
	//Define userAgent
	//Jaunt runs as something called a headless browser
	//For all intents and purposes, this is an actual browser, it just doesn't have a GUI
	//This is really helpful to have as it's a lot more powerful than just sending requests to a website
	//Also allows you to remotely interact with a website like inputting data and clicking on things (with code!!)
	private final UserAgent userAgent;

	public Scraper() {
		//set up our userAgent
		userAgent = new UserAgent();
	}

	//Debug purposes
	public String getInner(String website) {
		try {
			userAgent.visit(website);
			return userAgent.doc.innerHTML();

		} catch (JauntException e) {
			e.printStackTrace();

		}
		return null;
	}

	//This is the unfortunate bit
	//Every news page has a different HTML layout
	//While this looks like repetitive code, it differs just enough for each one that they have to have
	//Different methods. see the BBC links for a good example of this

	public ArrayList<String[]> getGuardianLinks() {
		//Each of these follows a format
		//Wrap everything in a try as Jaunt requires exception handling
		try {
			//Visit the target page
			userAgent.visit("https://www.theguardian.com/uk");
			//Find every <a> tag that matches a given pattern
			//Each page has a different pattern that identifies what is an article
			Elements a = userAgent.doc.findEvery("<a data-link-name=article>");

			//Cast that to an arraylist
			ArrayList<Element> aList = new ArrayList<>(a.toList());

			//hand off to a method
			return getAllLinks(aList);
		}
		catch (JauntException e) {
			e.printStackTrace();
		}
		//Satisfies the compiler
		//Code will never hit here but the compiler needs it "just in case"
		return null;
	}

	public ArrayList<String[]> getAllLinks(ArrayList<Element> aList) {
		//This one is used in every method in here, it gets the headline titles and their URLs and hands it back to GNM
		CvChecker c = new CvChecker();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String[]> output = new ArrayList<>();

		//Trawl through the elements in the arraylist
		for(Element e: aList) {
			//Trim it down and replace all HTML entities with their respective characters and replace double spaces with single space
			String childText = e.getTextContent(" ", true, true).trim().replaceAll("[ ]{2,}", " ");
			childText = childText.replace("&#x27;", "\"").replace("LiveLive", "");
			try {
				//Get the value of the href attribute
				String url = e.getAt("href");

				//Make a pair of the headline and its associated url
				String[] pair = new String[]{childText, url};

				//If we've already seen this headline, ignore it
				//If the headline contains weird formatting (\n), ignore it because it looks weird
				//If the headline is positive, use it!
				if (!names.contains(childText) && !childText.contains("\n") && c.checkPositivity(childText)) {
					//If we haven't seen the headline, it doesn't look weird, and it is positive, add it to the output
					//and also add it to the arraylist where we store our headlines we've seen
					output.add(pair);
					names.add(childText);
				}
				//This error springs if the <a> tag doesn't have href attribute but it should'nt show up
			} catch (NotFound not) {
				not.printStackTrace();
			}
		}

		return output;
	}


	public ArrayList<String[]> getBBCLinks() {
		try {
			userAgent.visit("https://bbc.co.uk/news");
			Elements a = userAgent.doc.findEvery("<a><h3>");

			ArrayList<Element> aList = new ArrayList<>(a.toList());
			ArrayList<Element> outList = new ArrayList<>();
			//This is where it gets complicated for the bbc
			for(Element link : aList) {
				try {
					//The only pattern I could find was that all the articles pointing to URLs end in numbers
					String url = link.getAt("href");

					boolean urlTest;
					String[] splitUrl = url.split("-");
					int urlLen = splitUrl.length;
					//So try and parse the end bit as a number
					try {
						Integer.parseInt(splitUrl[urlLen - 1]);
						urlTest = true;
					//If it breaks and throws a NumberFormatException then it's not pointing to an article
					} catch (NumberFormatException n) {
						urlTest = false;
					}
					//If it is an article
					if(urlTest) {
						//Throw it into the output
						outList.add(link);
					}

				//Ignore any <a> links that don't have href attributes as they're worthless
				} catch(NotFound ignored) {

				}
			}

			return getAllLinks(outList);
		}
		catch (JauntException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String[]> getTelegraphLinks() {
		//The Telegraph is nice and easy
		try {
			userAgent.visit( "https://www.telegraph.co.uk/news/");
			//All articles are tagged with a data-track-block tag
			//So get all <a> that have that tag
			Elements a = userAgent.doc.findEvery("<a data-track-block>");

			ArrayList<Element> aList = new ArrayList<>(a.toList());

			return getAllLinks(aList);
		}
		catch (JauntException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String[]> getIndependentLinks() {
		try {
			userAgent.visit( "https://www.independent.co.uk/");
			//All articles in the independent are <a> tags followed by <h2> tags
			Elements a = userAgent.doc.findEvery("<a>");

			ArrayList<Element> aList = new ArrayList<>(a.toList());

			return getAllLinks(aList);
		}
		catch (JauntException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String[]> getReutersLinks() {
		//Reuters is an interesting one
		//I'm actually scraping from the Oddly Enough section of Reuters as it's still topical but it's
		//more eclectic news and so you're more likely to get nice news from it
		ArrayList<Element> out = new ArrayList<>();
		//Reuters splits their pages really small
		//So there's only like 10 links per page which is pathetic
		//So i'm iterating through 3 of their pages and scraping all the links
		for(int i = 0; i < 4; i ++) {
			try {
				//I'm aware that the pageSize variable on the end of this URL looks like it would change the page Size and solve my problem
				//but it doesn't actually do anything
				userAgent.visit("https://uk.reuters.com/news/archive/oddlyenoughnews?view=page&page=" + i + "&pageSize=10");
				Elements a = userAgent.doc.findEvery("<a><h3 class=story-title>");

				ArrayList<Element> aList = new ArrayList<>(a.toList());

				//All the article urls contain the word "article" so here's a pretty easy check for that
				for (Element e : aList) {
					String link = e.getAt("href");
					if (link.contains("article")) {
						out.add(e);
					}
				}


			} catch (JauntException e) {
				e.printStackTrace();
			}
		}
		return getAllLinks(out);
	}
}