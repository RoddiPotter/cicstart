/*
 * CICSTART MACRO LANGUAGE
 * 
 * Helps you run stuff
 *
 * generate java files using:
 *  antlr -package ca.ualberta.physics.cicstart.cml CML.g4
 *
 */
grammar CML;

// this grammar is made up of statements
macro       : (statement(';')?)+
            ;

// a statement may be a function, a variable assignment
// or a foreach style loop
statement   : function 
            | assignment 
            | foreach
            ;

// foreach has 2 variants, one for synchronous iteration
// and one for parallel iteration with an optional barrier
// at the end
foreach     : 'foreach' id 'in' VARIABLE '{' macro* '}'                     
            | 'cforeach' id 'in' VARIABLE '{' macro* '}' ('and wait')?      
            ;

// a function can either take a list of parameters
// or a struct (anonymous object)
function    : ID '(' parameters ')'                                         
            | ID '(' struct ')'                                         	
            ;

// assignments set variable data to values returned
// from statements or a set of parameters (like in a struct)
assignment  : ID '=' function 												
            | ID '=' '(' parameters ')'										
            | ID '=' parameter												
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
parameter   : VARIABLE
            | STRING 
            ;
// ids follow a standard naming pattern
id          : ID ;

// variables always start with a $ sign.
VARIABLE    : '$'[a-zA-Z0-9\.]+ ;

// strings are double quoted, escape " with a \, for example
// "a \"double quoted\" string
STRING      : '"' (ESC|.)*? '"' ;
ESC         : '\\"' | '\\\\' ; // 2-char sequences \" and \\
ID          : [a-zA-Z0-9]+ ; // alphanumeric only
LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ; // Match "//" stuff '\n'
COMMENT     : '/*' .*? '*/' -> skip ; // Match "/*" stuff "*/"
WS          : [ \t\r\n]+ -> skip;
