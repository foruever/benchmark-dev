package com.ruc.model;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import com.ruc.CommonUtils;
import com.ruc.constant.FunctionTypeEnum;
import com.ruc.constant.TypeEnum;
import com.ruc.constant.XmlNodeAttrEnum;
import com.ruc.constant.XmlNodeNameEnum;

/**
 * 节点属性(实体类)
 * @author sxg
 */
public class XmlNodeAttr {
	public XmlNodeAttr() {
		super();
	}
	public XmlNodeAttr(Element element) {
		super();
		if(element==null){
			return;
		}
		this.element = element;
		setName(element.attributeValue(XmlNodeAttrEnum.NAME.getName(), "default"));
		setValue(element.attributeValue(XmlNodeAttrEnum.VALUE.getName(),""));
		if(XmlNodeNameEnum.KEY.getName().equals(element.getName())
				||XmlNodeNameEnum.COLUMN.getName().equals(element.getName())
				||XmlNodeNameEnum.VALUE.getName().equals(element.getName())){
			if(StringUtils.isBlank(element.attributeValue(XmlNodeAttrEnum.TYPE.getName()))){
				CommonUtils.stopThreadAndPrint(element.getName()+"标签type属性不得为空");
			}
		}
		setType(element.attributeValue(XmlNodeAttrEnum.TYPE.getName(),""));
		setFunctionType(element.attributeValue(XmlNodeAttrEnum.FUNCTION_TYPE.getName(),FunctionTypeEnum.RANDOM.getName()));
//		setOrder(element.attributeValue(XmlNodeAttrEnum.ORDER.getName(),"0"));//顺序貌似没用，去掉
		setActive(element.attributeValue(XmlNodeAttrEnum.ACTIVE.getName(),""));
		setType(element.attributeValue(XmlNodeAttrEnum.TYPE.getName(),""));
		String maxStr = element.attributeValue(XmlNodeAttrEnum.MAX.getName(),Long.MAX_VALUE+"");
		try {
			setMax(Long.parseLong(maxStr));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			CommonUtils.stopThreadAndPrint("max属性异常，必须为数字["+maxStr+"]");
		}
		String minStr = element.attributeValue(XmlNodeAttrEnum.MIN.getName(),Long.MIN_VALUE+"");
		try {
			setMin(Long.parseLong(minStr));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			CommonUtils.stopThreadAndPrint("min属性异常，必须为数字["+minStr+"]");
		}
		if(min>max){
			CommonUtils.stopThreadAndPrint("min不可以大于max，min为["+minStr+"]，max为["+maxStr+"]");
		}
	}
	
	private String name;
	private String value;
	private String type;
	private String functionType;
	private Integer order;
	private String active;
	private long max;
	private long min;
	private Element element;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFunctionType() {
		return functionType;
	}
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public long getMax() {
		return max;
	}
	public void setMax(long max) {
		this.max = max;
	}
	public long getMin() {
		return min;
	}
	public void setMin(long min) {
		this.min = min;
	}
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
	@Override
	public String toString() {
		return "XmlNodeAttr [name=" + name + ", value=" + value + ", type="
				+ type + ", functionType=" + functionType + ", order=" + order
				+ ", active=" + active + ", max=" + max + ", min=" + min + "]";
	}
}
