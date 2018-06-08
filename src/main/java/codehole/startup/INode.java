package codehole.startup;

public interface INode {

	NodeKind kind();

	Object call(Object target);

	Object call(Class<?> target);
}