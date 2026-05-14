package ir.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ir.cfg.ControlFlowGraph;
import frontend.abstract_syntax.type.Type;

public class RegisterAllocator {
    ControlFlowGraph cfg;
    Map<String, Set<String>> interference;

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
    }

    private Map<String, String> allocateRegisters() {
        // opret et map fra temporaries til reelle registre (f.eks. t -> a7)

        // lav en liste af alle temporaries, og sorter den efter mængden af interference
        // med andre temporaries

        // iterér igennem listen, og lav et set med unavailable registre.
        // fyld dette set op med de registre som allerede er brugt af de temporaries som
        // den intefererer med.
        // hent listen af tilgængelige registre for typen af tempen
        // iterer gennem denne liste, og hvis ikke registret er i unavailable, så
        // alloker det valgte register.
        // smid dette i det første map.
        // hvis et register ikke kunne findes, så map til SPILL i stedet.

        // returner mappet.
    }
}
