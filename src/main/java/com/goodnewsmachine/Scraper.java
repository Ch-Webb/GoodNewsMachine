package com.goodnewsmachine;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

import java.util.ArrayList;

public class Scraper {
	public static void main(String[] args) {

	}

	public Scraper() {

	}

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


	public ArrayList<String> getAllLinks(String website) {
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(website);
			Elements a = userAgent.doc.findEvery("<h3><a>");

			ArrayList<Element> aList = new ArrayList<>(a.toList());
			ArrayList<String> names = new ArrayList<>();
			for(Element e: aList) {
				names.add(e.getChildText());
			}
			return names;
		}
		catch (JauntException e) {
			e.printStackTrace();
		}
		return null;
	}
}