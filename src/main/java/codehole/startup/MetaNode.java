package codehole.startup;

public class MetaNode implements INode {

	private String pattern = "*";
	private int len = Integer.MAX_VALUE;

	public void len(int len) {
		this.len = len;
	}

	public void pattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public NodeKind kind() {
		return NodeKind.Meta;
	}

	@Override
	public Object call(Object target) {
		return call(target.getClass());
	}

	@Override
	public Object call(Class<?> target) {
		return new Meta(target, pattern, len);
	}

}
