package codehole.startup;

import org.junit.Assert;
import org.junit.Test;

public class StartupConstructorTest {

	static class A {

		private int a;

		A() {
			a = 1;
		}

		A(int v) {
			a = 2;
		}

		A(int v, int w) {
			a = 3;
		}

		A(int[] v, int[] w) {
			a = 4;
		}

		A(Integer[] v, int[] w) {
			a = 5;
		}

		A(Integer[] v, Integer[] w) {
			a = 6;
		}

		A(int[] v, Integer[] w) {
			a = 7;
		}
		
		A(int v, Integer w) {
			a = 8;
		}
		
		int a() {
			return a;
		}

	}

	@Test
	public void fire() {
		Sure sure = new Sure();
		sure.reset();
		Startup.fire(A.class, "+ - $a", (o) -> {
			Assert.assertEquals(1, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "+ 1 - $a", (o) -> {
			Assert.assertEquals(2, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "+ int:1024 int:2048 - $a", (o) -> {
			Assert.assertEquals(3, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "+ int[]:1024 int[]:2048 - $a", (o) -> {
			Assert.assertEquals(4, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "+ Int[]:1024 int[]:2048 - $a", (o) -> {
			Assert.assertEquals(5, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "+ Int[]:1024 Int[]:2048 - $a", (o) -> {
			Assert.assertEquals(6, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "+ int[]:1024 Int[]:2048 - $a", (o) -> {
			Assert.assertEquals(7, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(A.class, "+ int:1024 Int:2048 - $a", (o) -> {
			Assert.assertEquals(8, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
	}

}
