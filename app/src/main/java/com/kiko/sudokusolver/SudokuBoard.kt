package com.kiko.sudokusolver

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.ceil

@SuppressLint("ViewConstructor")
class SudokuBoard(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var boardColor = 0
    private var cellFillColor = 0
    private var cellsHighlightColor = 0

    private var letterColor = 0
    private var letterColorSolve = 0
    private var letterColorError = 0

    private val boardColorPaint = Paint()
    private val cellFillColorPaint = Paint()
    private val cellsHighlightColorPaint = Paint()

    private val letterPaint = Paint()
    private val letterPaintBounds = Rect()
    private var cellSize = 0

    private val solver: Solver = Solver()

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.SudokuBoard,
            0, 0
        )
        try {
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor, 0)
            cellFillColor = a.getInteger(R.styleable.SudokuBoard_cellFillColor, 0)
            cellsHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellsHighlightColor, 0)
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0)
            letterColorSolve = a.getInteger(R.styleable.SudokuBoard_letterColorSolve, 0)
            letterColorError = a.getInteger(R.styleable.SudokuBoard_letterColorError, 0)
        } finally {
            a.recycle()
        }
    }

    override fun onMeasure(width: Int, height: Int) {
        super.onMeasure(width, height)
        val dimension = this.measuredWidth.coerceAtMost(this.measuredHeight)
        cellSize = dimension / 9
        setMeasuredDimension(dimension, dimension)
    }

    override fun onDraw(canvas: Canvas) {
        boardColorPaint.style = Paint.Style.STROKE
        boardColorPaint.strokeWidth = 16f
        boardColorPaint.color = boardColor
        boardColorPaint.isAntiAlias = true
        cellFillColorPaint.style = Paint.Style.FILL
        boardColorPaint.isAntiAlias = true
        cellFillColorPaint.color = cellFillColor
        cellsHighlightColorPaint.style = Paint.Style.FILL
        boardColorPaint.isAntiAlias = true
        cellsHighlightColorPaint.color = cellsHighlightColor
        letterPaint.style = Paint.Style.FILL
        letterPaint.isAntiAlias = true
        letterPaint.color = letterColor
        colorCell(canvas, solver.getSelectedRow(), solver.getSelectedColumn())
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), boardColorPaint)
        drawBoard(canvas)
        drawNumbers(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val isValid: Boolean
        val x = event.x
        val y = event.y
        val action = event.action
        isValid = if (action == MotionEvent.ACTION_DOWN) {
            solver.setSelectedRow(ceil((y / cellSize).toDouble()).toInt())
            solver.setSelectedColumn(ceil((x / cellSize).toDouble()).toInt())
            true
        } else {
            false
        }
        return isValid
    }

    private fun drawNumbers(canvas: Canvas) {
        letterPaint.textSize = cellSize.toFloat()
        for (r in 0..8) {
            for (c in 0..8) {
                if (solver.getBoard().get(r).get(c) !== 0) {
                    var text: String
                    if (solver.getBoard()[r][c] < 0) {
                        text = (solver.getBoard()[r][c] * -1).toString()
                        letterPaint.color = letterColorError
                    } else {
                        letterPaint.color = letterColor
                        text = solver.getBoard()[r][c].toString()
                    }
                    letterPaint.getTextBounds(text, 0, text.length, letterPaintBounds)
                    val width: Float = letterPaint.measureText(text)
                    val height: Float = letterPaintBounds.height().toFloat()
                    canvas.drawText(
                        text, c * cellSize + (cellSize - width) / 2,
                        r * cellSize + cellSize - (cellSize - height) / 2,
                        letterPaint
                    )
                }
            }
        }
        letterPaint.color = letterColorSolve
        for (letter in solver.getEmptyBoxIndex()!!) {
            val r = letter[0] as Int
            val c = letter[1] as Int
            val text = solver.getBoard()[r][c].toString()
            letterPaint.getTextBounds(text, 0, text.length, letterPaintBounds)
            val width: Float = letterPaint.measureText(text)
            val height: Float = letterPaintBounds.height().toFloat()
            canvas.drawText(
                text, c * cellSize + (cellSize - width) / 2,
                r * cellSize + cellSize - (cellSize - height) / 2,
                letterPaint
            )
        }
    }

    private fun colorCell(canvas: Canvas, r: Int, c: Int) {
        if (solver.getSelectedColumn() !== -1 && solver.getSelectedRow() !== -1) {
            canvas.drawRect(
                ((c - 1) * cellSize).toFloat(),
                0f,
                (c * cellSize).toFloat(),
                (cellSize * 9).toFloat(),
                cellsHighlightColorPaint
            )
            canvas.drawRect(
                0f,
                ((r - 1) * cellSize).toFloat(),
                (cellSize * 9).toFloat(),
                (r * cellSize).toFloat(),
                cellsHighlightColorPaint
            )
            canvas.drawRect(
                ((c - 1) * cellSize).toFloat(),
                ((r - 1) * cellSize).toFloat(),
                (c * cellSize).toFloat(),
                (r * cellSize).toFloat(),
                cellsHighlightColorPaint
            )
        }
        invalidate()
    }

    private fun drawThickLine() {
        boardColorPaint.style = Paint.Style.STROKE
        boardColorPaint.strokeWidth = 10f
        boardColorPaint.color = boardColor
    }

    private fun drawThinLine() {
        boardColorPaint.style = Paint.Style.STROKE
        boardColorPaint.strokeWidth = 4f
        boardColorPaint.color = boardColor
    }


    private fun drawBoard(canvas: Canvas) {
        for (c in 0..9) {
            if (c % 3 == 0) {
                drawThickLine()
            } else {
                drawThinLine()
            }
            canvas.drawLine(
                (cellSize * c).toFloat(), 0f, (
                        cellSize * c).toFloat(), width.toFloat(), boardColorPaint
            )
        }
        for (r in 0..9) {
            if (r % 3 == 0) {
                drawThickLine()
            } else {
                drawThinLine()
            }
            canvas.drawLine(
                0f, (cellSize * r).toFloat(),
                width.toFloat(), (cellSize * r).toFloat(), boardColorPaint
            )
        }
    }

    fun getSolver() = solver

}