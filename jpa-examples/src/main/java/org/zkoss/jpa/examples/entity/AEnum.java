package org.zkoss.jpa.examples.entity;

public enum AEnum {
	HIGH(0, "High"), MEDIUM(1, "Medium"), LOW(2, "Low");

	private int index;
	private String label;

	private AEnum(int index, String label) {
		this.index = index;
		this.label = label;
	}

	public int getIndex() {
		return index;
	}

	public String getLabel() {
		return label;
	}
}