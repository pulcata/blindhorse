package io.pulque.chessito

import io.pulque.chessito.pieces.Piece
import io.pulque.chessito.pieces.Rook
import io.pulque.chessito.pieces.Square
import io.pulque.chessito.utils.Bits
import io.pulque.chessito.utils.Constants.SQUARE
import io.pulque.chessito.utils.Squares
import io.pulque.chessito.utils.validateFen

/*
 * @author savirdev on 2019-12-04
 */

private const val empty = -1
private const val black = "b"
private const val white = "w"
private const val defaultPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

class Chessito(fen: String? = null) {

    var PAWN = "p"
    var KNIGHT = "n"
    var BISHOP = "b"
    var ROOK = "r"
    var QUEEN = "q"
    var KING = "k"

    var SYMBOLS = "pnbrqkPNBRQK"

    var POSSIBLE_RESULTS = listOf("1-0", "0-1", "1/2-1/2", "*")

    var PAWN_OFFSETS = HashMap<String, List<Int>>().apply {
        this[black] = listOf(16, 32, 17, 15)
        this[white] = listOf(-16, -32, -17, -15)
    }

    var PIECE_OFFSETS = HashMap<String, List<Int>>().apply {
        this["n"] = listOf(-18, -33, -31, -14, 18, 33, 31, 14)
        this["b"] = listOf(-17, -15, 17, 15)
        this["q"] = listOf(-16, 1, 16, -1)
        this["r"] = listOf(-17, -16, -15, 1, 17, 16, 15, -1)
        this["k"] = listOf(-17, -16, -15, 1, 17, 16, 15, -1)
    }

    // prettier-ignore
    var ATTACKS = listOf(
        20, 0, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0, 0,20, 0,
        0,20, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0,20, 0, 0,
        0, 0,20, 0, 0, 0, 0, 24,  0, 0, 0, 0,20, 0, 0, 0,
        0, 0, 0,20, 0, 0, 0, 24,  0, 0, 0,20, 0, 0, 0, 0,
        0, 0, 0, 0,20, 0, 0, 24,  0, 0,20, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,20, 2, 24,  2,20, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 2,53, 56, 53, 2, 0, 0, 0, 0, 0, 0,
        24,24,24,24,24,24,56,  0, 56,24,24,24,24,24,24, 0,
        0, 0, 0, 0, 0, 2,53, 56, 53, 2, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,20, 2, 24,  2,20, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0,20, 0, 0, 24,  0, 0,20, 0, 0, 0, 0, 0,
        0, 0, 0,20, 0, 0, 0, 24,  0, 0, 0,20, 0, 0, 0, 0,
        0, 0,20, 0, 0, 0, 0, 24,  0, 0, 0, 0,20, 0, 0, 0,
        0,20, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0,20, 0, 0,
        20, 0, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0, 0,20
    )

    // prettier-ignore
    var RAYS = listOf(
        17,  0,  0,  0,  0,  0,  0, 16,  0,  0,  0,  0,  0,  0, 15, 0,
        0, 17,  0,  0,  0,  0,  0, 16,  0,  0,  0,  0,  0, 15,  0, 0,
        0,  0, 17,  0,  0,  0,  0, 16,  0,  0,  0,  0, 15,  0,  0, 0,
        0,  0,  0, 17,  0,  0,  0, 16,  0,  0,  0, 15,  0,  0,  0, 0,
        0,  0,  0,  0, 17,  0,  0, 16,  0,  0, 15,  0,  0,  0,  0, 0,
        0,  0,  0,  0,  0, 17,  0, 16,  0, 15,  0,  0,  0,  0,  0, 0,
        0,  0,  0,  0,  0,  0, 17, 16, 15,  0,  0,  0,  0,  0,  0, 0,
        1,  1,  1,  1,  1,  1,  1,  0, -1, -1,  -1,-1, -1, -1, -1, 0,
        0,  0,  0,  0,  0,  0,-15,-16,-17,  0,  0,  0,  0,  0,  0, 0,
        0,  0,  0,  0,  0,-15,  0,-16,  0,-17,  0,  0,  0,  0,  0, 0,
        0,  0,  0,  0,-15,  0,  0,-16,  0,  0,-17,  0,  0,  0,  0, 0,
        0,  0,  0,-15,  0,  0,  0,-16,  0,  0,  0,-17,  0,  0,  0, 0,
        0,  0,-15,  0,  0,  0,  0,-16,  0,  0,  0,  0,-17,  0,  0, 0,
        0,-15,  0,  0,  0,  0,  0,-16,  0,  0,  0,  0,  0,-17,  0, 0,
        -15,  0,  0,  0,  0,  0,  0,-16,  0,  0,  0,  0,  0,  0,-17
    )

    var SHIFTS = HashMap<String, Int>().apply {
        this["p"] = 0
        this["n"] = 1
        this["b"] = 2
        this["r"] = 3
        this["q"] = 4
        this["k"] = 5
    }

    val normal = "NORMAL"
    val capture = "CAPTURE"
    val bigPawn = "BIG_PAWN"
    val epCapture = "EP_CAPTURE"
    val promotion = "PROMOTION"
    val kSideCastle = "KSIDE_CASTLE"
    val qSideCastle = "QSIDE_CASTLE"

