// Generated from CML.g4 by ANTLR 4.0
package ca.ualberta.physics.cicstart.cml;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CMLParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__12=1, T__11=2, T__10=3, T__9=4, T__8=5, T__7=6, T__6=7, T__5=8, T__4=9, 
		T__3=10, T__2=11, T__1=12, T__0=13, ID=14, STRING=15, ESC=16, INT=17, 
		FLOAT=18, LINE_COMMENT=19, COMMENT=20, WS=21;
	public static final String[] tokenNames = {
		"<INVALID>", "'on'", "')'", "'in'", "','", "'('", "'='", "';'", "'and wait'", 
		"'{'", "'cforeach'", "'}'", "'$'", "'foreach'", "ID", "STRING", "ESC", 
		"INT", "FLOAT", "LINE_COMMENT", "COMMENT", "WS"
	};
	public static final int
		RULE_macro = 0, RULE_statement = 1, RULE_expr = 2, RULE_function = 3, 
		RULE_assignment = 4, RULE_struct = 5, RULE_parameters = 6, RULE_parameter = 7, 
		RULE_variable = 8, RULE_id = 9;
	public static final String[] ruleNames = {
		"macro", "statement", "expr", "function", "assignment", "struct", "parameters", 
		"parameter", "variable", "id"
	};

	@Override
	public String getGrammarFileName() { return "CML.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public CMLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class MacroContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public MacroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterMacro(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitMacro(this);
		}
	}

	public final MacroContext macro() throws RecognitionException {
		MacroContext _localctx = new MacroContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_macro);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(24); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(20); statement();
					setState(22);
					_la = _input.LA(1);
					if (_la==7) {
						{
						setState(21); match(7);
						}
					}

					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(26); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			} while ( _alt!=2 && _alt!=-1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(31);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(28); function();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(29); assignment();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(30); expr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ForeachContext extends ExprContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public List<MacroContext> macro() {
			return getRuleContexts(MacroContext.class);
		}
		public MacroContext macro(int i) {
			return getRuleContext(MacroContext.class,i);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public ForeachContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterForeach(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitForeach(this);
		}
	}
	public static class OnContext extends ExprContext {
		public List<MacroContext> macro() {
			return getRuleContexts(MacroContext.class);
		}
		public MacroContext macro(int i) {
			return getRuleContext(MacroContext.class,i);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode STRING() { return getToken(CMLParser.STRING, 0); }
		public OnContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterOn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitOn(this);
		}
	}
	public static class CforeachContext extends ExprContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public List<MacroContext> macro() {
			return getRuleContexts(MacroContext.class);
		}
		public MacroContext macro(int i) {
			return getRuleContext(MacroContext.class,i);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public CforeachContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterCforeach(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitCforeach(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_expr);
		int _la;
		try {
			setState(74);
			switch (_input.LA(1)) {
			case 13:
				_localctx = new ForeachContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(33); match(13);
				setState(34); id();
				setState(35); match(3);
				setState(36); variable();
				setState(37); match(9);
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 10) | (1L << 13) | (1L << ID))) != 0)) {
					{
					{
					setState(38); macro();
					}
					}
					setState(43);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(44); match(11);
				}
				break;
			case 10:
				_localctx = new CforeachContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(46); match(10);
				setState(47); id();
				setState(48); match(3);
				setState(49); variable();
				setState(50); match(9);
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 10) | (1L << 13) | (1L << ID))) != 0)) {
					{
					{
					setState(51); macro();
					}
					}
					setState(56);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(57); match(11);
				setState(59);
				_la = _input.LA(1);
				if (_la==8) {
					{
					setState(58); match(8);
					}
				}

				}
				break;
			case 1:
				_localctx = new OnContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(61); match(1);
				setState(64);
				switch (_input.LA(1)) {
				case 12:
					{
					setState(62); variable();
					}
					break;
				case STRING:
					{
					setState(63); match(STRING);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(66); match(9);
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 10) | (1L << 13) | (1L << ID))) != 0)) {
					{
					{
					setState(67); macro();
					}
					}
					setState(72);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(73); match(11);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public StructContext struct() {
			return getRuleContext(StructContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitFunction(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_function);
		try {
			setState(86);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(76); id();
				setState(77); match(5);
				setState(78); parameters();
				setState(79); match(2);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(81); id();
				setState(82); match(5);
				setState(83); struct();
				setState(84); match(2);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ParameterContext parameter() {
			return getRuleContext(ParameterContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitAssignment(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_assignment);
		try {
			setState(102);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(88); id();
				setState(89); match(6);
				setState(90); function();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(92); id();
				setState(93); match(6);
				setState(94); match(5);
				setState(95); parameters();
				setState(96); match(2);
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(98); id();
				setState(99); match(6);
				setState(100); parameter();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructContext extends ParserRuleContext {
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public StructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_struct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterStruct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitStruct(this);
		}
	}

	public final StructContext struct() throws RecognitionException {
		StructContext _localctx = new StructContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_struct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104); match(9);
			setState(105); assignment();
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(106); match(4);
				setState(107); assignment();
				}
				}
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(113); match(11);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParametersContext extends ParserRuleContext {
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitParameters(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115); parameter();
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(116); match(4);
				setState(117); parameter();
				}
				}
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(CMLParser.FLOAT, 0); }
		public TerminalNode INT() { return getToken(CMLParser.INT, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode STRING() { return getToken(CMLParser.STRING, 0); }
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitParameter(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_parameter);
		try {
			setState(127);
			switch (_input.LA(1)) {
			case 12:
				enterOuterAlt(_localctx, 1);
				{
				setState(123); variable();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(124); match(STRING);
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 3);
				{
				setState(125); match(INT);
				}
				break;
			case FLOAT:
				enterOuterAlt(_localctx, 4);
				{
				setState(126); match(FLOAT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CMLParser.ID, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitVariable(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129); match(12);
			setState(130); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CMLParser.ID, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CMLListener ) ((CMLListener)listener).exitId(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\2\3\27\u0089\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b"+
		"\4\t\t\t\4\n\t\n\4\13\t\13\3\2\3\2\5\2\31\n\2\6\2\33\n\2\r\2\16\2\34\3"+
		"\3\3\3\3\3\5\3\"\n\3\3\4\3\4\3\4\3\4\3\4\3\4\7\4*\n\4\f\4\16\4-\13\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4\67\n\4\f\4\16\4:\13\4\3\4\3\4\5\4>"+
		"\n\4\3\4\3\4\3\4\5\4C\n\4\3\4\3\4\7\4G\n\4\f\4\16\4J\13\4\3\4\5\4M\n\4"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5Y\n\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6i\n\6\3\7\3\7\3\7\3\7\7\7o\n\7"+
		"\f\7\16\7r\13\7\3\7\3\7\3\b\3\b\3\b\7\by\n\b\f\b\16\b|\13\b\3\t\3\t\3"+
		"\t\3\t\5\t\u0082\n\t\3\n\3\n\3\n\3\13\3\13\3\13\2\f\2\4\6\b\n\f\16\20"+
		"\22\24\2\2\u0091\2\32\3\2\2\2\4!\3\2\2\2\6L\3\2\2\2\bX\3\2\2\2\nh\3\2"+
		"\2\2\fj\3\2\2\2\16u\3\2\2\2\20\u0081\3\2\2\2\22\u0083\3\2\2\2\24\u0086"+
		"\3\2\2\2\26\30\5\4\3\2\27\31\7\t\2\2\30\27\3\2\2\2\30\31\3\2\2\2\31\33"+
		"\3\2\2\2\32\26\3\2\2\2\33\34\3\2\2\2\34\32\3\2\2\2\34\35\3\2\2\2\35\3"+
		"\3\2\2\2\36\"\5\b\5\2\37\"\5\n\6\2 \"\5\6\4\2!\36\3\2\2\2!\37\3\2\2\2"+
		"! \3\2\2\2\"\5\3\2\2\2#$\7\17\2\2$%\5\24\13\2%&\7\5\2\2&\'\5\22\n\2\'"+
		"+\7\13\2\2(*\5\2\2\2)(\3\2\2\2*-\3\2\2\2+)\3\2\2\2+,\3\2\2\2,.\3\2\2\2"+
		"-+\3\2\2\2./\7\r\2\2/M\3\2\2\2\60\61\7\f\2\2\61\62\5\24\13\2\62\63\7\5"+
		"\2\2\63\64\5\22\n\2\648\7\13\2\2\65\67\5\2\2\2\66\65\3\2\2\2\67:\3\2\2"+
		"\28\66\3\2\2\289\3\2\2\29;\3\2\2\2:8\3\2\2\2;=\7\r\2\2<>\7\n\2\2=<\3\2"+
		"\2\2=>\3\2\2\2>M\3\2\2\2?B\7\3\2\2@C\5\22\n\2AC\7\21\2\2B@\3\2\2\2BA\3"+
		"\2\2\2CD\3\2\2\2DH\7\13\2\2EG\5\2\2\2FE\3\2\2\2GJ\3\2\2\2HF\3\2\2\2HI"+
		"\3\2\2\2IK\3\2\2\2JH\3\2\2\2KM\7\r\2\2L#\3\2\2\2L\60\3\2\2\2L?\3\2\2\2"+
		"M\7\3\2\2\2NO\5\24\13\2OP\7\7\2\2PQ\5\16\b\2QR\7\4\2\2RY\3\2\2\2ST\5\24"+
		"\13\2TU\7\7\2\2UV\5\f\7\2VW\7\4\2\2WY\3\2\2\2XN\3\2\2\2XS\3\2\2\2Y\t\3"+
		"\2\2\2Z[\5\24\13\2[\\\7\b\2\2\\]\5\b\5\2]i\3\2\2\2^_\5\24\13\2_`\7\b\2"+
		"\2`a\7\7\2\2ab\5\16\b\2bc\7\4\2\2ci\3\2\2\2de\5\24\13\2ef\7\b\2\2fg\5"+
		"\20\t\2gi\3\2\2\2hZ\3\2\2\2h^\3\2\2\2hd\3\2\2\2i\13\3\2\2\2jk\7\13\2\2"+
		"kp\5\n\6\2lm\7\6\2\2mo\5\n\6\2nl\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2"+
		"qs\3\2\2\2rp\3\2\2\2st\7\r\2\2t\r\3\2\2\2uz\5\20\t\2vw\7\6\2\2wy\5\20"+
		"\t\2xv\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{\17\3\2\2\2|z\3\2\2\2}\u0082"+
		"\5\22\n\2~\u0082\7\21\2\2\177\u0082\7\23\2\2\u0080\u0082\7\24\2\2\u0081"+
		"}\3\2\2\2\u0081~\3\2\2\2\u0081\177\3\2\2\2\u0081\u0080\3\2\2\2\u0082\21"+
		"\3\2\2\2\u0083\u0084\7\16\2\2\u0084\u0085\7\20\2\2\u0085\23\3\2\2\2\u0086"+
		"\u0087\7\20\2\2\u0087\25\3\2\2\2\20\30\34!+8=BHLXhpz\u0081";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}