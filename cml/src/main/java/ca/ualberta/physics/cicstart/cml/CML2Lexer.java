// Generated from CML.g4 by ANTLR 4.0
package ca.ualberta.physics.cicstart.cml;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CML2Lexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__10=1, T__9=2, T__8=3, T__7=4, T__6=5, T__5=6, T__4=7, T__3=8, T__2=9, 
		T__1=10, T__0=11, VARIABLE=12, STRING=13, ESC=14, ID=15, LINE_COMMENT=16, 
		COMMENT=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'and wait'", "'{'", "'in'", "')'", "','", "'cforeach'", "'('", "'='", 
		"'}'", "';'", "'foreach'", "VARIABLE", "STRING", "ESC", "ID", "LINE_COMMENT", 
		"COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", 
		"T__1", "T__0", "VARIABLE", "STRING", "ESC", "ID", "LINE_COMMENT", "COMMENT", 
		"WS"
	};


	public CML2Lexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CML.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 15: LINE_COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 16: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 17: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2: skip();  break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1: skip();  break;
		}
	}
	private void LINE_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\2\4\24\u0092\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\r\3\r\6\rU\n\r\r\r\16\rV\3\16\3\16\3\16\7\16\\\n\16\f\16\16\16_\13"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\17\5\17g\n\17\3\20\6\20j\n\20\r\20\16\20"+
		"k\3\21\3\21\3\21\3\21\7\21r\n\21\f\21\16\21u\13\21\3\21\5\21x\n\21\3\21"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\22\7\22\u0082\n\22\f\22\16\22\u0085\13"+
		"\22\3\22\3\22\3\22\3\22\3\22\3\23\6\23\u008d\n\23\r\23\16\23\u008e\3\23"+
		"\3\23\5]s\u0083\24\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1"+
		"\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\2#\23\3%\24"+
		"\4\3\2\5\7\60\60\62;C\\^^c|\5\62;C\\c|\5\13\f\17\17\"\"\u009a\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3"+
		"\2\2\2\3\'\3\2\2\2\5\60\3\2\2\2\7\62\3\2\2\2\t\65\3\2\2\2\13\67\3\2\2"+
		"\2\r9\3\2\2\2\17B\3\2\2\2\21D\3\2\2\2\23F\3\2\2\2\25H\3\2\2\2\27J\3\2"+
		"\2\2\31R\3\2\2\2\33X\3\2\2\2\35f\3\2\2\2\37i\3\2\2\2!m\3\2\2\2#}\3\2\2"+
		"\2%\u008c\3\2\2\2\'(\7c\2\2()\7p\2\2)*\7f\2\2*+\7\"\2\2+,\7y\2\2,-\7c"+
		"\2\2-.\7k\2\2./\7v\2\2/\4\3\2\2\2\60\61\7}\2\2\61\6\3\2\2\2\62\63\7k\2"+
		"\2\63\64\7p\2\2\64\b\3\2\2\2\65\66\7+\2\2\66\n\3\2\2\2\678\7.\2\28\f\3"+
		"\2\2\29:\7e\2\2:;\7h\2\2;<\7q\2\2<=\7t\2\2=>\7g\2\2>?\7c\2\2?@\7e\2\2"+
		"@A\7j\2\2A\16\3\2\2\2BC\7*\2\2C\20\3\2\2\2DE\7?\2\2E\22\3\2\2\2FG\7\177"+
		"\2\2G\24\3\2\2\2HI\7=\2\2I\26\3\2\2\2JK\7h\2\2KL\7q\2\2LM\7t\2\2MN\7g"+
		"\2\2NO\7c\2\2OP\7e\2\2PQ\7j\2\2Q\30\3\2\2\2RT\7&\2\2SU\t\2\2\2TS\3\2\2"+
		"\2UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2W\32\3\2\2\2X]\7$\2\2Y\\\5\35\17\2Z\\"+
		"\13\2\2\2[Y\3\2\2\2[Z\3\2\2\2\\_\3\2\2\2]^\3\2\2\2][\3\2\2\2^`\3\2\2\2"+
		"_]\3\2\2\2`a\7$\2\2a\34\3\2\2\2bc\7^\2\2cg\7$\2\2de\7^\2\2eg\7^\2\2fb"+
		"\3\2\2\2fd\3\2\2\2g\36\3\2\2\2hj\t\3\2\2ih\3\2\2\2jk\3\2\2\2ki\3\2\2\2"+
		"kl\3\2\2\2l \3\2\2\2mn\7\61\2\2no\7\61\2\2os\3\2\2\2pr\13\2\2\2qp\3\2"+
		"\2\2ru\3\2\2\2st\3\2\2\2sq\3\2\2\2tw\3\2\2\2us\3\2\2\2vx\7\17\2\2wv\3"+
		"\2\2\2wx\3\2\2\2xy\3\2\2\2yz\7\f\2\2z{\3\2\2\2{|\b\21\2\2|\"\3\2\2\2}"+
		"~\7\61\2\2~\177\7,\2\2\177\u0083\3\2\2\2\u0080\u0082\13\2\2\2\u0081\u0080"+
		"\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0084\3\2\2\2\u0083\u0081\3\2\2\2\u0084"+
		"\u0086\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0087\7,\2\2\u0087\u0088\7\61"+
		"\2\2\u0088\u0089\3\2\2\2\u0089\u008a\b\22\3\2\u008a$\3\2\2\2\u008b\u008d"+
		"\t\4\2\2\u008c\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008c\3\2\2\2\u008e"+
		"\u008f\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0091\b\23\4\2\u0091&\3\2\2\2"+
		"\f\2V[]fksw\u0083\u008e";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}