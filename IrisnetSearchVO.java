package com.example.maventest;

public class IrisnetSearchVO extends SearchDefaultVO {

	private String srchFd;
	private String kwd;
	private String category;
	private String sort;
	private String ipcType;
	private String techPft;
	private String techDrvYr;
	private String ipcCd;
	private String[] mtrSourceNm;
	private String sn;

	private String techPftFacet;
	private String ipcCdFacet;
	private String ipcTypeFacet;
	private String mtrSourceNmFacet;

	private String hlTag ="b";
	private String query;

	public String getSrchFd() {
		return srchFd;
	}
	public void setSrchFd(String srchFd) {
		this.srchFd = srchFd;
	}
	public String getKwd() {
		return kwd;
	}
	public void setKwd(String kwd) {
		this.kwd = kwd;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getIpcType() {
		return ipcType;
	}
	public void setIpcType(String ipcType) {
		this.ipcType = ipcType;
	}
	public String getTechPft() {
		return techPft;
	}
	public void setTechPft(String techPft) {
		this.techPft = techPft;
	}
	public String getTechDrvYr() {
		return techDrvYr;
	}
	public void setTechDrvYr(String techDrvYr) {
		this.techDrvYr = techDrvYr;
	}
	public String getIpcCd() {
		return ipcCd;
	}
	public void setIpcCd(String ipcCd) {
		this.ipcCd = ipcCd;
	}
	public String[] getMtrSourceNm() {
		return mtrSourceNm;
	}
	public void setMtrSourceNm(String[] mtrSourceNm) {
		this.mtrSourceNm = mtrSourceNm;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getTechPftFacet() {
		return techPftFacet;
	}
	public void setTechPftFacet(String techPftFacet) {
		this.techPftFacet = techPftFacet;
	}
	public String getMtrSourceNmFacet() {
		return mtrSourceNmFacet;
	}
	public void setMtrSourceNmFacet(String mtrSourceNmFacet) {
		this.mtrSourceNmFacet = mtrSourceNmFacet;
	}
	public String getIpcCdFacet() {
		return ipcCdFacet;
	}
	public void setIpcCdFacet(String ipcCdFacet) {
		this.ipcCdFacet = ipcCdFacet;
	}
	public String getIpcTypeFacet() {
		return ipcTypeFacet;
	}
	public void setIpcTypeFacet(String ipcTypeFacet) {
		this.ipcTypeFacet = ipcTypeFacet;
	}
	public String getHlTag() {
		return hlTag;
	}
	public void setHlTag(String hlTag) {
		this.hlTag = hlTag;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}


}