package a

import sun.security.krb5.internal.KDCOptions.with

fun main() = with(System.`in`.bufferedReader()) {
    val A = readLine().toInt()
    for (i in 0..A) {
           println('a')//s
        System.err.println(i)
    }
}