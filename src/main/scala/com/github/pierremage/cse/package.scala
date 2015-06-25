package com.github.pierremage

import java.io.InputStream

package object cse {

  type CseSuccess = InputStream

  type CseError = (Int, String)

}
