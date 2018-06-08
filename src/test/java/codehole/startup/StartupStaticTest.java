package codehole.startup;

import org.junit.Assert;
import org.junit.Test;

public class StartupStaticTest {

	static class A {
		public static int a;
		private static A inst = new A();

		public static A setA(int v) {
			a = v;
			return inst;
		}
	}

	static class B extends A {
		public static int b;
		private static B inst = new B();

		public static B setB(int v) {
			b = v;
			return inst;
		}
	}

	@Test
	public void fire() {
		Sure sure = new Sure();
		sure.reset();
		Startup.fire(B.class, "@@setB 5 - $$b", (o) -> {
			Assert.assertEquals(5, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "@@setA 3 - $$a", (o) -> {
			Assert.assertEquals(3, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "@@setA 2 - $$a", (o) -> {
			Assert.assertEquals(2, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "@@setB 1 - $$b", (o) -> {
			Assert.assertEquals(1, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "@@setA 4 - $$a", (o) -> {
			Assert.assertEquals(4, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
	}

}
