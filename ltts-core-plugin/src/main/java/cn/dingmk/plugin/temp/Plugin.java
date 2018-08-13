package cn.dingmk.plugin.temp;

public class Plugin {

	private String name = "dingmk"; // 插件值可以从解析器中获取
	
	private String jar;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJar() {
		return jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}
	
}
