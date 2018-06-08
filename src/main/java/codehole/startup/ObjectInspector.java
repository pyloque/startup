package codehole.startup;

public class ObjectInspector implements Inspector<Object> {

	private Object target;
	private IConsole console;

	public ObjectInspector(Object target, IConsole console) {
		this.target = target;
		this.console = console;
	}

	@Override
	public void inspect(ArgumentStack args) {
		if (args.isEmpty()) {
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
