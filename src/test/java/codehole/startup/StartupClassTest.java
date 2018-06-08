package codehole.startup;

import org.junit.Assert;
import org.junit.Test;

public class StartupClassTest {

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
		Startup.fire(B.class, "!", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertTrue(meta.isAll());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "! *", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), Integer.MAX_VALUE);
			Assert.assertTrue(meta.isAll());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "! * 10", (o) -> {
			Assert.assertSame(o.getClass(), Meta.class);
			Meta meta = (Meta) o;
			Assert.assertSame(meta.target(), B.class);
			Assert.assertEquals(meta.len(), 10);
			Assert.assertTrue(meta.isAll());
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "! $", (o) -> {
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
		Startup.fire(B.class, "! @", (o) -> {
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
		Startup.fire(B.class, "! $*", (o) -> {
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
		Startup.fire(B.class, "! @get*", (o) -> {
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
		Startup.fire(B.class, "! @set*", (o) -> {
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
		Startup.fire(B.class, "+", (o) -> {
			Assert.assertSame(o.getClass(), B.class);
			B b = (B) o;
			Assert.assertEquals(b.getB(), 0);
			Assert.assertEquals(b.getA(), 0);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "+ -", (o) -> {
			Assert.assertSame(o.getClass(), B.class);
			B b = (B) o;
			Assert.assertEquals(b.getB(), 0);
			Assert.assertEquals(b.getA(), 0);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "+ - @setB 4 - @getB", (o) -> {
			Assert.assertEquals(o, 4);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "+ - @setB int:4 - @getB", (o) -> {
			Assert.assertEquals(o, 4);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "+ - @setA 3 - @getA", (o) -> {
			Assert.assertEquals(o, 3);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "+ - @setA int:3 - @getA", (o) -> {
			Assert.assertEquals(o, 3);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "$a", (o) -> {
			Assert.assertEquals(o, 0);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "$b", (o) -> {
			Assert.assertEquals(o, 0);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "@setA int:3 - $a", (o) -> {
			Assert.assertEquals(o, 3);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "@setB int:4 - $b", (o) -> {
			Assert.assertEquals(o, 4);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "@setA int:3 - @getA", (o) -> {
			Assert.assertEquals(o, 3);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(B.class, "@setB int:4 - @getB", (o) -> {
			Assert.assertEquals(o, 4);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
	}

}
