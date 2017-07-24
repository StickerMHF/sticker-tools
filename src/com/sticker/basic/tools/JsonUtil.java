package com.sticker.basic.tools;

import io.vertx.core.json.JsonObject;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
	public static String objectToJson(Object object) {
		StringBuilder json = new StringBuilder();
		if (object == null) {
			json.append("\"\"");
		} else if (object instanceof String || object instanceof Integer) {
			json.append("\"").append(object.toString()).append("\"");
		} else if (object instanceof ArrayList) {
			List<?> list=(ArrayList) object;
			if(list.get(0).getClass().getSimpleName().equals("JsonObject")){
				return "JsonObject";
			} 
		} else {
			json.append(beanToJson(object));
		}
		return json.toString();
	}

	public static String beanToJson(Object bean) {
		StringBuilder json = new StringBuilder();
		System.out.println(bean.getClass().getSimpleName());
		
			json.append("{");
			PropertyDescriptor[] props = null;
			try {
				props = Introspector.getBeanInfo(bean.getClass(), Object.class)
						.getPropertyDescriptors();
			} catch (IntrospectionException e) {
			}
			if (props != null) {
				for (int i = 0; i < props.length; i++) {
					try {
						String name = objectToJson(props[i].getName());
						String value = objectToJson(props[i].getReadMethod()
								.invoke(bean));
						if(value!=null){
							json.append(name);
							json.append(":");
							json.append(value);
							json.append(",");
						}
							
						
					} catch (Exception e) {
					}
				}
				json.setCharAt(json.length() - 1, '}');
			} else {
				json.append("}");
			}
			return json.toString();
		
	}

	public static String listToJson(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/*
	 * public static String listModelToJson(List<BTaskModel> list) { String s =
	 * listModelToJsonString(list);
	 * 
	 * return s; }
	 */

}
