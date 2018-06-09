package codehole.startup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class Reflector {

	public static boolean containsEmptyConstructor(Class<?> target) {
		try {
			target.getDeclaredConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
		return true;
	}

	public static Object newEmpty(Class<?> target) {
		try {
			Constructor<?> cons = target.getDeclaredConstructor();
			cons.setAccessible(true);
			return cons.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new StartupException("new empty instance error", e);
		}
	}

	static interface IAccepted<T> {
		boolean accept(T t);
	}

	public static Field getField(Class<?> target, String name, IAccepted<Field> acceptor) {
		Field field = null;
		do {
			try {
				field = target.getDeclaredField(name);
				if (acceptor.accept(field)) {
					field.setAccessible(true);
					return field;
				}
			} catch (NoSuchFieldException e1) {
			}
			target = target.getSuperclass();
		} while (target != null);
		return null;
	}

	public static Field getInstanceField(Class<?> target, String name) {
		return getField(target, name, (f) -> {
			return !Modifier.isStatic(f.getModifiers());
		});
	}

	public static Field getStaticField(Class<?> target, String name) {
		return getField(target, name, (f) -> {
			return Modifier.isStatic(f.getModifiers());
		});
	}

	public static Constructor<?> selectConstructor(Class<?> target, List<Parameter> params) {
		for (Constructor<?> cons : target.getDeclaredConstructors()) {
			if (cons.getParameterCount() != params.size()) {
				continue;
			}
			Class<?>[] sourceTypes = cons.getParameterTypes();
			boolean match = true;
			for (int i = 0; i < params.size(); i++) {
				Class<?> sourceType = sourceTypes[i];
				if (!Parameter.isClassSupported(sourceType)) {
					match = false;
					break;
				}
				Class<?> targetType = params.get(i).clazz();
				if (sourceType != targetType && targetType != null) {
					match = false;
					break;
				}
			}
			if (match) {
				cons.setAccessible(true);
				return cons;
			}
		}
		return null;
	}

	public static void fillParameterType(Constructor<?> cons, List<Parameter> params) {
		Class<?>[] sourceTypes = cons.getParameterTypes();
		for (int i = 0; i < params.size(); i++) {
			Class<?> sourceType = sourceTypes[i];
			params.get(i).type(sourceType);
		}
	}

	public static Method selectMethod(Class<?> target, String name, List<Parameter> params,
			IAccepted<Method> acceptor) {
		do {
			for (Method method : target.getDeclaredMethods()) {
				if (!method.getName().equals(name)) {
					continue;
				}
				if (method.getParameterCount() != params.size()) {
					continue;
				}
				if (!acceptor.accept(method)) {
					continue;
				}
				Class<?>[] sourceTypes = method.getParameterTypes();
				boolean match = true;
				for (int i = 0; i < params.size(); i++) {
					Class<?> sourceType = sourceTypes[i];
					if (!Parameter.isClassSupported(sourceType)) {
						match = false;
						break;
					}
					Class<?> targetType = params.get(i).clazz();
					if (sourceType != targetType && targetType != null) {
						match = false;
						break;
					}
				}
				if (match) {
					method.setAccessible(true);
					return method;
				}
			}
			target = target.getSuperclass();
		} while (target != null);
		return null;
	}

	public static Method selectStaticMethod(Class<?> target, String name, List<Parameter> params) {
		return selectMethod(target, name, params, (m) -> {
			return Modifier.isStatic(m.getModifiers());
		});
	}

	public static Method selectInstanceMethod(Class<?> target, String name, List<Parameter> params) {
		return selectMethod(target, name, params, (m) -> {
			return !Modifier.isStatic(m.getModifiers());
		});
	}

	public static void fillParameterType(Method method, List<Parameter> params) {
		Class<?>[] sourceTypes = method.getParameterTypes();
		for (int i = 0; i < params.size(); i++) {
			Class<?> sourceType = sourceTypes[i];
			params.get(i).type(sourceType);
		}
	}

}
