/*******************************************************************************
 * Copyright (c) 2012 Emiel Tasseel and James Lee King.
 * All rights reserved. This file is part of ARME.
 * 
 * ARME is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ARME is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with ARME.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.brokenpineapple.arme;

public class CPU {

    ////////////////* Constants *////////////////
    
    /* Modes */
    public final static int MODE_USER = 0b10000;
    public final static int MODE_FIQ  = 0b10001;
    public final static int MODE_IRQ  = 0b10010;
    public final static int MODE_SVC  = 0b10011;
    public final static int MODE_ABT  = 0b10111;
    public final static int MODE_UND  = 0b11011;
    public final static int MODE_SYS  = 0b11111;

    /* Registers */
    public final static int REG_R0    = 0x00;
    public final static int REG_R1    = 0x01;
    public final static int REG_R2    = 0x02;
    public final static int REG_R3    = 0x03;
    public final static int REG_R4    = 0x04;
    public final static int REG_R5    = 0x05;
    public final static int REG_R6    = 0x06;
    public final static int REG_R7    = 0x07;
    public final static int REG_R8    = 0x08;
    public final static int REG_R9    = 0x09;
    public final static int REG_R10   = 0x0A;
    public final static int REG_R11   = 0x0B;
    public final static int REG_R12   = 0x0C;
    public final static int REG_R13   = 0x0D;
    public final static int REG_R14   = 0x0E;
    public final static int REG_PC    = 0x0F;
    public final static int REG_CPSR  = 0x10;
    public final static int REG_SPSR  = 0x11;
    
    /* Conditions */
    public final static int COND_EQ   = 0b0000;
    public final static int COND_NE   = 0b0001;
    public final static int COND_CS   = 0b0010;
    public final static int COND_HS   = 0b0010;
    public final static int COND_CC   = 0b0011;
    public final static int COND_LO   = 0b0011;
    public final static int COND_MI   = 0b0100;
    public final static int COND_PL   = 0b0101;
    public final static int COND_VS   = 0b0110;
    public final static int COND_VC   = 0b0111;
    public final static int COND_HI   = 0b1000;
    public final static int COND_LS   = 0b1001;
    public final static int COND_GE   = 0b1010;
    public final static int COND_LT   = 0b1011;
    public final static int COND_GT   = 0b1100;
    public final static int COND_LE   = 0b1101;
    public final static int COND_AL   = 0b1110;
    public final static int COND_UC   = 0b1111;             // Unconditional
    
    /////////////////////////////////////////////
    
    public  int[] memory;                                   // RAM

    private int pc;                                         // program counter
    private int cpsr;                                       // current program status register
    private int r0, r1, r2, r3, r4, r5, r6, r7,
                r8, r9, r10, r11, r12, r13, r14;

    private int spsr_svc;                                   // supervisor mode
    private int r13_svc, r14_svc; 

    private int spsr_abt;                                   // abort mode
    private int r13_abt, r14_abt; 

    private int spsr_und;                                   // undefined mode
    private int r13_und, r14_und; 

    private int spsr_irq;                                   // interrupt mode
    private int r13_irq, r14_irq;

    private int spsr_fiq;                                   // fast interrupt mode
    private int r8_fiq, r9_fiq, r10_fiq, 
                r11_fiq, r12_fiq, r13_fiq, r14_fiq;
    
    public CPU(int[] memory) {
        this.memory = memory;
    }
    
    public void process() {
        int cond = (memory[pc] & 0b11110000) >> 4;
        
        if(cond == COND_UC) {                               // unconditional
            
        } else {
            boolean flag = false;
            
            switch(cond) {              
                case COND_EQ:               flag =  this.getZ();                                        break;
                case COND_NE:               flag = !this.getZ();                                        break;
                case COND_CS & COND_HS:     flag =  this.getC();                                        break;
                case COND_CC & COND_LO:     flag = !this.getC();                                        break;
                case COND_MI:               flag =  this.getN();                                        break;
                case COND_PL:               flag = !this.getN();                                        break;
                case COND_VS:               flag =  this.getV();                                        break;
                case COND_VC:               flag = !this.getV();                                        break;
                case COND_HI:               flag =  this.getC() && !this.getZ();                        break;
                case COND_LS:               flag = !this.getC() ||  this.getZ();                        break;
                case COND_GE:               flag =  this.getN() ==  this.getV();                        break;
                case COND_LT:               flag =  this.getN() !=  this.getV();                        break;
                case COND_GT:               flag =  this.getN() ==  this.getV() && !this.getZ();        break;
                case COND_LE:               flag =  this.getN() !=  this.getV() &&  this.getZ();        break;
                case COND_AL:               flag =  true;                                               break;
            }
            
            if(flag) {
                System.out.println("Condition validated");
            }
        }
        
        pc += 4;
    }

    public void setRegister(int r, int v) {
        switch(r) {
            case REG_R0:  r0 = v;                                                           return;
            case REG_R1:  r1 = v;                                                           return;
            case REG_R2:  r2 = v;                                                           return;
            case REG_R3:  r3 = v;                                                           return;
            case REG_R4:  r4 = v;                                                           return;
            case REG_R5:  r5 = v;                                                           return;
            case REG_R6:  r6 = v;                                                           return;
            case REG_R7:  r7 = v;                                                           return;
    
            case REG_R8:  if ((cpsr & 0b11111) == MODE_FIQ) r8_fiq  = v; else r9  = v;      return;  
            case REG_R9:  if ((cpsr & 0b11111) == MODE_FIQ) r9_fiq  = v; else r10 = v;      return;
            case REG_R10: if ((cpsr & 0b11111) == MODE_FIQ) r10_fiq = v; else r11 = v;      return;
            case REG_R11: if ((cpsr & 0b11111) == MODE_FIQ) r11_fiq = v; else r12 = v;      return;
            case REG_R12: if ((cpsr & 0b11111) == MODE_FIQ) r12_fiq = v; else r13 = v;      return;
            
            case REG_R13: switch(cpsr & 0b11111) {
                case MODE_SVC: r13_svc = v;                                                 return;
                case MODE_ABT: r13_abt = v;                                                 return;
                case MODE_UND: r13_und = v;                                                 return;
                case MODE_IRQ: r13_irq = v;                                                 return;
                case MODE_FIQ: r13_fiq = v;                                                 return;
                default:    r13  = v;                                                       return;
            }
            
            case REG_R14: switch(cpsr & 0b11111) {
                case MODE_SVC: r14_svc = v;                                                 return;
                case MODE_ABT: r14_abt = v;                                                 return;
                case MODE_UND: r14_und = v;                                                 return;
                case MODE_IRQ: r14_irq = v;                                                 return;
                case MODE_FIQ: r14_fiq = v;                                                 return;
                default:    r14  = v;                                                       return;
            }
            
            case REG_PC:pc= v;                                                              return;
            case REG_CPSR: cpsr = v;                                                        return;
    
            case REG_SPSR: switch(cpsr & 0b11111) {
                case MODE_SVC: spsr_svc = v;                                                return;
                case MODE_ABT: spsr_abt = v;                                                return;
                case MODE_UND: spsr_und = v;                                                return;
                case MODE_IRQ: spsr_irq = v;                                                return;
                case MODE_FIQ: spsr_fiq = v;                                                return;
            }
        }
    }
    
    public int getRegister(int r) {
        switch(r) {
            case REG_R0:        return r0;
            case REG_R1:        return r1;
            case REG_R2:        return r2;
            case REG_R3:        return r3;
            case REG_R4:        return r4;
            case REG_R5:        return r5;
            case REG_R6:        return r6;
            case REG_R7:        return r7;

            case REG_R8:        return ((cpsr & 0b11111) == MODE_FIQ) ? r8_fiq  : r8;
            case REG_R9:        return ((cpsr & 0b11111) == MODE_FIQ) ? r9_fiq  : r9;
            case REG_R10:       return ((cpsr & 0b11111) == MODE_FIQ) ? r10_fiq : r10;
            case REG_R11:       return ((cpsr & 0b11111) == MODE_FIQ) ? r11_fiq : r11;
            case REG_R12:       return ((cpsr & 0b11111) == MODE_FIQ) ? r12_fiq : r12;

            case REG_R13: switch(cpsr & 0b11111) {
                case MODE_SVC:  return r13_svc;
                case MODE_ABT:  return r13_abt;
                case MODE_UND:  return r13_und;
                case MODE_IRQ:  return r13_irq;
                case MODE_FIQ:  return r13_fiq;
                default:        return r13;
            }
            
            case REG_R14: switch(cpsr & 0b11111) {
                case MODE_SVC:  return r14_svc;
                case MODE_ABT:  return r14_abt;
                case MODE_UND:  return r14_und;
                case MODE_IRQ:  return r14_irq;
                case MODE_FIQ:  return r14_fiq;
                default:        return r14;
            }
            
            case REG_PC:        return pc;
            case REG_CPSR:      return cpsr;

            case REG_SPSR: switch(cpsr & 0b11111) {
                case MODE_SVC:  return spsr_svc;
                case MODE_ABT:  return spsr_abt;
                case MODE_UND:  return spsr_und;
                case MODE_IRQ:  return spsr_irq;
                case MODE_FIQ:  return spsr_fiq;
            }
        }
        
        return 0;
    }

    public boolean getN() {
        return (cpsr >> 31 & 1) == 1;
    }
    
    public boolean getZ() {
        return (cpsr >> 30 & 1) == 1;
    }
    
    public boolean getC() {
        return (cpsr >> 29 & 1) == 1;
    }

    public boolean getV() {
        return (cpsr >> 28 & 1) == 1;
    }
    
    public boolean getQ() {
        return (cpsr >> 27 & 1) == 1;
    }

    public boolean getJ() {
        return (cpsr >> 24 & 1) == 1;
    }
    
    public int getGE() {
        return  cpsr >> 16 & 0b1111;
    }
    
    public boolean getE() {
        return (cpsr >> 9  & 1) == 1;
    }
    
    public boolean getA() {
        return (cpsr >> 8  & 1) == 1;
    }
    
    public boolean getI() {
        return (cpsr >> 7  & 1) == 1;
    }
    
    public boolean getF() {
        return (cpsr >> 6  & 1) == 1;
    }
    
    public boolean getT() {
        return (cpsr >> 5  & 1) == 1;
    }
    
    public int getM() {
        return  cpsr >> 0 & 0b11111;
    }

    public void setN(boolean b) {
        this.cpsr = b ? cpsr | (1 << 31) : cpsr & ~(1 << 31);
    }

    public void setZ(boolean b) {
        this.cpsr = b ? cpsr | (1 << 30) : cpsr & ~(1 << 30);
    }

    public void setC(boolean b) {
        this.cpsr = b ? cpsr | (1 << 29) : cpsr & ~(1 << 29);
    }

    public void setV(boolean b) {
        this.cpsr = b ? cpsr | (1 << 28) : cpsr & ~(1 << 28);
    }

    public void setQ(boolean b) {
        this.cpsr = b ? cpsr | (1 << 27) : cpsr & ~(1 << 27);
    }

    public void setJ(boolean b) {
        this.cpsr = b ? cpsr | (1 << 24) : cpsr & ~(1 << 24);
    }

    public void setGE(int v) {
        this.cpsr = (v >> 0 & 1) == 1 ? cpsr | (1 << 16) : cpsr & ~(1 << 16);
        this.cpsr = (v >> 1 & 1) == 1 ? cpsr | (1 << 17) : cpsr & ~(1 << 17);
        this.cpsr = (v >> 2 & 1) == 1 ? cpsr | (1 << 18) : cpsr & ~(1 << 18);
        this.cpsr = (v >> 3 & 1) == 1 ? cpsr | (1 << 19) : cpsr & ~(1 << 19);
    }

    public void setE(boolean b) {
        this.cpsr = b ? cpsr | (1 << 9) : cpsr & ~(1 << 9);
    }

    public void setA(boolean b) {
        this.cpsr = b ? cpsr | (1 << 8) : cpsr & ~(1 << 8);
    }

    public void setI(boolean b) {
        this.cpsr = b ? cpsr | (1 << 7) : cpsr & ~(1 << 7);
    }

    public void setF(boolean b) {
        this.cpsr = b ? cpsr | (1 << 6) : cpsr & ~(1 << 6);
    }

    public void setT(boolean b) {
        this.cpsr = b ? cpsr | (1 << 5) : cpsr & ~(1 << 5);
    }

    public void setM(int v) {
        this.cpsr = (v >> 0 & 1) == 1 ? cpsr | (1 << 0) : cpsr & ~(1 << 0);
        this.cpsr = (v >> 1 & 1) == 1 ? cpsr | (1 << 1) : cpsr & ~(1 << 1);
        this.cpsr = (v >> 2 & 1) == 1 ? cpsr | (1 << 2) : cpsr & ~(1 << 2);
        this.cpsr = (v >> 3 & 1) == 1 ? cpsr | (1 << 3) : cpsr & ~(1 << 3);
        this.cpsr = (v >> 4 & 1) == 1 ? cpsr | (1 << 4) : cpsr & ~(1 << 4);
    }

}
