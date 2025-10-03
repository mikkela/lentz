package org.mikadadocs.lentz
package parser

import munit.FunSuite
import fastparse._
import ast.*
import ast.TypeReference.{NamedType, StringType, IntType, BoolType, MoneyType}

final class LentzParserSpec extends FunSuite:

  test("parse simple type decl with one field") {
    val code = "type Foo { x: Int }"
    val res = LentzParser.parseTypeDeclaration(code)
    assert(res.isInstanceOf[Parsed.Success[?]])
    val TypeDeclaration(name, fields, _) = res.get.value
    assertEquals(name, "Foo")
    fields match
      case List(FieldDeclaration("x", IntType, _)) => ()
      case other => fail(s"Unexpected fields: $other")
  }

  test("parse generic: List<String>") {
    val code = "type Foo { xs: List<String> }"
    val res  = LentzParser.parseTypeDeclaration(code)
    val TypeDeclaration(_, fields, _) = res.get.value
    fields.head match
      case FieldDeclaration("xs", NamedType("List", List(StringType), _), _) => ()
      case other => fail(s"Unexpected first field: $other")
  }

  test("parse generic: Map<Bool, Money>") {
    val code = "type Foo { xs: Map<Bool, Money> }"
    val res = LentzParser.parseTypeDeclaration(code)
    val TypeDeclaration(_, fields, _) = res.get.value
    fields.head match
      case FieldDeclaration("xs", NamedType("Map", List(BoolType, MoneyType), _), _) => ()
      case other => fail(s"Unexpected first field: $other")
  }

  test("parse nested generics: Map<String, List<Int>>") {
    val code = "type Foo { m: Map<String, List<Int>> }"
    val res  = LentzParser.parseTypeDeclaration(code)
    val TypeDeclaration(_, fields, _) = res.get.value
    fields.head match
      case FieldDeclaration(
      "m",
      NamedType("Map", List(StringType, NamedType("List", List(IntType), _)), _),
      _
      ) => ()
      case other => fail(s"Unexpected first field: $other")
  }

  test("program: multiple type decls") {
    val code =
      """type Foo { a: Int }
        |type Bar { b: String, c: List<Int> }
        |""".stripMargin
    val res  = LentzParser.parseProgram(code)
    val Program(ds) = res.get.value
    assertEquals(ds.length, 2)
  }

  test("line & block comments (nested) + whitespace") {
    val code =
      """// top comment
        |/* outer
        |   /* inner */ still outer */
        |type Foo { x: Int, y: List<String> } // trailing
        |""".stripMargin
    val res = LentzParser.parseProgram(code)
    assert(res.isInstanceOf[Parsed.Success[?]])
    val Program(ds) = res.get.value
    assertEquals(ds.length, 1)
  }

  test("Unicode typename start: ΔVector<String>") {
    val code = "type T { xs: ΔVector<String> }"
    val res  = LentzParser.parseTypeDeclaration(code)   // <- correct method name
    val TypeDeclaration(_, fields, _) = res.get.value
    fields.head match
      case FieldDeclaration("xs", NamedType("ΔVector", List(StringType), _), _) => ()
      case other => fail(s"Unexpected first field: $other")
  }

  test("spans captured on TypeDeclaration / FieldDeclaration / NamedType") {
    val code = "type Foo { xs: List<String> }"
    val res  = LentzParser.parseTypeDeclaration(code)
    val td @ TypeDeclaration(_, fields, tdSpan) = res.get.value

    val (nnSpan, fSpan) = fields.head match
      case FieldDeclaration(_, NamedType(_, _, nn), f) => (nn, f)
      case other => fail(s"Unexpected first field: $other")

    assert(tdSpan.start <= fSpan.start && fSpan.end <= tdSpan.end)
    assert(nnSpan.start   >= tdSpan.start && nnSpan.end   <= tdSpan.end)
  }

  test("error shows failure result (caret printed by CLI)") {
    val code =
      """type X {
        |  a: Int,
        |  b: List<String>
        |  oops
        |}""".stripMargin
    val res = LentzParser.parseProgram(code)
    assert(res.isInstanceOf[Parsed.Failure])
  }

