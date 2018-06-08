package codehole.startup;

import org.junit.Assert;
import org.junit.Test;

public class StartupObjectTest {

	static class A {
		private int a;

		public int getA() {
			return a;
		}

		public A setA(int a) {
			this.a = a;
			return this;
		}
	}

	static class B extends A {
		private int b;

		public int getB() {
			return b;
		}

		public B setB(int b) {
			this.b = b;
			return this;
		}
	}

	@Test
	public void fire() {
		Sure sure = new Sure();
		sure.reset();
		Startup.fire(new B(), "!", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertTrue(meta.isAll());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "! *", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertTrue(meta.isAll());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "! * 10", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), 10);
			Assert.assertTrue(meta.isAll());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "! $", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertTrue(meta.isAllField());
			Assert.assertTrue(meta.isFieldOnly());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "! @", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertTrue(meta.isAllMethod());
			Assert.assertTrue(meta.isMethodOnly());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "! $*", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertFalse(meta.isAllMethod());
			Assert.assertTrue(meta.isFieldOnly());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "! @get*", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertFalse(meta.isAllMethod());
			Assert.assertTrue(meta.isMethodOnly());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "! @set*", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertFalse(meta.isAllMethod());
			Assert.assertTrue(meta.isMethodOnly());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "$a", (o) -> {
			Assert.assertEquals(o, 0);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "$b", (o) -> {
			Assert.assertEquals(o, 0);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "@setA int:3 - $a", (o) -> {
			Assert.assertEquals(o, 3);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "@setB int:4 - $b", (o) -> {
			Assert.assertEquals(o, 4);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "@setA int:3 - @getA", (o) -> {
			Assert.assertEquals(o, 3);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new B(), "@setB int:4 - @getB", (o) -> {
			Assert.assertEquals(o, 4);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
	}
}
