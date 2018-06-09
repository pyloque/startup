package codehole.startup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Meta {

	private Class<?> target;
	private String pattern;
	private int len;

	public Meta(Class<?> target, String pattern, int len) {
		this.target = target;
		this.pattern = pattern;
		this.len = len;
	}

	public String pattern() {
		return pattern;
	}

	public Class<?> target() {
		return target;
	}

	public int len() {
		return len;
	}

	public boolean isMethodOnly() {
		return pattern.startsWith("@");
	}

	public boolean isFieldOnly() {
		return pattern.startsWith("$");
	}

	public boolean isAllMethod() {
		return pattern.equals("@");
	}

	public boolean isAllField() {
		return pattern.equals("$");
	}

	public boolean isAll() {
		return pattern.equals("*");
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Class<?> current = target;
		String pattern = this.pattern;
		if (pattern.startsWith("@")) {
			pattern = pattern.substring(1);
		} else if (pattern.startsWith("$")) {
			pattern = pattern.substring(1);
		}
		pattern = pattern.replace("*", "\\w*");
		LOOP: while ((current != Object.class || target == Object.class) && len > 0) {
			sb.append("class: ");
			sb.append(current.getCanonicalName());
			sb.append("\n");
			if (!isMethodOnly()) {
				for (Field f : current.getDeclaredFields()) {
					if (!isAllField() && !f.getName().matches(pattern)) {
						continue;
					}
					sb.append("\tfield: ");
					if (Modifier.isStatic(f.getModifiers())) {
						sb.append("static ");
					}
					String type = f.getType().getCanonicalName();
					if (type.startsWith("java.lang") || type.startsWith("java.util")) {
						sb.append(f.getType().getSimpleName());
					} else {
						sb.append(type);
					}
					sb.append(" ");
					sb.append(f.getName());
					sb.append("\n");
					if (--len <= 0) {
						break LOOP;
					}
				}
			}
			if (!isFieldOnly()) {
				for (Method m : current.getDeclaredMethods()) {
					if (!isAllMethod() && !m.getName().matches(pattern)) {
						continue;
					}
					sb.append("\tmethod: ");
					if (Modifier.isStatic(m.getModifiers())) {
						sb.append("static ");
					}
					sb.append(m.getName());
					sb.append("(");
					boolean first = true;
					for (java.lang.reflect.Parameter p : m.getParameters()) {
						if (!first) {
							sb.append(", ");
						} else {
							first = false;
						}
						String type = p.getType().getCanonicalName();
						if (type.startsWith("java.lang") || type.startsWith("java.util")) {
							sb.append(p.getType().getSimpleName());
						} else {
							sb.append(type);
						}
						sb.append(" ");
						sb.append(p.getName());
					}
					sb.append(")\n");
					if (--len <= 0) {
						break LOOP;
					}
				}
			}
			current = current.getSuperclass();
		}
		return sb.toString().trim();
	}

}
