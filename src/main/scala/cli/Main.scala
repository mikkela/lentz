package org.mikadadocs.lentz
package cli

import parser.LentzParser
import fastparse.Parsed
import scala.io.Source
import java.nio.file.{Files, Paths}

object Main:

  final case class LineCol(line: Int, col: Int) // 1-baseret

  private def lineStarts(s: String): Array[Int] =
    val arr = scala.collection.mutable.ArrayBuffer(0)
    var i = 0
    while i < s.length do
      val c = s.charAt(i)
      if c == '\n' then arr += (i + 1)
      i += 1
    arr.toArray

  private def lineColAt(idx: Int, starts: Array[Int]): LineCol =
    // binærsøgning efter seneste linjestart <= idx
    var lo = 0; var hi = starts.length - 1
    while lo <= hi do
      val mid = (lo + hi) >>> 1
      if starts(mid) <= idx then lo = mid + 1 else hi = mid - 1
    val lineIdx = math.max(0, hi)
    val col = idx - starts(lineIdx) + 1
    LineCol(lineIdx + 1, col)

  private def extractLine(s: String, lineNo: Int, starts: Array[Int]): String =
    val start = starts(lineNo - 1)
    val end =
      if lineNo < starts.length then starts(lineNo) - 1
      else s.length
    s.substring(start, end).replace("\r", "")

  private def printFailure(input: String, f: Parsed.Failure): Unit =
    val starts = lineStarts(input)
    val pos    = lineColAt(f.index, starts)
    val line   = extractLine(input, pos.line, starts)
    val caret  = " " * (pos.col - 1) + "^"
    Console.err.println(s"Parse error at ${pos.line}:${pos.col}: ${f.msg}")
    Console.err.println(line)
    Console.err.println(caret)

  def main(args: Array[String]): Unit =
    val input =
      if args.nonEmpty && Files.exists(Paths.get(args(0))) then
        Source.fromFile(args(0)).mkString
      else
        Source.stdin.getLines().mkString("\n")

    LentzParser.parseProgram(input) match
      case Parsed.Success(ast, _) =>
        println(ast)
      case f: Parsed.Failure =>
        printFailure(input, f)
        sys.exit(1)