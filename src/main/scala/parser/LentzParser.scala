package org.mikadadocs.lentz
package parser

import ast.*
import ast.TypeReference.{IntType, StringType, BoolType, MoneyType, NamedType, DeltaType}
import fastparse.*
import fastparse.{Whitespace, ParsingRun, P}

object LentzParser:

  // Custom whitespace (spaces, // line, and /* */ block with NESTING)
  // Replace your current whitespace with this:
  given Whitespace with
    def apply(ctx: ParsingRun[?]): P[Unit] =
      given ParsingRun[?] = ctx

      def space: P[Unit]  = P( CharsWhileIn(" \t\r\n") )

      def lineC: P[Unit] =
        P( "//" ~ CharsWhile(c => c != '\n' && c != '\r') ~ ( "\r\n" | "\n" | "\r" | End ) )

      // Disable whitespace skipping *inside* block comments to avoid interference
      def blockC: P[Unit] =
        given Whitespace = fastparse.NoWhitespace.noWhitespaceImplicit
        def body: P[Unit] = P( (blockC | ( !"*/" ~ AnyChar )).rep )
        P( "/*" ~ body ~ "*/" )

      P( (space | lineC | blockC).rep )

  // Explicit skipper used at file boundaries and between top-level decls.
  // Works with *no* ambient whitespace so it won't fight the global Whitespace.
  private def skip[$: P]: P[Unit] =
    given Whitespace = fastparse.NoWhitespace.noWhitespaceImplicit

    def space: P[Unit] = P(CharsWhileIn(" \t\r\n"))

    def lineC: P[Unit] = P("//" ~ CharsWhile(c => c != '\n' && c != '\r') ~ ("\r\n" | "\n" | "\r" | End))

    def blockC: P[Unit] =
      def body: P[Unit] = P((blockC | (!"*/" ~ AnyChar)).rep) // nested

      P("/*" ~ body ~ "*/")

    P((space | lineC | blockC).rep)
  // -------- Unicode-venlige identifikatorer ---------------------------------
  private def isLetter(ch: Char): Boolean = Character.isLetter(ch)

  private def isLowerStart(ch: Char): Boolean = isLetter(ch) && Character.isLowerCase(ch)

  private def isUpperStart(ch: Char): Boolean =
    (isLetter(ch) && Character.isUpperCase(ch)) || ch == 'Δ' // tillad Δ som type-start

  private def restChar(ch: Char): Boolean =
    Character.isLetterOrDigit(ch) || ch == '_' || Character.getType(ch) == Character.NON_SPACING_MARK

  private def ident[$: P]: P[String] =
    P(CharPred(isLowerStart) ~ CharsWhile(restChar).?).!.map(_.toString)

  private def typeName[$: P]: P[String] =
    P(CharPred(isUpperStart) ~ CharsWhile(restChar).?).!.map(_.toString)

  // -------- Small helpers ------------------------------------------------------
  private def spanOf[T](parser: => P[T])(using P[?]): P[(T, Span)] =
    P(Index ~ parser ~ Index).map { case (s, v, e) => (v, Span(s, e)) }

  private def lt[$: P] = P("<");

  private def gt[$: P] = P(">")

  private def colon[$: P] = P(":");

  private def comma[$: P] = P(",")

  private def lbrace[$: P] = P("{");

  private def rbrace[$: P] = P("}")

  // -------- Type references --------------------------------------------------
  def typeReference[$: P]: P[TypeReference] =
    P(
      "Int".!.map(_ => IntType)
        | "String".!.map(_ => StringType)
        | "Bool".!.map(_ => BoolType)
        | "Money".!.map(_ => MoneyType)
        // Δ prefix — must be before NamedType so it wins on inputs like "ΔOption<...>"
        | spanOf("Δ" ~ typeReference).map { case (t, sp) => DeltaType(t, sp) }
        | spanOf(typeName ~ typeArgs.?).map {
        case ((n, None), sp) => NamedType(n, Nil, sp)
        case ((n, Some(as)), sp) => NamedType(n, as, sp)
      }
    )

  private def typeArgs[$: P]: P[List[TypeReference]] =
    P(lt ~ typeReference.rep(sep = comma, min = 1) ~ gt).map(_.toList)

  // -------- Fields & TypeDecl ------------------------------------------------
  private def fieldDeclaration[$: P]: P[FieldDeclaration] =
    spanOf(ident ~ colon ~ typeReference).map { case ((n, t), sp) => FieldDeclaration(n, t, sp) }

  private def fieldDeclarations[$: P]: P[List[FieldDeclaration]] =
    P(fieldDeclaration.rep(sep = comma)).map(_.toList)

  def typeDeclaration[$: P]: P[TypeDeclaration] =
    spanOf("type" ~ typeName ~ lbrace ~ fieldDeclarations ~ rbrace)
      .map { case ((n, fs), sp) => TypeDeclaration(n, fs, sp) }

  def program[$: P]: P[Program] =
    P( skip ~ typeDeclaration.rep(sep = skip) ~ skip ~ End ).map(ds => Program(ds.toList))

  // -------- Public parse-helpers ----------------------------------------
  private def rootTypeDeclaration[$: P]: P[TypeDeclaration] =
    P( Pass ~ typeDeclaration )

  def parseTypeDeclaration(input: String): Parsed[TypeDeclaration] =
    parse(input, p => rootTypeDeclaration(using p), verboseFailures = true)

  def parseProgram(input: String): Parsed[Program] =
    parse(input, p => program(using p), verboseFailures = true)
