package com.mycompany.app.ast.component.constants;

import com.mycompany.app.ast.value.Num;

import lombok.Getter;

/* Port Constant */
@Getter
public class PortComp extends CompConst {
    private Num portNum;

    PortComp(int lineNumber, Num portNum) {
        super(lineNumber);
        this.portNum = portNum;
    }
}
