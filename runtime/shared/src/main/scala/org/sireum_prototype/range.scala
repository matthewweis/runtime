/*
 Copyright (c) 2017, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum_prototype

import scala.meta._


object range {
  def q(index: Boolean, minOpt: Option[BigInt], maxOpt: Option[BigInt], name: String): Term.Block = {
    def unsupported(op: Predef.String) = Lit.String(s"Unsupported $name operation '$op'")
    assert(!index || minOpt.nonEmpty)
    val typeName = Type.Name(name)
    val termName = Term.Name(name)
    val ctorName = Ctor.Name(name)
    val nameStr = Lit.String(name)
    val signed = minOpt.forall(_ < 0)
    def min = Lit.String(minOpt.map(_.toString).getOrElse("0"))
    def max = Lit.String(maxOpt.map(_.toString).getOrElse("0"))
    val minUnsupported = unsupported("Min")
    val maxUnsupported = unsupported("Max")
    val minErrorMessage = Lit.String(s" is less than $name.Min (${min.value})")
    val maxErrorMessage = Lit.String(s" is greater than $name.Max (${max.value})")
    Term.Block(List(
      q"""final class $typeName(val value: Z.MP) extends AnyVal with Z.Range[$typeName] {
            @inline def name: Predef.String = $termName.name
            @inline def Min: $typeName = $termName.Min
            @inline def Max: $typeName = $termName.Max
            @inline def isIndex: scala.Boolean = $termName.isIndex
            @inline def isSigned: scala.Boolean = $termName.isSigned
            @inline def hasMin: scala.Boolean = $termName.hasMin
            @inline def hasMax: scala.Boolean = $termName.hasMax
            def make(v: Z.MP): $typeName = $termName(v)
          }""",
      q"""object $termName {
            val name: Predef.String = $nameStr
            lazy val Min: $typeName = if (hasMin) new $ctorName(Z.MP($min)) else halt($minUnsupported)
            lazy val Max: $typeName = if (hasMax) new $ctorName(Z.MP($max)) else halt($maxUnsupported)
            def isIndex: scala.Boolean = ${Lit.Boolean(index)}
            def isSigned: scala.Boolean = ${Lit.Boolean(signed)}
            def hasMin: scala.Boolean = ${Lit.Boolean(minOpt.nonEmpty)}
            def hasMax: scala.Boolean = ${Lit.Boolean(maxOpt.nonEmpty)}
            private def check(v: scala.BigInt): scala.BigInt = {
              if (hasMin) assert(Min.toBigInt <= v, v + $minErrorMessage)
              if (hasMax) assert(v <= Max.toBigInt, v + $maxErrorMessage)
              v
            }
            def apply(value: scala.Int): $typeName = {
              check(scala.BigInt(value))
              new $ctorName(Z.MP(value))
            }
            def apply(value: scala.Long): $typeName = {
              check(scala.BigInt(value))
              new $ctorName(Z.MP(value))
            }
            def apply(value: String): $typeName = new $ctorName(Z.MP(check(scala.BigInt(value.value))))
            def apply(value: Z): $typeName = value match {
              case value: Z.MP =>
                check(value.toBigInt)
                new $ctorName(value)
              case _ => halt(s"Unsupported $$name creation from $${value.name}.")
            }
            def unapply(n: $typeName): scala.Option[Z] = scala.Some(n.value)
            object Int {
              def unapply(n: $typeName): scala.Option[scala.Int] =
                if (scala.Int.MinValue <= n.value && n.value <= scala.Int.MaxValue) scala.Some(n.value.toBigInt.toInt)
                else scala.None
            }
            object Long {
              def unapply(n: $typeName): scala.Option[scala.Long] =
                if (scala.Long.MinValue <= n.value && n.value <= scala.Long.MaxValue) scala.Some(n.value.toBigInt.toLong)
                else scala.None
            }
            object String {
              def unapply(n: $typeName): scala.Option[Predef.String]= scala.Some(n.value.toString)
            }
          }"""
    ))
  }
}

class range(min: Option[BigInt] = None,
            max: Option[BigInt] = None) extends scala.annotation.StaticAnnotation {
  inline def apply(tree: Any): Any = meta {
    tree match {
      case q"class $tname" =>
        val q"new range(..$args)" = this
        var minOpt: Option[BigInt] = None
        var maxOpt: Option[BigInt] = None
        for (arg <- args) {
          arg match {
            case arg"min = ${exp: Term}" => minOpt = helper.extractInt(exp)
            case arg"max = ${exp: Term}" => maxOpt = helper.extractInt(exp)
            case _ => abort(arg.pos, s"Invalid Slang @range argument: ${arg.syntax}")
          }
        }
        if (minOpt.isEmpty && maxOpt.isEmpty) abort(tree.pos, s"Slang @range should have either a minimum, a maximum, or both.")
        val result = range.q(index = false, minOpt, maxOpt, tname.value)
        //println(result)
        result
      case _ => abort(tree.pos, s"Invalid Slang @range on: ${tree.syntax}")
    }
  }
}
