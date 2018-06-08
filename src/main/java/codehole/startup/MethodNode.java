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
			return call(target.getClass());
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
			return method.invoke(target, values);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new StartupException(
					String.format("method invoke error name=%s class=%s", name, target.getClass().getCanonicalName()),
					e);
		}
	}

	@Override
	public Object call(Class<?> target) {
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
			return method.invoke(null, values);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new StartupException(
					String.format("method invokr error name=%s class=%s", name, target.getClass().getCanonicalName()));
		}
	}
}