package org.mikadadocs.lentz
package ast

/** 0-based offset in the entire string. */
final case class Span(start: Int, end: Int)

sealed trait TypeReference
object TypeReference:
  case object IntType extends TypeReference
  case object StringType extends TypeReference
  // Named T with optional type args: e.g. List<String>, Foo<A,B>
  final case class NamedType(name: String, args: List[TypeReference] = Nil, span: Span) extends TypeReference

final case class FieldDeclaration(name: String, tpe: TypeReference, span: Span)

sealed trait Declaration
final case class TypeDeclaration(name: String, fields: List[FieldDeclaration], span: Span) extends Declaration

// Program node til at holde flere declarations, hvis du senere vil udvide
final case class Program(decls: List[Declaration])
