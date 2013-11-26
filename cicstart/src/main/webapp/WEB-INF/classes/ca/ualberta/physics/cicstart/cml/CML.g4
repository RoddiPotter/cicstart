/*
 * CICSTART MACRO LANGUAGE
 * 
 * Helps you run stuff
 *
 *  ~/workspaces/cicstart/cml/src/main/java/ca/ualberta/physics/cicstart/cml$ antlr -package ca.ualberta.physics.cicstart.cml CML.g4
 *  ~/workspaces/cicstart/cml/src/main/webapp/WEB-INF/classes$ grun ca.ualberta.physics.cicstart.cml.CML macro -tokens test.cml
 *  ~/workspaces/cicstart/cml/src/main/webapp/WEB-INF/classes$ grun ca.ualberta.physics.cicstart.cml.CML macro -gui test.cml
 */
grammar CML;

// this grammar is made up of statements
macro       : (statement(';')?)+
            ;

// a statement may be a function, a variable assignment,
// an on directive, or a foreach style loop
statement   : function
			| assignment
			| expr
            ;
// foreach has 2 variants, one for synchronous iteration
// and one for parallel iteration with an optional barrier
// at the end
expr	    : 'foreach' id 'in' variable '{' macro* '}'       			# foreach              
            | 'cforeach' id 'in' variable '{' macro* '}' ('and wait')?	# cforeach
            | 'on' (variable|STRING) '{' macro* '}'						# on
            ;
            
// a function can either take a list of parameters
// or a struct (anonymous object)
function    : id '(' parameters ')'                                         
            | id '(' struct ')'                                         	
            ;
// assignments set variable data to values returned
// from statements or a set of parameters (like in a struct)
assignment  : id '=' function 												
            | id '=' '(' parameters ')'										
            | id '=' parameter												
            ;
// a struct is like an anonymous object of key=value
// paris.  Values maybe of arbitrary complexity (lists of
// more structs).
struct      : '{' assignment (',' assignment)*  '}'
            ;
// parameters is just a list of paramater
parameters  : parameter (',' parameter)* 
            ;
// a parameter can be a variable (from an assignment or 
// a globally known variable, or a simple string.  Strings
parameter   : variable
            | STRING
            | INT
            | FLOAT
            ;

variable	: '$' ID ;      
id			: ID;

ID			: [a-zA-Z]+ ('.' ([a-zA-Z]+|INT)+)? ;          
STRING		: '"' (ESC|.)*? '"' ;
ESC			: '\\"' | '\\\\' ; 					// 2-char sequences \" and \\
INT			: [0-9]+ ; 							// numbers
FLOAT		: [0-9]+ '.' ;
LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ; 	// Match "//" stuff '\n'
COMMENT     : '/*' .*? '*/' -> skip ; 			// Match "/*" stuff "*/"
WS          : [ \t\r\n]+ -> skip;				// skip all whitespace
