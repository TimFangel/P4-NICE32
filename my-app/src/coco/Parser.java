package src.coco;

import src.abstract_syntax.*;
import java.util.*;



public class Parser {
	public static final int _EOF = 0;
	public static final int _FLOAT = 1;
	public static final int _INT = 2;
	public static final int _IDENT = 3;
	public static final int maxT = 27;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	public Program mainNode = null;

public boolean hasErrors() {
    return errors.count > 0;
}



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void NICE32() {
		BlockStmt setupBlock;
		BlockStmt functionBlock;
		BlockStmt mainBlock; 
		Expect(4);
		setupBlock = Block();
		Expect(5);
		functionBlock = Block();
		Expect(6);
		mainBlock = Block();
		mainNode = new Program(setupBlock, functionBlock, mainBlock); 
	}

	BlockStmt  Block() {
		BlockStmt  block;
		List<Stmt> stmts = new ArrayList<>(); Stmt s; 
		Expect(7);
		while (StartOf(1)) {
			s = Stmt();
			stmts.add(s); 
		}
		Expect(8);
		block = new BlockStmt(stmts); 
		return block;
	}

	Stmt  Stmt() {
		Stmt  stmt;
		stmt = null;
		Type type;
		String name;
		Expr value;
		
		if (la.kind == 15 || la.kind == 16 || la.kind == 17) {
			type = TypeName();
			Expect(3);
			name = t.val; 
			Expect(9);
			value = Expr();
			Expect(10);
			stmt = new Decl(type, name, value); 
		} else if (la.kind == 3) {
			Get();
			name = t.val; 
			Expect(9);
			value = Expr();
			Expect(10);
			stmt = new Assign(name, value); 
		} else if (la.kind == 11) {
			stmt = IfStmt();
		} else SynErr(28);
		return stmt;
	}

	Type  TypeName() {
		Type  type;
		type = Type.INT; 
		if (la.kind == 15) {
			Get();
			type = Type.INT; 
		} else if (la.kind == 16) {
			Get();
			type = Type.FLOAT; 
		} else if (la.kind == 17) {
			Get();
			type = Type.BOOL; 
		} else SynErr(29);
		return type;
	}

	Expr  Expr() {
		Expr  expr;
		expr = OrExpr();
		return expr;
	}

	Stmt  IfStmt() {
		Stmt  stmt;
		Expr cond;
		BlockStmt thenBlock;
		Stmt elseBranch = null;
		BlockStmt elseBlock;
		
		Expect(11);
		Expect(12);
		cond = Expr();
		Expect(13);
		thenBlock = Block();
		if (la.kind == 14) {
			Get();
			if (la.kind == 11) {
				elseBranch = IfStmt();
			} else if (la.kind == 7) {
				elseBlock = Block();
				elseBranch = elseBlock; 
			} else SynErr(30);
		}
		stmt = new If(cond, thenBlock, elseBranch); 
		return stmt;
	}

	Expr  OrExpr() {
		Expr  expr;
		Expr right; 
		expr = AndExpr();
		while (la.kind == 18) {
			Get();
			right = AndExpr();
			expr = new BinaryOp(expr, Op.OR, right); 
		}
		return expr;
	}

	Expr  AndExpr() {
		Expr  expr;
		Expr right; 
		expr = EqExpr();
		while (la.kind == 19) {
			Get();
			right = EqExpr();
			expr = new BinaryOp(expr, Op.AND, right); 
		}
		return expr;
	}

	Expr  EqExpr() {
		Expr  expr;
		Expr right; 
		expr = AddExpr();
		while (la.kind == 20) {
			Get();
			right = AddExpr();
			expr = new BinaryOp(expr, Op.EQ, right); 
		}
		return expr;
	}

	Expr  AddExpr() {
		Expr  expr;
		Expr right; 
		expr = MulExpr();
		while (la.kind == 21 || la.kind == 22) {
			if (la.kind == 21) {
				Get();
				right = MulExpr();
				expr = new BinaryOp(expr, Op.ADD, right); 
			} else {
				Get();
				right = MulExpr();
				expr = new BinaryOp(expr, Op.SUB, right); 
			}
		}
		return expr;
	}

