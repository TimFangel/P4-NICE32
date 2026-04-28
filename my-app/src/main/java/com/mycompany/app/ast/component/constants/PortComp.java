package com.mycompany.app.ast.component.constants;

import com.mycompany.app.ast.type.IntT;

import lombok.Getter;

/* Port Constant */
@Getter
public class PortComp extends CompConst {
    private IntT portNum;

    PortComp(int lineNumber, IntT portNum) {
        super(lineNumber);
        this.portNum = portNum;
    }
}
