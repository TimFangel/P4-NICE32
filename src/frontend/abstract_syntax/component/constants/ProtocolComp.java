package frontend.abstract_syntax.component.constants;

import frontend.abstract_syntax.component.constants.component_types.ProtocolType;
import lombok.Getter;
import lombok.ToString;

/* Protocol Constant */
@ToString
@Getter
public class ProtocolComp extends CompConst {
    private ProtocolType protocol;

    ProtocolComp(int lineNumber, ProtocolType protocol) {
        super(lineNumber);
        this.protocol = protocol;
    }
    
}
