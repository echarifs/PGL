/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.codegen;

import java.util.Stack;

//import static org.mockito.Mockito.ignoreStubs;

import fr.ensimag.deca.DecacCompiler;
//import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
//import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
//import fr.ensimag.ima.pseudocode.RegisterOffset;
//import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
//import fr.ensimag.ima.pseudocode.instructions.STORE;
//import fr.ensimag.ima.pseudocode.instructions.SUBSP;

/**
 *
 * @author gl01
 */
public class RegisterGestion {
    private int offset = 3;
    private int localOffset = -3;
    private int nbRegisters;
    private int cptRegister;
    private boolean[] isRegisterFree;
    private boolean[] isRegisterFreeMeth;
    private DecacCompiler compiler;
    private Stack<GPRegister> stack;
    private int ADDSP = 0;

    //private Register base = Register.GB;
    
    /**
    * Constructor of register handler, initialize the registers.
    *
    */
    public RegisterGestion(int nbRegisters, DecacCompiler compiler) {
        this.nbRegisters = nbRegisters;
        this.isRegisterFree = new boolean[nbRegisters];
        this.isRegisterFreeMeth = new boolean[nbRegisters];
        this.stack = new Stack<GPRegister>();
        for (int i = 0; i < nbRegisters ; i++)
        {
            this.isRegisterFree[i] = true;
            this.isRegisterFreeMeth[i] = true;
        }
        this.cptRegister = 0;
        this.compiler = compiler;
    }

    public int getOffset()
    {
        return this.offset;
    }

    public void incADDSP(int value)
    {
        this.ADDSP += value;
    }

    public int getADDSP()
    {
        return this.ADDSP;
    }

    public void incOffset(int value)
    {
        this.offset += value;
    }

    public int getLocalOffset()
    {
        return this.localOffset;
    }

    public void incLocalOffset(int value)
    {
        this.localOffset -= value;
    }

    public void resetLocalOffset()
    {
        this.localOffset = -3;
    }
    
    
    /**
    * Get the number of registers
    *
    * @return number of registers
    */
    public int getNbRegisters(){
        return nbRegisters;
    }
    
    /**
    * Check if we have free register to use
    *
    * @return true if we have, otherwise false
    */
    public boolean containsGPRegisterLibre(){
        int i;
        for (i = nbRegisters - 1; i >= 2; i--)
            if (isRegisterFree[i])
                return true;
        return false;
    }
    
    /**
    * Get the free register to use
    *
    * @return the free register to use
    */
    public GPRegister getGPRegisterLibre(){
        int i;
        if (this.compiler.getStatus())
        {
            for (i = 2; i < nbRegisters; i++)
                if (isRegisterFreeMeth[i])
                    break;
            if (i < nbRegisters)
            {
                this.isRegisterFreeMeth[i] = false;
                cptRegister = i;
                if (!this.stack.contains(Register.getR(i)))
                {
                    this.compiler.addIndex(new PUSH(Register.getR(i)), this.compiler.getfirstLineOfMethod() + this.stack.size());
                    //this.compiler.addInstruction(new PUSH(Register.getR(i)));
                    this.stack.push(Register.getR(i));
                }
                return Register.getR(i);
            }
            else
            {
                this.compiler.addInstruction(new PUSH(Register.getR(nbRegisters - 1)));
                this.stack.push(Register.getR(nbRegisters - 1));
                cptRegister = nbRegisters - 1;
                return Register.getR(nbRegisters - 1);
            }
        }
        else
        {
            for (i = 2; i < nbRegisters; i++)
                if (isRegisterFree[i])
                    break;
            if (i < nbRegisters)
            {
                this.isRegisterFree[i] = false;
                cptRegister = i;
                return Register.getR(i);
            }
            else
            {
                this.compiler.addInstruction(new PUSH(Register.getR(nbRegisters - 1)));
                cptRegister = nbRegisters - 1;
                return Register.getR(nbRegisters - 1);
            }
        }
    }

    public GPRegister getGPRegister()
    {
        return GPRegister.getR(this.cptRegister);
    }

    public void resetRegisters()
    {
        for (int i = 0; i < nbRegisters ; i++)
            this.isRegisterFreeMeth[i] = true;
    }

    public void resetMainRegisters()
    {
        for (int i = 0; i < nbRegisters ; i++)
            this.isRegisterFree[i] = true;
    }
    
    /**
    * Free the register
    *
    *
    */
    public void freeGPRegister(GPRegister reg) {
        int i = reg.getNumber();
        if (this.compiler.getStatus())
            isRegisterFreeMeth[i] = true;
        else
            isRegisterFree[i] = true;
    }

    public void freeStack() {
        while (!this.stack.isEmpty())
            this.compiler.addInstruction(new POP(this.stack.pop()));
    }

}
