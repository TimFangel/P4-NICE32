package com.mycompany.app.symboltable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

// Object describing a declared symbol in symboltable, used for lookup of identifiers.
@RequiredArgsConstructor
public class Symbol { 
    public @Getter @Setter String name; // Name of symbol
    public @Getter @Setter int type; // Type of symbol: int, float, bool
    public @Getter @Setter Symbol next; // Next symbol in same scope
    public @Getter @Setter int category; // The category of symbol: variable, constant, function, component, constant, scope
    public @Getter @Setter Symbol locals; // The local symbols 
    public @Getter @Setter int level; // O = global, 1 = local
}