    var FLAGS = HashMap<String, String>().apply {
        this[normal] = "n"
        this[capture] = "c"
        this[bigPawn] = "b"
        this[epCapture] = "e"
        this[promotion] = "p"
        this[kSideCastle] = "k"
        this[qSideCastle] = "q"
    }

    var BITS = HashMap<String, Int>().apply {
        this[normal] = 1
        this[capture] = 2
        this[bigPawn] = 4
        this[epCapture] = 8
        this[promotion] = 16
        this[kSideCastle] = 32
        this[qSideCastle] = 64
    }

    var RANK_1 = 7
    var RANK_2 = 6
    var RANK_3 = 5
    var RANK_4 = 4
    var RANK_5 = 3
    var RANK_6 = 2
    var RANK_7 = 1
    var RANK_8 = 0

    var Rooks = HashMap<String,List<Rook>>().apply {
        this["w"] = listOf(Rook(Squares.a1, Bits.qSideCastle), Rook(Squares.h1, Bits.kSideCastle))
        this["b"] = listOf(Rook(Squares.a8, Bits.qSideCastle), Rook(Squares.h8, Bits.kSideCastle))
    }

    private var board : Array<Square?> = arrayOf()
    private var kings = HashMap<String, Int>().apply {
        this[white] = empty
        this[black] = empty
    }
    private var turn = white
    private var castling = HashMap<String, Int>().apply {
        this[white] = 0
        this[black] = 0
    }
    private var epSquare = empty
    private var halfMoves = 0
    private var moveNumber = 1
    private var history = listOf<String>()
    private var header = HashMap<String, String>()

    init {

        initValues()

        fen?.let {
            load(it)
        } ?: load(defaultPosition)

    }

    fun load(fen: String, keepHeaders: Boolean = false) : Boolean{
        var tokens = fen.split(" ")
        var position = tokens.first()
        var square = 0

        if (!fen.validateFen()){
            return false
        }

        clear(keepHeaders)

        for (piece in position.toCharArray()){
            if (piece == '/'){
                square += 8
            }else if (piece.isDigit()){
                square += piece.toInt()
            }else{
                val color = if (piece < 'a') white else black

            }
        }

    }

    private fun put(piece: Piece, square: Int) : Boolean{

        /* check for piece */
        if (!SYMBOLS.contains(piece.type)){
            return false
        }

        if (piece.type == KING && !(kings[piece.color] == empty || kings[piece.color] == square)){
            return false
        }

        board[square] = piece
        if (piece.type == KING){
            kings[piece.color] = square
        }

        updateSetup(generateFen())

        return true

    }

    private fun clear(keepHeaders: Boolean = false){
        initValues()
        if (!keepHeaders){
            header = HashMap()
            updateSetup()
        }
    }

    private fun initValues(){
        board = arrayOf()
        kings = HashMap<String, Int>().apply {
            this[white] = empty
            this[black] = empty
        }

        turn = white
        castling = HashMap<String, Int>().apply {
            this[white] = 0
            this[black] = 0
        }
        epSquare = empty
        halfMoves = 0
        moveNumber = 1
        history = listOf()
    }

    private fun updateSetup(fen: String){
        if (history.count() > 0)
            return

        if (fen !== defaultPosition) {
            header["SetUp"] = "1"
            header["FEN"] = fen

        } else {
            header.remove("SetUp")
            header.remove("FEN")
        }
    }

    private fun generateFen(): String{
        var empty = 0
        var fen : StringBuilder = StringBuilder()

        for (i in Squares.a8..Squares.h1){
            board[i]?.let {
                empty += 1
            } ?: run {
                if (empty > 0) {
                    fen.append(empty)
                    empty = 0
                }

                val color = board[i]?.color
                val piece = board[i]?.piece

                piece?.let {
                    fen.append(if (color == white) piece.toUpperCase() else piece.toLowerCase())
                }
            }

            if ((i + 1) and i < 0x88) {
                if (empty > 0) {
                    fen.append(empty)
                }

                if (i != Squares.h1) {
                    fen.append("/")
                }

                empty = 0
                i += 8
            }
        }

        var cflags = StringBuilder()
        if (castling[white] == Bits.kSideCastle) {
            cflags.append("K")
        }
        if (castling[white] == Bits.qSideCastle) {
            cflags.append("Q")
        }
        if (castling[black] == Bits.kSideCastle) {
            cflags.append("k")
        }
        if (castling[black] == Bits.qSideCastle) {
            cflags.append("q")
        }

        /* do we have an empty castling flag? */
        if (cflags.isEmpty()) cflags.append("-")

        val epFlags = if(epSquare == empty) "-"
        var epflags = if(epSquare == empty) "-" else algebraic(epSquare)

        return StringBuilder().append(fen).append(" ").append(turn).append(" ").append(cflags).append(" ").append(halfMoves).append(" ").append(moveNumber).toString()
    }

    private fun algebraic(epSquare: Int) : String{
        val f = file(epSquare)
        val r = rank(epSquare)
        return "abcdefgh".substring(f..f + 1) + "87654321".substring(r.. r + 1)
    }

    private fun rank(i: Int) : Int {
        return i shr 4
    }


    private fun file(i: Int) : Int {
        return i and 15
    }
}