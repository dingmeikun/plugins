package cn.dingmk.plugin.parser;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import cn.dingmk.plugin.utils.CoreUtil;

/**
 * Jaxb 解析器，解析构造器传入的classes以及*.plugin.xml的inputstream,为classs实例
 * 
 * @author dingmk
 *
 */
public class XmlConfigManager {
	
	private JAXBContext context;
	
	public XmlConfigManager(Class<?>[] classes)
	{
		try
		{
			this.context = JAXBContext.newInstance(classes);
	    } catch (JAXBException e) {
	       throw CoreUtil.wrapThrow("JAXBContext init error", e, new String[0]);
	    }
	}
	
	protected Marshaller getMarshaller() {
		try {
			Marshaller ret = this.context.createMarshaller();
		 	ret.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
	       
		  	return ret;
	  	} catch (javax.xml.bind.PropertyException e) {
	  		throw new IllegalArgumentException(e);
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	protected Unmarshaller getUnmarshaller() {
		try {
			return this.context.createUnmarshaller();
	 	}
	   	catch (JAXBException e)
	   	{
			throw new IllegalArgumentException(e);
	  	}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T load(InputStream input)
	{
		try
	  	{
			return (T) getUnmarshaller().unmarshal(input);
	  	} catch (Exception e) {
	  		throw CoreUtil.wrapThrow("jaxb load error", e, new String[0]);
		}
	}
	
	public void save(Object o, OutputStream output) {
	try {
		getMarshaller().marshal(o, output);
	} catch (JAXBException e) {
		throw CoreUtil.wrapThrow("jaxb save error", e, new String[0]);
		}
	}
}
