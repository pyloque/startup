package codehole.startup;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldNode implements INode {
	String name;

	public FieldNode(String name) {
		this.name = name;
	}

	@Override
	public NodeKind kind() {
		return NodeKind.Field;
	}

	@Override
	public Object call(Object target) {
		Field field = Reflector.getField(target.getClass(), name);
		if (field == null) {
			throw new StartupException(
					String.format("field %s not found for class %s", name, target.getClass().getName()));
		}
		try {
			return field.get(target);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new StartupException(String.format("field %s access error for target %s", name, target), e);
		}
	}

	@Override
	public Object call(Class<?> target) {
		Field field = Reflector.getField(target, name);
		if (field == null) {
			throw new StartupException(
					String.format("field %s not found for class %s", name, target.getCanonicalName()));
		}
		if (!Modifier.isStatic(field.getModifiers())) {
			// 在类上调用实例方法，需要先使用默认构造期来实例化
			if (Reflector.containsEmptyConstructor(target)) {
				return call(Reflector.newEmpty(target));
			}
			throw new StartupException(String.format("access instance field %s on class %s without default constructor",
					name, target.getClass().getCanonicalName()));
		}
		try {
			return field.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new StartupException(
					String.format("field %s access error for class %s", name, target.getCanonicalName()), e);
		}
	}
}