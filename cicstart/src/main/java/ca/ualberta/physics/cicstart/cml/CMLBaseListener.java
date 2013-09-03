// Generated from CML.g4 by ANTLR 4.0
package ca.ualberta.physics.cicstart.cml;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CMLBaseListener implements CMLListener {
	@Override public void enterId(CMLParser.IdContext ctx) { }
	@Override public void exitId(CMLParser.IdContext ctx) { }

	@Override public void enterForeach(CMLParser.ForeachContext ctx) { }
	@Override public void exitForeach(CMLParser.ForeachContext ctx) { }

	@Override public void enterStatement(CMLParser.StatementContext ctx) { }
	@Override public void exitStatement(CMLParser.StatementContext ctx) { }

	@Override public void enterAssignment(CMLParser.AssignmentContext ctx) { }
	@Override public void exitAssignment(CMLParser.AssignmentContext ctx) { }

	@Override public void enterMacro(CMLParser.MacroContext ctx) { }
	@Override public void exitMacro(CMLParser.MacroContext ctx) { }

	@Override public void enterParameter(CMLParser.ParameterContext ctx) { }
	@Override public void exitParameter(CMLParser.ParameterContext ctx) { }

	@Override public void enterStruct(CMLParser.StructContext ctx) { }
	@Override public void exitStruct(CMLParser.StructContext ctx) { }

	@Override public void enterOn(CMLParser.OnContext ctx) { }
	@Override public void exitOn(CMLParser.OnContext ctx) { }

	@Override public void enterParameters(CMLParser.ParametersContext ctx) { }
	@Override public void exitParameters(CMLParser.ParametersContext ctx) { }

	@Override public void enterVariable(CMLParser.VariableContext ctx) { }
	@Override public void exitVariable(CMLParser.VariableContext ctx) { }

	@Override public void enterCforeach(CMLParser.CforeachContext ctx) { }
	@Override public void exitCforeach(CMLParser.CforeachContext ctx) { }

	@Override public void enterFunction(CMLParser.FunctionContext ctx) { }
	@Override public void exitFunction(CMLParser.FunctionContext ctx) { }

	@Override public void enterEveryRule(ParserRuleContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }
	@Override public void visitTerminal(TerminalNode node) { }
	@Override public void visitErrorNode(ErrorNode node) { }
}