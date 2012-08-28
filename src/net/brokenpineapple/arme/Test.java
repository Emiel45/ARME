package net.brokenpineapple.arme;

public class Test {
    
    private static int instructions = 0;
    
	public static void main(String[] args) {
		int[] memory = new int[128 * 1024]; // 128kb of RAM

        System.arraycopy(new int[] { CPU.COND_AL << 4 | 0b1010, 0x00, 0x00, 0xff }, 0, memory, 0x00000000, 4); // 0x00000000: B #FF
        System.arraycopy(new int[] { CPU.COND_AL << 4 | 0b1010, 0x00, 0x00, 0x00 }, 0, memory, 0x000000ff, 4); // 0x000000ff: B #00

        CPU cpu = new CPU(memory);
        
        /*new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(instructions + " instr/sec");
                    instructions = 0;
                }
            }
            
        }).start();*/
        
		while(true) {
		    cpu.process();
		    instructions++;
		}
	}

}
