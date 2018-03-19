package cn.language.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 错误码Root节点实体类
 * @author dingmk
 *
 */
@XStreamAlias("errorConf")
public class ErrorConf 
{
	@XStreamAsAttribute  
    @XStreamAlias("id")  
	private String id;
	
	@XStreamAsAttribute
	@XStreamAlias("longname")
	private String longname;
	
	@XStreamAsAttribute
	@XStreamAlias("package")
	private String packages;
	
	@XStreamAsAttribute
	@XStreamImplicit(itemFieldName = "errors")
	private List<Errors> errors;
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
	public List<Errors> getErrors() {
		return errors;
	}
	public void setErrors(List<Errors> errors) {
		this.errors = errors;
	}
	public String getPackages() {
		return packages;
	}
	public void setPackages(String packages) {
		this.packages = packages;
	}
	@Override
	public String toString() {
		return "ErrorConf [id=" + id + ", longname=" + longname + ", errors="
				+ errors + "]";
	}
	
}