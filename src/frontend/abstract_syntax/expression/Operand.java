package frontend.abstract_syntax.expression;

import frontend.abstract_syntax.value.Value;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public final class Operand extends Expr {
    Value value;

    public Operand(int lineNumber, Value value) {
        super(lineNumber);

        this.value = value;
    }
}
