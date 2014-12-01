package net.logstash.log4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;

@SuppressWarnings("rawtypes")
public class Log4j1_2_14Util {
	static Method getProperties = null;
	static {
		try {
			getProperties = LoggingEvent.class.getMethod("getProperties", null);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
	}
	static Field mdcCopy;
	static {
		try {
			mdcCopy = LoggingEvent.class.getField("mdcCopy");
			mdcCopy.setAccessible(true);
		} catch (NoSuchFieldException e) {
		} catch (SecurityException e) {
		}
	}

	static public Map getProperties(LoggingEvent loggingEvent) {
		if(getProperties !=null){
			Map ret = getByMethod(loggingEvent);
			if(ret!=null)
				return ret;
		}
		if(mdcCopy != null){
			Map ret = getByField(loggingEvent);
			if(ret!=null)
				return ret;
		}
		Hashtable m = MDC.getContext();
		if(m!=null){
			return (Map) m.clone();
		}
		return null;
	}

	private static Map getByField(LoggingEvent loggingEvent) {
		try {
			loggingEvent.getMDCCopy();
			return (Map) mdcCopy.get(loggingEvent);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	private static Map getByMethod(LoggingEvent loggingEvent) {
		try {
			return (Map) getProperties.invoke(loggingEvent, null);
		} catch (IllegalAccessException e1) {
		} catch (IllegalArgumentException e1) {
		} catch (InvocationTargetException e1) {
		}
		return null;
	}
}
