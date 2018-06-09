package codehole.startup;

import java.lang.reflect.Field;

public class FieldNode implements INode {
	String name;
	boolean meta; // is static

	public FieldNode(String name, boolean meta) {
		this.name = name;
		this.meta = meta;
	}

	@Override
	public NodeKind kind() {
		return NodeKind.Field;
	}

	@Override
	public Object call(Object target) {
		if (meta) {
			return call(target.getClass());
		}
		
		Field field = Reflector.getInstanceField(target.getClass(), name);
		if (field == null) {
			throw new StartupException(
					String.format("instance field %s not found for class %s", name, target.getClass().getName()));
		}
		try {
			return field.get(target);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new StartupException(String.format("field %s access error for target %s", name, target), e);
		}
	}

	@Override
	public Object call(Class<?> target) {
		if (!meta) {
			if (Reflector.containsEmptyConstructor(target)) {
				Object o = Reflector.newEmpty(target);
				return call(o);
			}
			throw new StartupException(
					String.format("appropriate constructor not found for class %s", target.getCanonicalName()));
		}
		Field field = Reflector.getStaticField(target, name);
		if (field == null) {
			throw new StartupException(
					String.format("static field %s not found for class %s", name, target.getCanonicalName()));
		}
		try {
			return field.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new StartupException(
					String.format("field %s access error for class %s", name, target.getCanonicalName()), e);
		}
	}
}