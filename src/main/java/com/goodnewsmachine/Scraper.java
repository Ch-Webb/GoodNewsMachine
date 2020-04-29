package com.goodnewsmachine;

import com.jaunt.*;

import java.util.ArrayList;

public class Scraper {
	public static void main(String[] args) {

	}

	public Scraper() {

	}

	//Debug purposes
	public String getInner(String website) {
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(website);
			return userAgent.doc.innerHTML();

		} catch (JauntException e) {
			e.printStackTrace();

		}
		return null;
	}

	public ArrayList<String[]> getGuardianLinks() {
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.visit("https://www.theguardian.com/uk");
			Elements a = userAgent.doc.findEvery("<a data-link-name=article>");

			ArrayList<Element> aList = new ArrayList<>(a.toList());

			return getAllLinks(aList);
		}
		catch (JauntException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String[]> getAllLinks(ArrayList<Element> aList) {

		ArrayList<String> names = new ArrayList<>();
		ArrayList<String[]> output = new ArrayList<>();

		for(Element e: aList) {
			String childText = e.getTextContent(" ", true, true).trim();
			try {
				String url = e.getAt("href");

				String[] pair = new String[]{childText, url};

				if (!names.contains(childText) && !childText.contains("\n")) {
					output.add(pair);
					names.add(childText);
				}
			} catch (NotFound not) {
				not.printStackTrace();
			}
		}

		return output;
	}

	public ArrayList<String[]> getBBCLinks() {
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.visit("https://bbc.co.uk/news");
			Elements a = userAgent.doc.findEvery("<a><h3>");

			ArrayList<Element> aList = new ArrayList<>(a.toList());
			ArrayList<Element> outList = new ArrayList<>();
			for(Element link : aList) {
				try {
					String url = link.getAt("href");

					boolean urlTest;
					String[] splitUrl = url.split("-");
					int urlLen = splitUrl.length;
					try {
						Integer.parseInt(splitUrl[urlLen - 1]);
						urlTest = true;
					} catch (NumberFormatException n) {
						urlTest = false;
					}
					if(urlTest) {
						outList.add(link);
					}

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
}