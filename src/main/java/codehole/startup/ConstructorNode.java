package codehole.startup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ConstructorNode implements INode {
	List<Parameter> params;

	public ConstructorNode(List<Parameter> params) {
		this.params = params;
	}

	@Override
	public NodeKind kind() {
		return NodeKind.Constructor;
	}

	@Override
	public Object call(Object target) {
		throw new StartupException(String.format("constructor disallowd on object %s", target));
	}

	@Override
	public Object call(Class<?> target) {
		Constructor<?> cons = Reflector.selectConstructor(target, params);
		if (cons == null) {
			throw new StartupException(
					String.format("appropriate constructor not found for class %s", target.getCanonicalName()));
		}
		Reflector.fillParameterType(cons, params);
		Object[] values = new Object[params.size()];
		int i = 0;
		for (Parameter param : params) {
			values[i++] = param.value();
		}
		try {
			return cons.newInstance(values);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new StartupException(
					String.format("new instance from constructor error class=%s", target.getCanonicalName()));
		}
	}

}