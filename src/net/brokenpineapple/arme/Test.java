package net.brokenpineapple.arme;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CPU cpu = new CPU(new int[] {
				0b11100101, 0b10011111, 0b00010000, 0b00010100 // LDR	R1, =0
		});
		cpu.process();
	}

}
