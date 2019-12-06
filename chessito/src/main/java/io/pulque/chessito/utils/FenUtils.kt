package io.pulque.chessito.utils

import android.util.Log
import java.lang.RuntimeException

/*
 * @author savirdev on 2019-12-05
 */

private const val sixSpaceDelimitedFieldsError =
    "FEN string must contain six space-delimited fields."
private const val sixthFieldMustBePositiveError =
    "6th field (move number) must be a positive integer."
private const val fifthFieldMustBeNonNegativeError =
    "6th field (move number) must be a positive integer."
private const val forthFieldIsInvalidError = "4th field (en-passant square) is invalid."
private const val thirdFieldIsInvalidError = "3rd field (castling availability) is invalid."
private const val secondFieldIsInvalidError = "2nd field (side to move) is invalid."
private const val firstFieldIncompleteRowsError =
    "1st field (piece positions) does not contain 8 '/'-delimited rows."
private const val firstFieldIsInvalidConsecutiveError =
    "1st field (piece positions) is invalid [consecutive numbers]."
private const val firstFieldIsInvalidPieceError =
    "1st field (piece positions) is invalid [invalid piece]."
private const val firstFieldIsInvalidTooLargeRowError =
    "1st field (piece positions) is invalid [row too large]."
private const val illegalEnpassantSquare = "Illegal en-passant square"

fun String.validateFen(): Boolean {

    /* 1st criterion: 6 space-seperated fields? */
    val tokens = split(" ")
    if (tokens.isEmpty()) {
        return showError(sixSpaceDelimitedFieldsError)
    }

    /* 2nd criterion: move number field is a integer value > 0? */
    if (tokens[5].toInt() <= 0) {
        return showError(sixthFieldMustBePositiveError)
    }

    /* 3rd criterion: half move counter is an integer >= 0? */
    if (tokens[4].toInt() < 0) {
        return showError(fifthFieldMustBeNonNegativeError)
    }

    /* 4th criterion: 4th field is a valid e.p.-string? */
    if (!Regex("^(-|[abcdefgh][36])\$").matches(tokens[3])) {
        return showError(forthFieldIsInvalidError)
    }

    /* 5th criterion: 3th field is a valid castle-string? */
    if (!Regex("^(KQ?k?q?|Qk?q?|kq?|q|-)\$").matches(tokens[2])) {
        return showError(thirdFieldIsInvalidError)
    }

    /* 6th criterion: 2nd field is "w" (white) or "b" (black)? */
    if (!Regex("^([wb])$").matches(tokens[1])) {
        return showError(secondFieldIsInvalidError)
    }

    /* 7th criterion: 1st field contains 8 rows? */
    val rows = tokens[0].split("/")
    if (rows.count() != 8) {
        return showError(firstFieldIncompleteRowsError)
    }

    /* 8th criterion: every row is valid? */
    rows.forEach { row ->

        /* check for right sum of fields AND not two numbers in succession */
        var sumFields = 0
        var previousWasNumber = false

        row.forEach { character ->
            if (character.isDigit()) {
                if (previousWasNumber) {
                    return showError(firstFieldIsInvalidConsecutiveError)
                }

                sumFields += character.toInt()
                previousWasNumber = true
            }else{
                if (!Regex("^[prnbqkPRNBQK]\$").matches(character.toString())){
                    return showError(firstFieldIsInvalidPieceError)
                }

                sumFields += 1
                previousWasNumber = false
            }
        }

        if (sumFields != 8){
            return showError(firstFieldIsInvalidTooLargeRowError)
        }
    }

    if ((tokens[3].toCharArray()[1] == '3' && tokens[1] == "w") || (tokens[3].toCharArray()[1] == '6' && tokens[1] == "b")){
        return showError(illegalEnpassantSquare)
    }

    return true

}

private fun showError(error: String) : Boolean{

    Log.i("FenUtils", error)
    return false
}