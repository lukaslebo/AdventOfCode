package util

import java.math.BigInteger
import java.security.MessageDigest

/**
 * Converts string to util.md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
