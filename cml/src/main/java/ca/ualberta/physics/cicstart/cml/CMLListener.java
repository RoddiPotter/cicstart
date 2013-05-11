// Generated from CML.g4 by ANTLR 4.0
package ca.ualberta.physics.cicstart.cml;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface CMLListener extends ParseTreeListener {
	void enterId(CMLParser.IdContext ctx);
	void exitId(CMLParser.IdContext ctx);

	void enterForeach(CMLParser.ForeachContext ctx);
	void exitForeach(CMLParser.ForeachContext ctx);

	void enterStatement(CMLParser.StatementContext ctx);
	void exitStatement(CMLParser.StatementContext ctx);

	void enterAssignment(CMLParser.AssignmentContext ctx);
	void exitAssignment(CMLParser.AssignmentContext ctx);

	void enterMacro(CMLParser.MacroContext ctx);
	void exitMacro(CMLParser.MacroContext ctx);

	void enterParameter(CMLParser.ParameterContext ctx);
	void exitParameter(CMLParser.ParameterContext ctx);

	void enterStruct(CMLParser.StructContext ctx);
	void exitStruct(CMLParser.StructContext ctx);

	void enterParameters(CMLParser.ParametersContext ctx);
	void exitParameters(CMLParser.ParametersContext ctx);

	void enterFunction(CMLParser.FunctionContext ctx);
	void exitFunction(CMLParser.FunctionContext ctx);
}