package frontend.abstract_syntax.component.constants;

import frontend.abstract_syntax.Node;

import lombok.Getter;
import lombok.ToString;

/* Component Constant Superclass */
@ToString
@Getter
public abstract sealed class CompConst extends Node permits DirectionComp, ProtocolComp {
    CompConst(int lineNumber) {
        super(lineNumber);
    }
}
