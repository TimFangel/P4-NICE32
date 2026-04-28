package com.mycompany.app.ast.component.constants;

import com.mycompany.app.ast.component.constants.component_types.DirectionType;

import lombok.Getter;

/* Direction Constant */
@Getter
public class DirectionComp extends CompConst {
    private DirectionType direction;

    DirectionComp(int lineNumber, DirectionType direction) {
        super(lineNumber);
        this.direction = direction;
    }
    
}
