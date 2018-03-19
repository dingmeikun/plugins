package cn.language.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("error")
public class Error
{
	@XStreamAlias("id")
	@XStreamAsAttribute
	private String id;
	@XStreamAlias("type")
	@XStreamAsAttribute
	private String type;
	@XStreamAlias("message")
	@XStreamAsAttribute
	private String message;
	@XStreamAlias("errorCode")
	@XStreamAsAttribute
	private String errorCode;
	
	@XStreamImplicit(itemFieldName = "parameter")
	private List<Parameter> parameter;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public List<Parameter> getParameter() {
		return parameter;
	}
	public void setParameter(List<Parameter> parameter) {
		this.parameter = parameter;
	}
}
