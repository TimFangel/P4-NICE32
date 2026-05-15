package ir;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class IrValue {
    @Setter
    private String name; // t1, 2, 2.5, true
    private Type type; // bool, int, func, label etc.

    public IrValue(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
