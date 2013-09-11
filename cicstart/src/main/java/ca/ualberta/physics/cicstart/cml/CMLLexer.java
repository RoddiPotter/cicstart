// Generated from CML.g4 by ANTLR 4.0
package ca.ualberta.physics.cicstart.cml;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CMLLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__12=1, T__11=2, T__10=3, T__9=4, T__8=5, T__7=6, T__6=7, T__5=8, T__4=9, 
		T__3=10, T__2=11, T__1=12, T__0=13, ID=14, STRING=15, ESC=16, INT=17, 
		FLOAT=18, LINE_COMMENT=19, COMMENT=20, WS=21;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'on'", "')'", "'in'", "','", "'('", "'='", "';'", "'and wait'", "'{'", 
		"'cforeach'", "'}'", "'$'", "'foreach'", "ID", "STRING", "ESC", "INT", 
		"FLOAT", "LINE_COMMENT", "COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", 
		"T__3", "T__2", "T__1", "T__0", "ID", "STRING", "ESC", "INT", "FLOAT", 
		"LINE_COMMENT", "COMMENT", "WS"
	};


	public CMLLexer(CharStream input) {
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
		case 18: LINE_COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 19: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 20: WS_action((RuleContext)_localctx, actionIndex); break;
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
		"\2\4\27\u00b0\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2"+
		"\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17"+
		"\6\17_\n\17\r\17\16\17`\3\17\3\17\6\17e\n\17\r\17\16\17f\3\17\6\17j\n"+
		"\17\r\17\16\17k\5\17n\n\17\3\20\3\20\3\20\7\20s\n\20\f\20\16\20v\13\20"+
		"\3\20\3\20\3\21\3\21\3\21\3\21\5\21~\n\21\3\22\6\22\u0081\n\22\r\22\16"+
		"\22\u0082\3\23\6\23\u0086\n\23\r\23\16\23\u0087\3\23\3\23\3\24\3\24\3"+
		"\24\3\24\7\24\u0090\n\24\f\24\16\24\u0093\13\24\3\24\5\24\u0096\n\24\3"+
		"\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\7\25\u00a0\n\25\f\25\16\25\u00a3"+
		"\13\25\3\25\3\25\3\25\3\25\3\25\3\26\6\26\u00ab\n\26\r\26\16\26\u00ac"+
		"\3\26\3\26\5t\u0091\u00a1\27\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t"+
		"\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1"+
		"#\23\1%\24\1\'\25\2)\26\3+\27\4\3\2\7\4C\\c|\4C\\c|\3\62;\3\62;\5\13\f"+
		"\17\17\"\"\u00bd\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\3-\3"+
		"\2\2\2\5\60\3\2\2\2\7\62\3\2\2\2\t\65\3\2\2\2\13\67\3\2\2\2\r9\3\2\2\2"+
		"\17;\3\2\2\2\21=\3\2\2\2\23F\3\2\2\2\25H\3\2\2\2\27Q\3\2\2\2\31S\3\2\2"+
		"\2\33U\3\2\2\2\35^\3\2\2\2\37o\3\2\2\2!}\3\2\2\2#\u0080\3\2\2\2%\u0085"+
		"\3\2\2\2\'\u008b\3\2\2\2)\u009b\3\2\2\2+\u00aa\3\2\2\2-.\7q\2\2./\7p\2"+
		"\2/\4\3\2\2\2\60\61\7+\2\2\61\6\3\2\2\2\62\63\7k\2\2\63\64\7p\2\2\64\b"+
		"\3\2\2\2\65\66\7.\2\2\66\n\3\2\2\2\678\7*\2\28\f\3\2\2\29:\7?\2\2:\16"+
		"\3\2\2\2;<\7=\2\2<\20\3\2\2\2=>\7c\2\2>?\7p\2\2?@\7f\2\2@A\7\"\2\2AB\7"+
		"y\2\2BC\7c\2\2CD\7k\2\2DE\7v\2\2E\22\3\2\2\2FG\7}\2\2G\24\3\2\2\2HI\7"+
		"e\2\2IJ\7h\2\2JK\7q\2\2KL\7t\2\2LM\7g\2\2MN\7c\2\2NO\7e\2\2OP\7j\2\2P"+
		"\26\3\2\2\2QR\7\177\2\2R\30\3\2\2\2ST\7&\2\2T\32\3\2\2\2UV\7h\2\2VW\7"+
		"q\2\2WX\7t\2\2XY\7g\2\2YZ\7c\2\2Z[\7e\2\2[\\\7j\2\2\\\34\3\2\2\2]_\t\2"+
		"\2\2^]\3\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2am\3\2\2\2bi\7\60\2\2ce\t"+
		"\3\2\2dc\3\2\2\2ef\3\2\2\2fd\3\2\2\2fg\3\2\2\2gj\3\2\2\2hj\5#\22\2id\3"+
		"\2\2\2ih\3\2\2\2jk\3\2\2\2ki\3\2\2\2kl\3\2\2\2ln\3\2\2\2mb\3\2\2\2mn\3"+
		"\2\2\2n\36\3\2\2\2ot\7$\2\2ps\5!\21\2qs\13\2\2\2rp\3\2\2\2rq\3\2\2\2s"+
		"v\3\2\2\2tu\3\2\2\2tr\3\2\2\2uw\3\2\2\2vt\3\2\2\2wx\7$\2\2x \3\2\2\2y"+
		"z\7^\2\2z~\7$\2\2{|\7^\2\2|~\7^\2\2}y\3\2\2\2}{\3\2\2\2~\"\3\2\2\2\177"+
		"\u0081\t\4\2\2\u0080\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0080\3\2\2"+
		"\2\u0082\u0083\3\2\2\2\u0083$\3\2\2\2\u0084\u0086\t\5\2\2\u0085\u0084"+
		"\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088"+
		"\u0089\3\2\2\2\u0089\u008a\7\60\2\2\u008a&\3\2\2\2\u008b\u008c\7\61\2"+
		"\2\u008c\u008d\7\61\2\2\u008d\u0091\3\2\2\2\u008e\u0090\13\2\2\2\u008f"+
		"\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u0092\3\2\2\2\u0091\u008f\3\2"+
		"\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0096\7\17\2\2\u0095"+
		"\u0094\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0098\7\f"+
		"\2\2\u0098\u0099\3\2\2\2\u0099\u009a\b\24\2\2\u009a(\3\2\2\2\u009b\u009c"+
		"\7\61\2\2\u009c\u009d\7,\2\2\u009d\u00a1\3\2\2\2\u009e\u00a0\13\2\2\2"+
		"\u009f\u009e\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a1\u009f"+
		"\3\2\2\2\u00a2\u00a4\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\7,\2\2\u00a5"+
		"\u00a6\7\61\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a8\b\25\3\2\u00a8*\3\2\2"+
		"\2\u00a9\u00ab\t\6\2\2\u00aa\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00aa"+
		"\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00af\b\26\4\2"+
		"\u00af,\3\2\2\2\21\2`fikmrt}\u0082\u0087\u0091\u0095\u00a1\u00ac";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}