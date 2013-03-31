package com.hailo.local.data;

import java.io.Serializable;
import java.util.List;

public class Results implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<RestaurantData> results;
	private String status;
	private String next_page_token;
	private String[] html_attribs;
	
	
	public List<RestaurantData> getResults() {
		return results;
	}
	public void setResults(List<RestaurantData> results) {
		this.results = results;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNext_page_token() {
		return next_page_token;
	}
	public void setNext_page_token(String next_page_token) {
		this.next_page_token = next_page_token;
	}
	public String[] getHtml_attribs() {
		return html_attribs;
	}
	public void setHtml_attribs(String[] html_attribs) {
		this.html_attribs = html_attribs;
	}
	
	
}
