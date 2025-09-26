grammar lentz;

program     : (typeDecl | ruleDecl | opticDecl | defDecl)* EOF ;

typeDecl    : 'type' typeName '{' fieldDecl '}' ;
fieldDecl   : ident ':' typeRef  ;

typeRef     : simpleType
            | simpleType '<' typeRef (',' typeRef)* '>'
            ;
simpleType  : ident
            | 'Int'
            | 'String'
            | 'Bool'
            | 'Money'
            | 'List'
            | 'Map'
            | 'Option'
            | 'Result'
            | 'event'
            | 'signal'
            | 'Δ'
            ;

opticDecl   : 'optic' ident ':' opticType '=' opticExpr ;
opticType   : 'lens' '<' typeRef ',' typeRef '>'
            | 'affine' '<' typeRef ',' typeRef '>'
            | 'prism' '<' typeRef ',' typeRef '>'
            | 'traversal' '<' typeRef ',' typeRef '>'
            | 'view' '<' typeRef ',' typeRef '>'
            ;

opticExpr   : baseOptic ('.' opticOp)* ;
baseOptic   : 'lens' typeName '.' ident
            | 'prism' typeName '.' variant
            | 'view' typeName '.' ident
            | ident
            ;
opticOp     : 'then' '(' opticExpr ')'
            | 'each'
            | 'where' '(' expr ')'
            | 'byKey' '(' expr ')'
            | 'product' '(' opticExpr ')'
            | 'orDefault' '(' expr ')'
            ;

defDecl     : 'view' ident '(' paramList? ')' ':' typeRef '=' expr
            | 'def' ident '(' paramList? ')' ':' typeRef '=' expr
            ;

ruleDecl    : 'rule' ident ':' 'event' '<' deltaType '>' '->' 'event' '<' deltaType '>' '=' ruleExpr ;
deltaType   : 'Δ' typeRef;

paramList   : param (',' param)* ;
param       : ident ':' typeRef ;

ruleExpr    : lambdaExpr
            | expr
            ;

expr        : letExpr
            | ifExpr
            | matchExpr
            | lambdaExpr
            | fnApp
            | orExpr
            ;
letExpr     : 'let' ident '=' expr 'in' expr
            | 'let' letBind (',' letBind)* 'in' expr
            ;
letBind     : ident '=' expr ;

ifExpr      : 'if' '(' expr ')' expr 'else' expr ;

matchExpr   : 'match' expr '{' matchCase+ '}' ;
matchCase   : 'case' pattern '=>' expr ;
pattern     : '_'
            | ident
            | literal
            | constructorPattern
            ;
constructorPattern : ident '(' (pattern (',' pattern)*)? ')' ;

lambdaExpr  : '(' paramList? ')' '=>' expr ;

fnApp       : postFix '(' (argList?) ')' ;

argList     : expr (',' expr)* ;

postFix     : primary (postFixOp)* ;
postFixOp   : '.' ident
            | '.' ident '(' (argList?) ')'
            | '[' expr ']'
            ;

primary     : literal
            | ident
            | '(' expr ')'
            | listLiteral
            | mapLiteral
            | tupleLiteral
            ;

listLiteral : '[' (expr (',' expr)*)? ']' ;
mapLiteral  : '{' (mapEntry (',' mapEntry)*)? '}' ;
mapEntry    : expr ':' expr ;
tupleLiteral: '(' expr ',' expr (',' expr)* ')' ;

literal     : intLiteral
            | stringLiteral
            | boolLiteral
            | moneyLiteral
            | nullLiteral
            ;

intLiteral  : DIGIT+ ;
stringLiteral : STRING ;
boolLiteral : 'true' | 'false' ;
moneyLiteral : '$' INT ('.' DIGIT DIGIT)? ;
nullLiteral : 'null' ;

orExpr      : andExpr ('||' andExpr)* ;
andExpr     : cmpExpr ('&&' cmpExpr)* ;
cmpExpr     : addExpr ( relOp addExpr )* ;
relOp       : '<' | '<=' | '>' | '>=' ;
addExpr     : mulExpr ( ('+' | '-') mulExpr )* ;
mulExpr     : unaryExpr ( ('*' | '/' | '%') unaryExpr )* ;
unaryExpr   : ('!' | '-') unaryExpr | pipe;
pipe        : pipeLeft ( '|>' ident )* ;
pipeLeft    : postFix ;

ident       : IDENT ;
typeName    : TYPENAME ;
variant     : VARIANT ;

STRING
            : '"' ( ESC | ~["\\\r\n] )* '"'
            ;
fragment DIGIT
            : [0-9]
            ;
fragment ESC
            : '\\' [btnfr"'\\/]
            | '\\' 'u' HEX HEX HEX HEX
            ;

fragment HEX
            : [0-9a-fA-F]
            ;

IDENT       : [a-zA-Z_] [a-zA-Z_0-9]*
            ;
TYPENAME    : [A-Z] [a-zA-Z_0-9]* ;
VARIANT     : [A-Z] [a-zA-Z_0-9]* ;
WS          : [ \t\r\n]+ -> skip ;