	Expr  MulExpr() {
		Expr  expr;
		Expr right; 
		expr = UnaryExpr();
		while (la.kind == 23 || la.kind == 24) {
			if (la.kind == 23) {
				Get();
				right = UnaryExpr();
				expr = new BinaryOp(expr, Op.MUL, right); 
			} else {
				Get();
				right = UnaryExpr();
				expr = new BinaryOp(expr, Op.DIV, right); 
			}
		}
		return expr;
	}

	Expr  UnaryExpr() {
		Expr  expr;
		expr = null; Type castType; 
		if (la.kind == 12) {
			Get();
			if (la.kind == 15 || la.kind == 16 || la.kind == 17) {
				castType = TypeName();
				Expect(13);
				Expr inner = UnaryExpr();
				expr = new Cast(castType, inner); 
			} else if (StartOf(2)) {
				expr = Expr();
				Expect(13);
			} else SynErr(31);
		} else if (StartOf(3)) {
			expr = PrimaryNoParens();
		} else SynErr(32);
		return expr;
	}

	Expr  PrimaryNoParens() {
		Expr  expr;
		expr = null; 
		if (la.kind == 2) {
			Get();
			expr = new IntNum(Integer.parseInt(t.val)); 
		} else if (la.kind == 1) {
			Get();
			expr = new FloatNum(Float.parseFloat(t.val)); 
		} else if (la.kind == 25) {
			Get();
			expr = new Bool(true); 
		} else if (la.kind == 26) {
			Get();
			expr = new Bool(false); 
		} else if (la.kind == 3) {
			Get();
			expr = new Var(t.val); 
		} else SynErr(33);
		return expr;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		NICE32();
		Expect(0);

		scanner.buffer.Close();
	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x},
		{_x,_x,_x,_T, _x,_x,_x,_x, _x,_x,_x,_T, _x,_x,_x,_T, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x},
		{_x,_T,_T,_T, _x,_x,_x,_x, _x,_x,_x,_x, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_T,_T,_x, _x},
		{_x,_T,_T,_T, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_T,_T,_x, _x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "FLOAT expected"; break;
			case 2: s = "INT expected"; break;
			case 3: s = "IDENT expected"; break;
			case 4: s = "\"setup\" expected"; break;
			case 5: s = "\"functions\" expected"; break;
			case 6: s = "\"main\" expected"; break;
			case 7: s = "\"{\" expected"; break;
			case 8: s = "\"}\" expected"; break;
			case 9: s = "\"=\" expected"; break;
			case 10: s = "\";\" expected"; break;
			case 11: s = "\"if\" expected"; break;
			case 12: s = "\"(\" expected"; break;
			case 13: s = "\")\" expected"; break;
			case 14: s = "\"else\" expected"; break;
			case 15: s = "\"int\" expected"; break;
			case 16: s = "\"float\" expected"; break;
			case 17: s = "\"bool\" expected"; break;
			case 18: s = "\"||\" expected"; break;
			case 19: s = "\"&&\" expected"; break;
			case 20: s = "\"==\" expected"; break;
			case 21: s = "\"+\" expected"; break;
			case 22: s = "\"-\" expected"; break;
			case 23: s = "\"*\" expected"; break;
			case 24: s = "\"/\" expected"; break;
			case 25: s = "\"true\" expected"; break;
			case 26: s = "\"false\" expected"; break;
			case 27: s = "??? expected"; break;
			case 28: s = "invalid Stmt"; break;
			case 29: s = "invalid TypeName"; break;
			case 30: s = "invalid IfStmt"; break;
			case 31: s = "invalid UnaryExpr"; break;
			case 32: s = "invalid UnaryExpr"; break;
			case 33: s = "invalid PrimaryNoParens"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
