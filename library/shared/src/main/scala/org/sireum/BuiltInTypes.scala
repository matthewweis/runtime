/*
 Copyright (c) 2017-2022, Robby, Kansas State University
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

package org.sireum

import org.sireum.$internal._

import scala.language.implicitConversions


trait Immutable extends Any with ImmutableMarker {

  final protected def $cannotMixImmutableAndMutable: scala.Nothing = halt("")

  protected def $hasEquals: scala.Boolean = false

  protected def $hasString: scala.Boolean = false

  def string: String

}


trait Number extends Any with Immutable


trait EnumSig extends Immutable {
  def numOfElements: Z

  def string: String = halt("Unsupported Enum operation 'string'.")
}

trait DatatypeSig extends Immutable with DatatypeMarker {
  def $content: scala.Seq[(_root_.java.lang.String, scala.Any)]

  def hash: Z = hashCode

  override def string: String = halt("Infeasible")
}


trait RichSig extends Immutable


trait Mutable extends Any with MutableMarker {

  final protected def $cannotMixImmutableAndMutable: scala.Nothing = halt("")

  def string: String

  protected def $hasEquals: scala.Boolean = false

  protected def $hasString: scala.Boolean = false

}


trait RecordSig extends Mutable {

  private var $isOwned: Boolean = false

  final override def $owned: Boolean = $isOwned

  final override def $owned_=(b: Boolean): this.type = {
    $isOwned = b
    this
  }

  def $content: scala.Seq[(_root_.java.lang.String, scala.Any)]

  def hash: Z = hashCode

  override def string: String = halt("Infeasible")
}
