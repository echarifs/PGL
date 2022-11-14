lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@header {
    import fr.ensimag.deca.syntax.DecaRecognitionException;
}


@members {
}

// Deca lexer rules.

// Mots réservés
ASM : ('asm');
CLASS : ('class');
EXTENDS : ('extends');
ELSE : ('else');
IF : ('if');
FALSE : ('false');
INSTANCEOF : ('instanceof');
NEW : ('new');
NULL : ('null');
READINT : ('readInt');
READFLOAT : ('readFloat');
PRINT : ('print');
PRINTLN : ('println');
PRINTLNX : ('printlnx');
PRINTX : ('printx');
PROTECTED : ('protected');
RETURN : ('return');
THIS : ('this');
TRUE : ('true');
WHILE : ('while');

// Identificateurs
fragment DIGIT: '0' .. '9';
fragment LETTER: ('a' .. 'z' | 'A' .. 'Z');
IDENT: (LETTER | '$' | '_') (LETTER | DIGIT | '$' | '_')*;

// Symboles spéciaux
PLUS : ('+');
MINUS : ('-');
TIMES : ('*');
EQUALS : ('=');
SLASH : ('/');
GT : ('>');
LT : ('<');
PERCENT : ('%');
DOT : ('.');
COMMA : (',');
OPARENT : ('(');
CPARENT : (')');
OBRACE : ('{');
CBRACE : ('}');
EXCLAM : ('!');
SEMI : (';');
EQEQ : ('==');
NEQ: ('!=');
GEQ : ('>=');
LEQ : ('<=');
AND : ('&&');
OR : ('||');

// Littéraux entiers
fragment POSITIVE_DIGIT: '1' .. '9';
INT: ('0' | POSITIVE_DIGIT DIGIT*);

// Littéraux flottants
fragment NUM : DIGIT+;
fragment SIGN: ('+' | '-' | /* epsilon */);
fragment EXP : ('E' | 'e') SIGN NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f' | /* epsilon */) ;
fragment DIGITHEX : '0' .. '9' | 'A' .. 'F' | 'a' .. 'f';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' | /* epsilon */) ;
FLOAT : FLOATDEC | FLOATHEX;

// Chaînes de caractères
fragment STRING_CAR: ~('"' | '\\' | '\n');
STRING: '"' (STRING_CAR | '\\"' | '\\\\')* '"'; //CONVENTION UNIMPLEMENTED
MULTI_LINE_STRING: '"' (STRING_CAR | '\n' | '\\"' | '\\\\')* '"'; //CONVENTION UNIMPLEMENTED

// Commentaires
fragment COMMENT_CLASSIC : '/*' .*? '*/';
fragment COMMENT_MONOLINE : '//' .*? ('\n' | EOF);
fragment COMMENT : COMMENT_CLASSIC | COMMENT_MONOLINE;

// Séparateurs
WS: ( ' ' | '\t' | '\r' | '\n' | COMMENT ) {
              skip(); // avoid producing a token
          };

// Inclusion de fichier 
fragment FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+;
INCLUDE: '#include' (' ')* '"' FILENAME '"' {doInclude(getText()); };

DEFAULT: .  { if(true){
                 throw new DecaRecognitionException(this, getInputStream(), "The token is not recognized. " );}
            };