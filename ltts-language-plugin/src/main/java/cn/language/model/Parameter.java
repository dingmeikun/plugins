package cn.language.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("parameter")
public class Parameter
{
	@XStreamAlias("id")
	@XStreamAsAttribute
	private String id;
	
	@XStreamAlias("type")
	@XStreamAsAttribute
	private String Type;
	
	@XStreamAlias("longname")
	@XStreamAsAttribute
	private String longname;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getLongname() {
		return longname;
	}
	public void setLongname(String longname) {
		this.longname = longname;
	}
}
