package com.mycompany.app.ast.component.constants;

import com.mycompany.app.ast.component.constants.component_types.ProtocolType;

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
