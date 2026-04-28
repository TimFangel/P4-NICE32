package frontend.abstract_syntax.component.constants;

import frontend.abstract_syntax.value.Num;

import lombok.Getter;
import lombok.ToString;

/* Port Constant */
@ToString
@Getter
public class PortComp extends CompConst {
    private Num portNum;

    PortComp(int lineNumber, Num portNum) {
        super(lineNumber);
        this.portNum = portNum;
    }
}
