package codehole.startup;

public class HybridInspector implements Inspector<Object> {

	private Object target;
	private IConsole console;

	public HybridInspector(Object target, IConsole console) {
		this.target = target;
		this.console = console;
	}

	@Override
	public void inspect(ArgumentStack args) {
		if (target instanceof Class<?>) {
			new ClassInspector((Class<?>) target, console).inspect(args);
		} else {
			new ObjectInspector(target, console).inspect(args);
		}
	}

}
