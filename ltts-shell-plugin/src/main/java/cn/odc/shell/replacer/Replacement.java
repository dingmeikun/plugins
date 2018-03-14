package cn.odc.shell.replacer;

public class Replacement {

	private DelimiterBuilder delimiter;
	private String token;
	private String value;

	public Replacement() {
	}

	public Replacement(String token, String value) {
		this.token = token;
		this.value = value;
		this.delimiter = DelimiterBuilder.defInstance();
	}

	public String getToken() {
		if (this.delimiter != null) {
			return this.delimiter.apply(this.token);
		}
		return this.token;
	}

	public String getValue() {
		return this.value;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDelimiter(DelimiterBuilder delimiter) {
		this.delimiter = delimiter;
	}
}
