package com.example.maventest;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

public class ESResult {
	private Map<String, Object> hitList = new HashMap<String, Object>();
	private Map<String, HighlightField> hlList = new HashMap<String, HighlightField>();
	
	public Map<String, Object> getHitList() {
		return hitList;
	}
	public void setHitList(Map<String, Object> hitList) {
		this.hitList = hitList;
	}
	public Map<String, HighlightField> getHlList() {
		return hlList;
	}
	public void setHlList(Map<String, HighlightField> hlList) {
		this.hlList = hlList;
	}
	
	
	
	
}
