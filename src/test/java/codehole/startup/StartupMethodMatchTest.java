package codehole.startup;

import org.junit.Assert;
import org.junit.Test;

public class StartupMethodMatchTest {

	static class A {

		int set() {
			return 1;
		}

		int set(int a) {
			return 2;
		}

		int set(int a, int b) {
			return 3;
		}

		int set(int[] a, int[] b) {
			return 4;
		}

		int set(Integer[] a, Integer[] b) {
			return 5;
		}

		int set(int[] a, Integer[] b) {
			return 6;
		}

	}

	@Test
	public void fire() {
		Sure sure = new Sure();
		sure.reset();
		Startup.fire(new A(), "@set", (o) -> {
			Assert.assertEquals(1, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new A(), "@set 1024", (o) -> {
			Assert.assertEquals(2, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new A(), "@set int:1 int:2", (o) -> {
			Assert.assertEquals(3, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new A(), "@set int[]:1,2 int[]:3,4,5", (o) -> {
			Assert.assertEquals(4, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new A(), "@set Int[]:1,2 Int[]:3,4,5", (o) -> {
			Assert.assertEquals(5, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		Startup.fire(new A(), "@set int[]:1,2 Int[]:3,4,5", (o) -> {
			Assert.assertEquals(6, o);
			sure.settle();
		});
		Assert.assertTrue(sure.ok());
		sure.reset();
		try {
			Startup.fire(new A(), "@set 1 2 3", (o) -> {
			});
		} catch (StartupException e) {
			sure.settle();
		}
		Assert.assertTrue(sure.ok());
		sure.reset();
	}

}
