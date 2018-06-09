package codehole.startup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Startup {

	public static void fire(Object target, String args) {
		fire(target, args.split("\\s+"));
	}

	public static void fire(Object target, String args, IConsole console) {
		fire(target, args.split("\\s+"), console);
	}

	public static void fire(Object target, String[] args, IConsole console) {
		ArgumentStack stack = ArgumentStack.parse(args);
		new HybridInspector(target, console).inspect(stack);
	}

	public static void fire(Object target, String[] args) {
		fire(target, args, (o) -> {
			System.out.println(o);
			System.out.flush();
		});
	}

	public static void shell(Object target) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("> ");
			System.out.flush();
			String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line.isEmpty()) {
				continue;
			}
			if (line.equals("q") || line.equals("exit")) {
				break;
			}
			try {
				Startup.fire(target, line.trim().split(" "));
			} catch (StartupException e) {
				e.printStackTrace();
			}
		}
	}

}
