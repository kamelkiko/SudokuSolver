package com.kiko.sudokusolver

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var solver: Solver
    private lateinit var board: SudokuBoard
    private lateinit var solverBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        solving()
    }

    private fun initViews() {
        board = findViewById(R.id.screen)
        solver = board.getSolver()
        solverBtn = findViewById(R.id.solveButton)
    }

    fun onClick(view: View) {
        val number = (view as TextView).text.toString().toInt()
        solver.setNumberPos(number)
        board.invalidate()
    }

    private fun solving() {
        solverBtn.setOnClickListener {
            if (solver.isError()) {
                Toast.makeText(this@MainActivity, "Wrong Input Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (solverBtn.text.toString() == getString(R.string.solve)) {
                solverBtn.text = getString(R.string.clear)
                solver.getEmptyBoxIndexes()
                val solveBoardThread = SolveBoardThread()
                Thread(solveBoardThread).start()
                board.invalidate()
            } else {
                solverBtn.text = getString(R.string.solve)
                solver.clearBoard()
                board.invalidate()
            }
        }
    }

    internal inner class SolveBoardThread : Runnable {
        override fun run() {
            solver.solve(board)
        }
    }
}