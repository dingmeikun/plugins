package cn.language.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("errors")
public class Errors
{
	@XStreamAlias("id")
	@XStreamAsAttribute
	private String id;
	@XStreamAlias("longname")
	@XStreamAsAttribute
	private String longname;
	
	@XStreamImplicit(itemFieldName = "error")
	private List<Error> error;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLongname() {
		return longname;
	}
	public void setLongname(String longname) {
		this.longname = longname;
	}
	public List<Error> getError() {
		return error;
	}
	public void setError(List<Error> error) {
		this.error = error;
	}
}
