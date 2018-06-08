package codehole.startup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArgumentStack {

	private LinkedList<INode> nodes = new LinkedList<>();

	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	public INode next() {
		return nodes.removeFirst();
	}

	public static ArgumentStack parse(String[] args) {
		ArgumentStack stack = new ArgumentStack();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("@@") || arg.startsWith("@")) {
				boolean meta = arg.startsWith("@@");
				String method = null;
				if (meta) {
					method = arg.substring(2);
				} else {
					method = arg.substring(1);
				}
				method = method.trim();
				if (method.isEmpty()) {
					throw new StartupException("empty method name disallowed");
				}
				List<Parameter> params = new ArrayList<>();
				for (i++; i < args.length + 1; i++) {
					if (i >= args.length || args[i].equals("-")) {
						break;
					}
					params.add(parseParameter(args[i]));
				}
				stack.nodes.add(new MethodNode(params, method, meta));
			} else if (arg.startsWith("$$")) {
				String field = arg.substring(2);
				stack.nodes.add(new FieldNode(field, true));
			} else if (arg.startsWith("$")) {
				String field = arg.substring(1);
				stack.nodes.add(new FieldNode(field, false));
			} else if (arg.equals("!")) {
				MetaNode meta = new MetaNode();
				stack.nodes.add(meta);
				if (i < args.length - 3) {
					throw new StartupException("too many args provided");
				}
				i++;
				if (i < args.length) {
					meta.pattern(args[i]);
				}
				i++;
				if (i < args.length) {
					try {
						meta.len(Integer.parseInt(args[i]));
					} catch (NumberFormatException e) {
						throw new StartupException("meta len arg must be integer");
					}
				}
				break;
			} else if (arg.equals("+")) {
				List<Parameter> params = new ArrayList<>();
				for (i++; i < args.length + 1; i++) {
					if (i >= args.length || args[i].equals("-")) {
						break;
					}
					params.add(parseParameter(args[i]));
				}
				stack.nodes.add(new ConstructorNode(params));
			} else {
				throw new StartupException("illegal argument=" + arg);
			}
		}
		return stack;
	}

	public static Parameter parseParameter(String s) {
		String[] parts = s.split(":");
		if (parts.length == 2) {
			String type = parts[0].trim();
			String value = parts[1].trim();
			if (Parameter.isTypeSupported(type)) {
				return new Parameter(type, value);
			} else {
				throw new StartupException("unsupported parameter type=" + type);
			}
		}
		return new Parameter(null, s.trim());
	}

}
