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

import org.sireum_prototype.$internal.ISMarker

object IS {

  final class Gen[I <: Z, V <: Immutable](array: Array[Any], length: Int) extends IS[I, V] {
    def apply(index: I): V = array(index.toIndex.toBigInt.toInt).asInstanceOf[V]
    def size: Z = length
    def elements: scala.Seq[V] = array.slice(0, length).map(_.asInstanceOf[V])
    lazy val hash: Z = elements.hashCode
    def isEqual(other: Immutable): B = other match {
      case other: IS[_, _] => elements == other.elements
      case _ => F
    }
    def string: String = toString
    override def toString: Predef.String = {
      val sb = new java.lang.StringBuilder
      sb.append('[')
      if (length > 0) {
        sb.append(array(0).toString)
        for (i <- 1 until length) {
          sb.append(", ")
          sb.append(array(i).toString)
        }
      }
      sb.append(']')
      sb.toString
    }
  }

  def apply[I <: Z, V <: Immutable](args: V*): IS[I, V] =
    new Gen[I, V](Array(args: _*), args.length)
}

sealed trait IS[I <: Z, V <: Immutable] extends Immutable with ISMarker {

  def apply(index: I): V

  def size: Z

  def elements: scala.Seq[V]

}

