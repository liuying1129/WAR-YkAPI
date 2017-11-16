package com.yklis.entity;

public class WorkerEntity {
	
	private int unid ;
	private int pkDeptId ;
	private String passwd ;
	private String pinYin;
	private String wbm ;
	private String accountLimit ;
	private String id ;
	private String name ;
	private String ifSuperUser ;
	private String showAllTj ;

	public int getUnid() {
		return this.unid;
	}

	public void setUnid(int unid) {
		this.unid = unid;
	}

	public int getPkDeptId() {
		return this.pkDeptId;
	}

	public void setPkDeptId(int pkDeptId) {
		this.pkDeptId = pkDeptId;
	}

	public String getPasswd() {
		return this.passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPinYin() {
		return this.pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}

	public String getWbm() {
		return this.wbm;
	}

	public void setWbm(String wbm) {
		this.wbm = wbm;
	}

	public String getAccountLimit() {
		return this.accountLimit;
	}

	public void setAccountLimit(String accountLimit) {
		this.accountLimit = accountLimit;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIfSuperUser() {
		return this.ifSuperUser;
	}

	public void setIfSuperUser(String ifSuperUser) {
		this.ifSuperUser = ifSuperUser;
	}

	public String getShowAllTj() {
		return this.showAllTj;
	}

	public void setShowAllTj(String showAllTj) {
		this.showAllTj = showAllTj;
	}
}
