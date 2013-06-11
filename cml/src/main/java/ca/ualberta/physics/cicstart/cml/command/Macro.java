package ca.ualberta.physics.cicstart.cml.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;

import ca.ualberta.physics.cicstart.cml.CMLBaseListener;
import ca.ualberta.physics.cicstart.cml.CMLParser.AssignmentContext;
import ca.ualberta.physics.cicstart.cml.CMLParser.ForeachContext;
import ca.ualberta.physics.cicstart.cml.CMLParser.FunctionContext;
import ca.ualberta.physics.cicstart.cml.CMLParser.OnContext;
import ca.ualberta.physics.cicstart.cml.CMLParser.ParameterContext;
import ca.ualberta.physics.cicstart.cml.CMLParser.StatementContext;
import ca.ualberta.physics.cicstart.cml.CMLParser.VariableContext;

public class Macro extends CMLBaseListener {

	private List<CommandDefinition> commands = new ArrayList<CommandDefinition>();

	private Stack<NestedCommandDefinition> nesting = new Stack<NestedCommandDefinition>();

	private CommandDefinition command;
	private String variableToAssign;

	@Override
	public void enterOn(OnContext ctx) {
		VariableContext variable = ctx.variable();
		OnCommandDefinition cmd = new OnCommandDefinition(ctx.getText(), ctx
				.getChild(0).getText(), variable != null ? variable.getText()
				: ctx.STRING().getText());
		nesting.push(cmd);
	}

	@Override
	public void exitOn(OnContext ctx) {
		OnCommandDefinition cmd = (OnCommandDefinition) nesting.pop();
		if (nesting.size() == 0) {
			commands.add(cmd);
		} else {
			nesting.peek().addChild(cmd);
		}
		command = null;
	}

	@Override
	public void enterFunction(FunctionContext ctx) {
		command = new CommandDefinition(ctx.getText(), ctx.id().getText());
		ParserRuleContext parent = ctx.getParent();
		if (parent instanceof AssignmentContext) {
			if (variableToAssign != null) {
				command.setAssignment(variableToAssign);
				variableToAssign = null;
			}
		}
	}

	@Override
	public void exitFunction(FunctionContext ctx) {
		if (nesting.size() == 0) {
			commands.add(command);
		} else {
			nesting.peek().addChild(command);
		}
		command = null;
	}

	@Override
	public void enterAssignment(AssignmentContext ctx) {
		ParserRuleContext parent = ctx.getParent();
		if (parent instanceof StatementContext) {
			variableToAssign = ctx.id().getText();
			if (command != null) {
				command.setAssignment(variableToAssign);
			}
		}
	}

	@Override
	public void enterForeach(ForeachContext ctx) {
		boolean waitFlag = ctx.getChild(ctx.getChildCount() - 1).getText()
				.contains("wait");
		ForEachCommandDefinition cmd = new ForEachCommandDefinition(
				ctx.getText(), ctx.getChild(0).getText(), waitFlag);
		cmd.setCollectionVar(ctx.variable().getText());
		cmd.setIteratorVar(ctx.id().getText());
		nesting.push(cmd);
	}

	@Override
	public void exitForeach(ForeachContext ctx) {
		CommandDefinition cmd = nesting.pop();
		if (nesting.size() == 0) {
			commands.add(cmd);
		} else {
			nesting.peek().addChild(cmd);
		}
		command = null;
	}

	@Override
	public void enterParameter(ParameterContext ctx) {
		ParserRuleContext parent = ctx.getParent();
		if (parent instanceof AssignmentContext) {
			// deal with struct
			AssignmentContext assignment = (AssignmentContext) parent;
			command.addStructParameter(assignment.id().getText(), ctx.getText());
		} else if (parent.getParent() instanceof AssignmentContext) {
			AssignmentContext assignment = (AssignmentContext) parent
					.getParent();
			command.addStructParameter(assignment.id().getText(), ctx.getText());
		} else {
			command.addParameterName(ctx.getText());
		}
	}

	public List<CommandDefinition> getCommands() {
		return commands;
	}
}
