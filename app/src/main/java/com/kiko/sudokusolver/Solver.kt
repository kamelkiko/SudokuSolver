package com.kiko.sudokusolver

class Solver {
    private var board: Array<IntArray> = Array(9) { IntArray(9) }
    private var emptyBoxIndex: ArrayList<ArrayList<Any>>? = null

    private var selectedRow = -1
    private var selectedColumn = -1


    init {
        clearBoard()
    }

    fun getEmptyBoxIndexes() {
        for (r in 0..8) {
            for (c in 0..8) {
                if (board[r][c] == 0) {
                    emptyBoxIndex!!.add(ArrayList())
                    emptyBoxIndex!![emptyBoxIndex!!.size - 1].add(r)
                    emptyBoxIndex!![emptyBoxIndex!!.size - 1].add(c)
                }
            }
        }
    }

    fun isError(): Boolean {
        for (r in 0..8) {
            for (c in 0..8) {
                if (board[r][c] < 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun check(row: Int, col: Int): Boolean {
        if (board[row][col] > 0) {
            for (i in 0..8) {
                if (board[i][col] == board[row][col] && row != i) {
                    return false
                }
                if (board[row][i] == board[row][col] && col != i) {
                    return false
                }
            }
            val boxRow = row / 3
            val boxCol = col / 3
            for (r in boxRow * 3 until boxRow * 3 + 3) {
                for (c in boxCol * 3 until boxCol * 3 + 3) {
                    if (board[r][c] == board[row][col] && row != r && col != c) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun solve(display: SudokuBoard): Boolean {
        var row = -1
        var col = -1
        for (r in 0..8) {
            for (c in 0..8) {
                if (board[r][c] == 0) {
                    row = r
                    col = c
                    break
                }
            }
        }
        if (row == -1 || col == -1) {
            return true
        }
        for (i in 1..9) {
            board[row][col] = i
            display.invalidate()
            if (check(row, col)) {
                if (solve(display)) {
                    return true
                }
            }
            board[row][col] = 0
        }
        return false
    }

    fun clearBoard() {
        for (r in 0..8) {
            for (c in 0..8) {
                board[r][c] = 0
            }
        }
        emptyBoxIndex = ArrayList()
    }

    fun setNumberPos(num: Int) {
        if (selectedRow != -1 && selectedColumn != -1) {
            if (board[selectedRow - 1][selectedColumn - 1] == num) {
                board[selectedRow - 1][selectedColumn - 1] = 0
            } else {
                board[selectedRow - 1][selectedColumn - 1] = num
                if (!check(selectedRow - 1, selectedColumn - 1)) {
                    board[selectedRow - 1][selectedColumn - 1] = -num
                }
            }
        }
    }

    @JvmName("getBoard1")
    fun getBoard(): Array<IntArray> {
        return board
    }

    @JvmName("getEmptyBoxIndex1")
    fun getEmptyBoxIndex(): ArrayList<ArrayList<Any>>? {
        return emptyBoxIndex
    }

    fun getSelectedRow(): Int {
        return selectedRow
    }

    fun getSelectedColumn(): Int {
        return selectedColumn
    }

    fun setSelectedRow(r: Int) {
        selectedRow = r
    }

    fun setSelectedColumn(c: Int) {
        selectedColumn = c
    }
}