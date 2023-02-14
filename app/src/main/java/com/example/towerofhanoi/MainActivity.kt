package com.example.towerofhanoi

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import java.util.*
import kotlin.concurrent.timerTask

const val GAME_STATE = "STATE"
const val MY_DEBUG_TAG = "ANI_MELDINGER"
class MainActivity : AppCompatActivity() {

    var seconds: Int=0
    lateinit var clock: TextView
    var gameState: String? = null
    var movesInt: Int = 0
    lateinit var moves: TextView


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(GAME_STATE, this.gameState)
        outState.putInt("seconds", seconds)
        outState.putInt("moves", movesInt)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        seconds = savedInstanceState.getInt("seconds", 0)
        movesInt = savedInstanceState.getInt("moves", 0)
        moves.text = movesInt.toString()
        clock.text = seconds.toString()


    }
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val blueRing = findViewById<ImageView>(R.id.blueRing)
        blueRing.setOnTouchListener(MyTouchListener())

        val redRing = findViewById<ImageView>(R.id.redRing)
        redRing.setOnTouchListener(MyTouchListener())

        val orangeRing = findViewById<ImageView>(R.id.orangeRing)
        orangeRing.setOnTouchListener(MyTouchListener())

        val left = findViewById<LinearLayout>(R.id.tower1)
        left.setOnDragListener(MyDragListener())

        val middle = findViewById<LinearLayout>(R.id.tower2)
        middle.setOnDragListener(MyDragListener())

        val right = findViewById<LinearLayout>(R.id.tower3)
        right.setOnDragListener(MyDragListener())

        moves = findViewById<TextView>(R.id.moves)
        moves.text = 0.toString()

        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener { view -> this@MainActivity.recreate();
            seconds = 0;
            movesInt = 0;
            moves.text = movesInt.toString();
            clock.text = seconds.toString()}

        clock = findViewById<TextView>(R.id.textClock)
        clock.text = "00:00:00"

        var timer = Timer()
        timer.scheduleAtFixedRate(
            timerTask {
                seconds++
                this@MainActivity.runOnUiThread({ clock.text = seconds.toString() })

            },
            0,
            1000
        )


    }

    inner class MyTouchListener : View.OnTouchListener {
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {
            // Starter "drag"-operasjon:
            val owner = viewToBeDragged.parent as LinearLayout
            val top = owner.getChildAt(0)
            return if (viewToBeDragged === top || owner.childCount == 1) {

                val data = ClipData.newPlainText("", "")

                val shadowBuilder = View.DragShadowBuilder(viewToBeDragged)

                viewToBeDragged.startDragAndDrop(data, shadowBuilder, viewToBeDragged, 0)

                viewToBeDragged.visibility = View.INVISIBLE
                true
            } else {
                Toast.makeText(this@MainActivity, "Du kan kun flytte toppen!", Toast.LENGTH_SHORT)
                    .show()
                false
            }
        }
    }

    inner class MyDragListener : View.OnDragListener {

        var enterShape: Drawable? =
            getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
        var normalShape: Drawable? = getDrawable(this@MainActivity, R.drawable.tower_shape)

        override fun onDrag(view: View, event: DragEvent): Boolean {
            val action = event.action
            var cancelDrag = false

            val draggedView = event.localState as ImageView

            val receiveContainer = view as LinearLayout

            when (action) {

                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED ->
                    view.setBackground(enterShape)
                DragEvent.ACTION_DRAG_EXITED ->
                    view.setBackground(normalShape)
                DragEvent.ACTION_DROP -> {

                    val toTower = view as LinearLayout
                    val topElement: View? = toTower.getChildAt(0) ?: null
                    val topElementWidth = topElement?.width ?: 0
                    val draggedViewWidth = draggedView.width

                    if (draggedView.id == R.id.blueRing
                        ||draggedView.id == R.id.redRing
                        ||draggedView.id == R.id.orangeRing) {
                        var moves = findViewById<TextView>(R.id.moves)
                        movesInt = moves.text.toString().toInt()
                        movesInt++
                        moves.setText(movesInt.toString()) }

                    if (topElement == null || draggedViewWidth < topElementWidth) {
                        val fromTower = draggedView.parent as LinearLayout
                        fromTower.removeView(draggedView)
                        toTower.addView(draggedView, 0)
                        println("Top element is null")



                        if (toTower.id == R.id.tower3 && toTower.childCount == 3) {
                            Toast.makeText(
                                this@MainActivity,
                                "Du har vunnet!",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("vinner")
                            this@MainActivity.recreate()

                        }
                        cancelDrag = false
                    } else {
                        if (draggedViewWidth > topElementWidth)
                            Toast.makeText(
                                this@MainActivity,
                                "Du kan ikke legge en større ring over en mindre!",
                                Toast.LENGTH_SHORT
                            ).show()
                        cancelDrag = true

                    }

                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    receiveContainer.background = normalShape
                    draggedView.visibility = View.VISIBLE
                }
                else -> {}


            }
            return true

        }
    }
}