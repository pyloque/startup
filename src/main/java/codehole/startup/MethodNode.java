package codehole.startup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MethodNode implements INode {
	String name;
	boolean meta; // is static
	List<Parameter> params;

	public MethodNode(List<Parameter> params, String name, boolean meta) {
		this.params = params;
		this.name = name;
		this.meta = meta;
	}

	@Override
	public NodeKind kind() {
		return NodeKind.Method;
	}

	@Override
	public Object call(Object target) {
		if (meta) {
			return call(target.getClass(), target);
		}
		Method method = Reflector.selectInstanceMethod(target.getClass(), name, params);
		if (method == null) {
			throw new StartupException(String.format("appropriate instance method %s not found for class %s", name,
					target.getClass().getCanonicalName()));
		}
		Reflector.fillParameterType(method, params);
		int i = 0;
		Object[] values = new Object[params.size()];
		for (Parameter param : params) {
			values[i++] = param.value();
		}
		try {
			Object result = method.invoke(target, values);
			if (method.getReturnType() == void.class) {
				// 如果函数返回值为void，那么就返回对象自身
				// 为了解决静态方法无法返回class.this的问题
				return target;
			}
			return result;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new StartupException(
					String.format("method invoke error name=%s class=%s", name, target.getClass().getCanonicalName()),
					e);
		}
	}

	@Override
	public Object call(Class<?> target) {
		return call(target, target);
	}

	private Object call(Class<?> target, Object voidRet) {
		if (!meta) {
			if (!Reflector.containsEmptyConstructor(target)) {
				throw new StartupException("empty constructor not found for class " + target.getCanonicalName());
			}
			Object o = Reflector.newEmpty(target);
			return call(o);
		}
		Method method = Reflector.selectStaticMethod(target, name, params);
		if (method == null) {
			throw new StartupException(String.format("appropriate static method not found for name %s class %s", name,
					target.getClass().getCanonicalName()));
		}
		Reflector.fillParameterType(method, params);
		int i = 0;
		Object[] values = new Object[params.size()];
		for (Parameter param : params) {
			values[i++] = param.value();
		}
		try {
			Object result = method.invoke(null, values);
			if (method.getReturnType() == void.class) {
				// 如果函数返回值为void，那么就返回对象自身
				// 为了解决静态方法无法返回class.this的问题
				return voidRet;
			}
			return result;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new StartupException(
					String.format("method invokr error name=%s class=%s", name, target.getClass().getCanonicalName()));
		}
	}
}