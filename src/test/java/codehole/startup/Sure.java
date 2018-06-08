package codehole.startup;

class Sure {
	private boolean ok;

	boolean ok() {
		return ok;
	}

	void settle() {
		ok = true;
	}

	void reset() {
		ok = false;
	}
}