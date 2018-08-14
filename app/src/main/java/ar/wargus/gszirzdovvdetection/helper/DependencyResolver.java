package ar.wargus.gszirzdovvdetection.helper;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class DependencyResolver {
	
	private static Context              context;
	private static Map<String, Object>  parsedObjects           = new HashMap<String, Object>   ();
	private static Map<String, Class>   primitiveNameToClassMap = new HashMap<String, Class>    ();
	
	static{
		primitiveNameToClassMap.put(Character.class .getSimpleName(),
		                            Character.class);
		primitiveNameToClassMap.put(Boolean.class   .getSimpleName(),
		                            Boolean.class);
		primitiveNameToClassMap.put(Integer.class   .getSimpleName(),
		                            Integer.class);
		primitiveNameToClassMap.put(Double.class    .getSimpleName(),
		                            Double.class);
		primitiveNameToClassMap.put(Float.class     .getSimpleName(),
		                            Float.class);
		primitiveNameToClassMap.put(Long.class      .getSimpleName(),
		                            Long.class);
		primitiveNameToClassMap.put(Byte.class      .getSimpleName(),
		                            Byte.class);
		
		primitiveNameToClassMap.put(char.class      .getSimpleName(),
		                            Character.class);
		primitiveNameToClassMap.put(boolean.class   .getSimpleName(),
		                            Boolean.class);
		primitiveNameToClassMap.put(int.class       .getSimpleName(),
		                            Integer.class);
		primitiveNameToClassMap.put(double.class    .getSimpleName(),
		                            Double.class);
		primitiveNameToClassMap.put(float.class     .getSimpleName(),
		                            Float.class);
		primitiveNameToClassMap.put(long.class      .getSimpleName(),
		                            Long.class);
		primitiveNameToClassMap.put(byte.class      .getSimpleName(),
		                            Byte.class);
	}
	
	public static void init(Context context){ DependencyResolver.context = context; }
	
	public static <T> T getFromResourceId(int       resourceId,
	                                      String    resourceName,
	                                      Class<T>  returnType)
		throws IOException,
		       XmlPullParserException,
		       NoSuchMethodException,
		       IllegalAccessException,
		       InvocationTargetException,
		       InstantiationException,
		       ClassNotFoundException {
		if(parsedObjects.containsKey(resourceName)){ return (T) parsedObjects.get(resourceName); }
		
		XmlResourceParser parser = context.getResources()
		                                  .getXml(resourceId);
		
		for(int token   =   parser.next();
		        token   !=  XmlPullParser.END_DOCUMENT;
		        token   =   parser.next()){
			
			if (token == XmlPullParser.START_TAG) {
				Map<String, String> attrNameValueMap = getAttrNameValueMap(parser);
				
				// Sprawdź czy to jest główny tag, którego szukamy i mamy zwrócić
				if (attrNameValueMap.get("name")
				                    .equals(resourceName)) {
					Object currentModel = createObject(parser);
					parsedObjects.put(resourceName,
					                  currentModel);
					
					return returnType.cast(currentModel);
				}
			}
		}
		return null;
	}
	
	//parser z początkowym tagiem
	private static Object createObject(XmlResourceParser parser)
		throws IllegalAccessException,
		       InvocationTargetException,
		       InstantiationException,
		       IOException,
		       XmlPullParserException,
		       NoSuchMethodException,
		       ClassNotFoundException {
		
		// Sprzwdź czy typ primitywny
		if(primitiveNameToClassMap.containsKey(parser.getName())) {
			Class<?>        tagClass        = primitiveNameToClassMap.get(parser.getName());
			Constructor<?>  tagConstructor  = tagClass.getConstructor(String.class);
			
			parser.next();
			return tagConstructor.newInstance(parser.getText());
		}
		
		Map<String, String> attrNameValueMap = getAttrNameValueMap(parser);
		// Sparwdź czy tablica
		if(parser.getName()
		         .equals("array")) {
			Class arrayType = getTagClass(attrNameValueMap.get("type"),
			                              attrNameValueMap);
			Object array = Array.newInstance(arrayType,
			                                 Integer.parseInt(attrNameValueMap.get("size")));
			
			for(int i = 0;
			        i < Integer.parseInt(attrNameValueMap.get("size"));
			        i++){
				if(parser.next() == XmlPullParser.START_TAG){
					String s1 = parser.getName();
					Object o = createObject(parser);
					Array.set(array,
					          i,
					          arrayType.cast(o));
					if(!s1.equals(parser.getName())) parser.next();
				}
			}
			return array;
		}
		
		// Więc będzie to obiekt, sprawdzamy czy już go nie stworzyliśmy
		if(attrNameValueMap.get("name") != null
		   && parsedObjects.containsKey(attrNameValueMap.get("name"))){
			for(int i = 1;
			    i>0;){
				int token = parser.next();
				if(token == XmlPullParser.START_TAG){ i++; }
				else if(token == XmlPullParser.END_TAG){ i--; }}
			return parsedObjects.get(attrNameValueMap.get("name"));
		}
		
		//inicjalizujemy przez domyślny konstruktor
		Class<?> tagClass = getTagClass(parser.getName(),
		                                attrNameValueMap);
		
		Object object = tagClass.getConstructor()
		                        .newInstance();
		
		for(int i = 1;
		        i>0;){
			int token = parser.nextTag();
			if(token == XmlPullParser.START_TAG){
				i++;
				Map<String, String> tokenAttrNameValueMap = getAttrNameValueMap(parser);
				Method tagSetMethod = object.getClass().getDeclaredMethod("set"
						                                                  + tokenAttrNameValueMap.get("name"),
				                                                          getTagClass(parser.getName(),
				                                                                      tokenAttrNameValueMap));
				
				tagSetMethod.invoke(object,
				                    createObject(parser));
			}
			else if(token == XmlPullParser.END_TAG){ i--; }
		}
		
		parsedObjects.put(attrNameValueMap.get("name"),
		                  object);
		
		return object;
	}
	
	private static Map<String, String> getAttrNameValueMap(XmlResourceParser parser){
		Map<String, String> attrNameValueMap = new HashMap<String, String>();
		for(int i = 0;
		    i < parser.getAttributeCount();
		    i++){
			attrNameValueMap.put(parser.getAttributeName(i),
			                     parser.getAttributeValue(i));
		}
		return attrNameValueMap;
	}
	
	private static Class getTagClass(String tagName,
	                                 Map<String, String> attrNameValueMap)
		throws ClassNotFoundException {
		
		if(tagName.equals("array")) return Array.newInstance(getTagClass(attrNameValueMap.get("type"),
		                                                                 attrNameValueMap),
		                                                     0)
		                                        .getClass();
		
		return primitiveNameToClassMap.containsKey(tagName)
		       ? primitiveNameToClassMap.get(tagName)
		       : DependencyResolver.class.getClassLoader()
                                         .loadClass(attrNameValueMap.get("package")
                                                    + tagName);
	}
}
