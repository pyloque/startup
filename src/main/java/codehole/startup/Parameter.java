package codehole.startup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameter {
	private String type;
	private String value;

	public Parameter(String type, String value) {
		this.type = type;
		this.value = value;
	}

	public void type(Class<?> clazz) {
		this.type = classTypes.get(clazz);
	}

	public Object value() {
		if (type == null) {
			return value;
		}
		IConverter<?> converter = typeConverters.get(type);
		if (converter == null) {
			throw new StartupException("converter not found for type=" + type);
		}
		try {
			return converter.parse(value);
		} catch (NumberFormatException e) {
			throw new StartupException(String.format("parse error value=%s with type=", value, type));
		}
	}

	public Class<?> clazz() {
		if (type == null) {
			return null;
		}
		return typeClasses.get(type);
	}

	static interface IConverter<T> {
		T parse(String value);
	}

	static <T> Object toArray(String s, Class<T> clazz, IConverter<T> converter) {
		String[] parts = s.trim().split(",");
		Object result = Array.newInstance(clazz, parts.length);
		for (int i = 0; i < parts.length; i++) {
			Array.set(result, i, converter.parse(parts[i]));
		}
		return result;
	}

	static <T> List<T> toList(String s, Class<T> clazz, IConverter<T> converter) {
		List<T> result = new ArrayList<T>();
		for (String part : s.trim().split(",")) {
			result.add(converter.parse(part));
		}
		return result;
	}

	static Map<String, IConverter<?>> typeConverters = new HashMap<>();
	static Map<Class<?>, String> classTypes = new HashMap<>();
	static Map<String, Class<?>> typeClasses = new HashMap<>();

	static {
		typeConverters.put("byte", (s) -> Byte.parseByte(s));
		typeConverters.put("short", (s) -> Short.parseShort(s));
		typeConverters.put("int", (s) -> Integer.parseInt(s));
		typeConverters.put("long", (s) -> Long.parseLong(s));
		typeConverters.put("bool", (s) -> Boolean.parseBoolean(s));
		typeConverters.put("char", (s) -> s.charAt(0));
		typeConverters.put("str", (s) -> s);
		typeConverters.put("null", (s) -> null);
		
		typeConverters.put("Byte", (s) -> Byte.valueOf(s));
		typeConverters.put("Short", (s) -> Short.valueOf(s));
		typeConverters.put("Int", (s) -> Integer.valueOf(s));
		typeConverters.put("Long", (s) -> Long.valueOf(s));
		typeConverters.put("Bool", (s) -> Boolean.valueOf(s));
		typeConverters.put("Char", (s) -> s.charAt(0));

		typeConverters.put("byte[]", (s) -> {
			return toArray(s, byte.class, (v) -> Byte.parseByte(v));
		});
		typeConverters.put("short[]", (s) -> {
			return toArray(s, short.class, (v) -> Short.parseShort(v));
		});
		typeConverters.put("int[]", (s) -> {
			return toArray(s, int.class, (v) -> Integer.parseInt(v));
		});
		typeConverters.put("long[]", (s) -> {
			return toArray(s, long.class, (v) -> Long.parseLong(v));
		});
		typeConverters.put("bool[]", (s) -> {
			return toArray(s, boolean.class, (v) -> Boolean.parseBoolean(v));
		});
		typeConverters.put("char[]", (s) -> {
			return toArray(s, char.class, (v) -> v.charAt(0));
		});
		typeConverters.put("str[]", (s) -> {
			return toArray(s, String.class, (v) -> v);
		});
		typeConverters.put("Byte[]", (s) -> {
			return toArray(s, Byte.class, (v) -> Byte.valueOf(v));
		});
		typeConverters.put("Short[]", (s) -> {
			return toArray(s, Short.class, (v) -> Short.valueOf(v));
		});
		typeConverters.put("Int[]", (s) -> {
			return toArray(s, Integer.class, (v) -> Integer.valueOf(v));
		});
		typeConverters.put("Long[]", (s) -> {
			return toArray(s, Long.class, (v) -> Long.valueOf(v));
		});
		typeConverters.put("Bool[]", (s) -> {
			return toArray(s, Boolean.class, (v) -> Boolean.valueOf(v));
		});
		typeConverters.put("Char[]", (s) -> {
			return toArray(s, Character.class, (v) -> v.charAt(0));
		});

		classTypes.put(Byte.class, "byte");
		classTypes.put(Short.class, "short");
		classTypes.put(Integer.class, "int");
		classTypes.put(Long.class, "long");
		classTypes.put(Boolean.class, "bool");
		classTypes.put(Character.class, "char");
		classTypes.put(String.class, "str");
		classTypes.put(byte.class, "byte");
		classTypes.put(short.class, "short");
		classTypes.put(int.class, "int");
		classTypes.put(long.class, "long");
		classTypes.put(boolean.class, "bool");
		classTypes.put(char.class, "char");
		classTypes.put(byte[].class, "byte[]");
		classTypes.put(short[].class, "short[]");
		classTypes.put(int[].class, "int[]");
		classTypes.put(long[].class, "long[]");
		classTypes.put(boolean[].class, "bool[]");
		classTypes.put(char[].class, "char[]");
		classTypes.put(Byte[].class, "Byte[]");
		classTypes.put(Short[].class, "Short[]");
		classTypes.put(Integer[].class, "Int[]");
		classTypes.put(Long[].class, "Long[]");
		classTypes.put(Boolean[].class, "Bool[]");
		classTypes.put(Character[].class, "Char[]");
		classTypes.put(String[].class, "str[]");

		classTypes.forEach((k, v) -> {
			typeClasses.put(v, k);
		});
	}

	public static boolean isTypeSupported(String type) {
		return typeConverters.containsKey(type);
	}

	public static boolean isClassSupported(Class<?> clazz) {
		return classTypes.containsKey(clazz);
	}

	public static Object parse(Class<?> clazz, String s) {
		String type = classTypes.get(clazz);
		if (type == null) {
			throw new StartupException("unsupported parse class " + clazz.getCanonicalName());
		}
		IConverter<?> converter = typeConverters.get(type);
		try {
			return converter.parse(s);
		} catch (NumberFormatException e) {
			throw new StartupException(String.format("parse error value=%s with class=%s", s, clazz.getCanonicalName()),
					e);
		}
	}

}
