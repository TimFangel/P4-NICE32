package frontend.abstract_syntax.expression;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberAccess extends Expr {
    private final String component;
    private final String variable;

    public MemberAccess(int lineNumber, String component, String variable) {
        super(lineNumber);
        this.component = component;
        this.variable = variable;
    }
}