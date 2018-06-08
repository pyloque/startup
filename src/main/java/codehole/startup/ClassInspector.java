package codehole.startup;

public class ClassInspector implements Inspector<Class<?>> {

	private Class<?> target;
	private IConsole console;

	public ClassInspector(Class<?> target, IConsole console) {
		this.target = target;
		this.console = console;
	}

	@Override
	public void inspect(ArgumentStack args) {
		if (args.isEmpty()) {
			if (Reflector.containsEmptyConstructor(target)) {
				console.log(Reflector.newEmpty(target));
				return;
			}
			console.log(target);
			return;
		}
		INode node = args.next();
		Object o = node.call(target);
		if (o == null || o instanceof Meta) {
			console.log(o);
			return;
		}
		new HybridInspector(o, console).inspect(args);
	}

}
