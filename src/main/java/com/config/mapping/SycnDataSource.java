package com.config.mapping;

import lombok.Data;

public class SycnDataSource {
	/**数据源名称*/
	public String name;
	/**数据驱动名称*/
	public String driverclassname;
	/**数据源url*/
	public String url;
	/**用户名称*/
	public String username;
	/**用户密码*/
	public String password;

	public String getName() {
		return name;
	}

	public String getDriverclassname() {
		return driverclassname;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDriverclassname(String driverclassname) {
		this.driverclassname = driverclassname;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
