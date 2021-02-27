package xworker.lang.system;

public enum MessageLevel {
	PRIMARY("primary", (byte) 1), SUCCESS("success", (byte) 2), INFO("info", (byte) 3),
	WARNING("warning", (byte) 4), DANGER("danger", (byte) 5), URGENT("urgent", (byte) 6),
	DEFAULT("default", (byte) 3);
	
	private String name;
	private byte value;
	
	private MessageLevel(String name, byte value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
}
