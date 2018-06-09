package codehole.startup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class MethodNode implements INode {
	String name;
	List<Parameter> params;

	public MethodNode(List<Parameter> params, String name) {
		this.params = params;
		this.name = name;
	}

	@Override
	public NodeKind kind() {
		return NodeKind.Method;
	}

	@Override
	public Object call(Object target) {
		Method method = Reflector.selectMethod(target.getClass(), name, params);
		if (method == null) {
			throw new StartupException(String.format("appropriate method %s not found for class %s", name,
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
		Method method = Reflector.selectMethod(target, name, params);
		if (method == null) {
			throw new StartupException(String.format("appropriate method not found for name %s class %s", name,
					target.getClass().getCanonicalName()));
		}
		if (!Modifier.isStatic(method.getModifiers())) {
			// 在类上调用实例方法，需要先使用默认构造期来实例化
			if (Reflector.containsEmptyConstructor(target)) {
				return call(Reflector.newEmpty(target));
			}
			throw new StartupException(
					String.format("invoke instance method %s on class %s without default constructor", name,
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
				return target;
			}
			return result;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new StartupException(String.format("static method invoke error name=%s class=%s", name,
					target.getClass().getCanonicalName()));
		}
	}

}