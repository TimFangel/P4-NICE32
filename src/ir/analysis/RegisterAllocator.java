package ir.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exception.RegisterSpillException;
import ir.IrInstruction;
import ir.IrValue;
import ir.cfg.BasicBlock;
import ir.cfg.ControlFlowGraph;
import ir.util.IrOperator;
import lombok.Getter;
import frontend.abstract_syntax.type.Type;

public class RegisterAllocator {
    @Getter
    ControlFlowGraph cfg;
    Map<String, Set<String>> interference;
    List<String> temps = new ArrayList<>();
    Map<String, Type> temporaryTypes = new HashMap<>();

    // List of all ESP32 registers we use based on temporary type.
    Map<Type, List<String>> registers = Map.of(
            Type.INT_T, List.of("a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "a10", "a11", "a12", "a13", "a14"),
            Type.BOOL_T,
            List.of("b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "b10", "b11", "b12", "b13", "b14"),
            Type.FLOAT_T, List.of("f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8")

    );

    // Map of temporaries and which register they have been allocated.
    Map<String, String> allocatedRegisters = new HashMap<>();

    public RegisterAllocator(
            Map<String, Set<String>> interference, ControlFlowGraph cfg) {
        this.cfg = cfg;
        this.interference = interference;

        getTemporaries();
        System.out.println("All temporary variables:\n" + temps);

        this.allocatedRegisters = allocateRegisters();
        System.out.println("\n" + allocatedRegisters);

        // Update CFG with newly allocated registers.
        updateCfg(this.allocatedRegisters);
    }

    /**
     * Allocates ESP32 registers based on type and interference
     * 
     * @return a map mapping temporaries to registers
     */
    private Map<String, String> allocateRegisters() {
        // Map for containing temporaries and their allocated register.
        Map<String, String> allocate = new HashMap<>();

        // Sort based on most interference first.
        temps.sort((a, b) -> Integer.compare(
                // Returns value of a/b or else empty set, then finds their size.
                interference.getOrDefault(b, Set.of()).size(),
                interference.getOrDefault(a, Set.of()).size()));

        // Iterate through all temporaries and find unavailable registers.
        for (String t : temps) {
            Set<String> unavailable = new HashSet<>();

            // Populate the set with interfering temporaries' registers.
            for (String n : interference.getOrDefault(t, Set.of())) {
                String allocatedRegister = allocate.get(n);

                if (n != null) {
                    unavailable.add(allocatedRegister);
                }
            }

            // Get temporary variable type.
            Type type = temporaryTypes.get(t);

            // Get list of available registers for the temporary type.
            List<String> regs = registers.get(type);

            String chosenRegister = null;

            /*
             * Iterate through the registers and check if they have been used by an
             * interfering temporary.
             */
            for (String r : regs) {
                if (!unavailable.contains(r)) {
                    chosenRegister = r;
                    break;
                }
            }

            /*
             * If no register could be allocated, throw exception, else allocate the
             * register.
             */
            if (chosenRegister == null) {
                throw new RegisterSpillException(
                        "ID-10T: Program requires more registers than available. Could not allocate variable " + t
                                + " of type " + type + " to a register");
            } else {
                allocate.put(t, chosenRegister);
            }
        }

        return allocate;
    }

    /**
     * Finds all temporary variables.
     */
    private void getTemporaries() {
        for (BasicBlock b : cfg.getBlocks()) {
            for (IrInstruction i : b.getInstructions()) {
                IrValue temp = i.getResult();

                if (temp == null) {
                    continue;
                }

                Type type = temp.getType();

                // labels not included
                if (type != Type.LABEL) {
                    String name = temp.getName();
                    if (name.charAt(0) == 't' && Character.isDigit(name.charAt(1))) {
                        this.temps.add(name);
                        this.temporaryTypes.put(name, type);
                    }
                }
            }
        }
    }

    /**
     * Updates the CFG with register names instead of temporary variables.
     * 
     * @param allocatedRegisters map containing which register each temporary should
     *                           be set to.
     */
    private void updateCfg(Map<String, String> allocatedRegisters) {
        List<BasicBlock> blocks = cfg.getBlocks();

        // For each block.
        for (BasicBlock b : blocks) {
            List<IrInstruction> instructions = b.getInstructions();

            // For each instruction in the block.
            for (IrInstruction i : instructions) {
                IrValue result = i.getResult();
                IrValue arg1 = i.getArg1();
                IrValue arg2 = i.getArg2();

                // Check if valid.
                if (result != null) {
                    Type type = result.getType();

                    if (type != Type.LABEL) {
                        String name = result.getName();

                        // Replace temp name with allocated register.
                        if (name.charAt(0) == 't' && Character.isDigit(name.charAt(1))) {
                            result.setName(allocatedRegisters.get(name));
                        }
                    }

                    i.setResult(result);
                }

                if (arg1 != null) {
                    String name = arg1.getName();

                    // Replace temporary with allocated register
                    if (name.charAt(0) == 't' && Character.isDigit(name.charAt(1))) {
                        arg1.setName(allocatedRegisters.get(name));
                    }

                    i.setArg1(arg1);
                }

                if (arg2 != null) {
                    String name = arg2.getName();

                    // Replace temporary with allocated register
                    if (name.charAt(0) == 't' && Character.isDigit(name.charAt(1))) {
                        arg2.setName(allocatedRegisters.get(name));
                    }

                    i.setArg2(arg2);
                }
            }

            // update block with updated instructions
            b.setInstructions(instructions);
        }

        // update CFG with updated blocks.
        cfg.setBlocks(blocks);
    }
}
