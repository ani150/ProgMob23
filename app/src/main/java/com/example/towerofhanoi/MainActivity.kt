package com.example.towerofhanoi

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.children
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {

    var seconds: Int = 0
    lateinit var clock: TextView
    var movesInt: Int = 0
    lateinit var moves: TextView
    lateinit var draggedView: ImageView
    lateinit var toTower: LinearLayout
    lateinit var fromTower: LinearLayout
    lateinit var left: LinearLayout
    lateinit var middle: LinearLayout
    lateinit var right: LinearLayout
    var image: Int = 0
    lateinit var blueRing: ImageView
    lateinit var redRing: ImageView
    lateinit var orangeRing: ImageView
    private var blueRingIndex: Int = 0
    private var redRingIndex: Int = 0
    private var orangeRingIndex: Int = 0
    var gameState: Int = 0


    override fun onRestart() {
        super.onRestart()
        left.removeAllViews()
        middle.removeAllViews()
        right.removeAllViews()
        left.addView(redRing)
        left.addView(blueRing)
        left.addView(orangeRing)
        seconds = 0;
        movesInt = 0;
        moves.text = movesInt.toString();
        clock.text = seconds.toString()
    }

    override fun onStop() {
        super.onStop()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setContentView(R.layout.activity_main)


        blueRing = findViewById<ImageView>(R.id.blueRing)
        blueRing.setOnTouchListener(MyTouchListener())

        redRing = findViewById<ImageView>(R.id.redRing)
        redRing.setOnTouchListener(MyTouchListener())

        orangeRing = findViewById<ImageView>(R.id.orangeRing)
        orangeRing.setOnTouchListener(MyTouchListener())

        left = findViewById<LinearLayout>(R.id.tower1)
        left.setOnDragListener(MyDragListener())

        middle = findViewById<LinearLayout>(R.id.tower2)
        middle.setOnDragListener(MyDragListener())

        right = findViewById<LinearLayout>(R.id.tower3)
        right.setOnDragListener(MyDragListener())


        moves = findViewById<TextView>(R.id.moves)
        moves.text =  0.toString()

        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener { view ->
            this@MainActivity.onRestart()
        }

        clock = findViewById<TextView>(R.id.textClock)
        clock.text = "00:00:00"

        var timer = Timer()
        timer.scheduleAtFixedRate(
            timerTask {
                if (!redRing.isEnabled){
                    timer.cancel()
                }else{
                seconds++}
                this@MainActivity.runOnUiThread({ clock.text = seconds.toString() })

            },
            0,
            1000
        )


//        println("oncreate")
//        println(left.children)
//        println(middle.children)
//        println(right.children)
//        println(redRingIndex)
//        println(blueRingIndex)
//        println(orangeRingIndex)
//        println(redRing.id)
//        println(blueRing.id)
//        println(orangeRing.id)
//        println(gameState)

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("seconds", seconds)
        outState.putInt("moves", movesInt)
//        outState.putInt("gameState", gameState)
//        outState.putInt("blueRingIndex", blueRing.id)
//        outState.putInt("redRingIndex", redRing.id)
//        outState.putInt("orangeRingIndex", orangeRing.id)
        outState.putInt("leftRed", left.indexOfChild(redRing))
        outState.putInt("leftBlue", left.indexOfChild(blueRing))
        outState.putInt("leftOrange", left.indexOfChild(orangeRing))
        outState.putInt("middleRed", middle.indexOfChild(redRing))
        outState.putInt("middleBlue", middle.indexOfChild(blueRing))
        outState.putInt("middleOrange", middle.indexOfChild(orangeRing))
        outState.putInt("rightRed", right.indexOfChild(redRing))
        outState.putInt("rightBlue", right.indexOfChild(blueRing))
        outState.putInt("rightOrange", right.indexOfChild(orangeRing))

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        movesInt = savedInstanceState.getInt("moves", 0)
        moves.text = movesInt.toString()
        seconds = savedInstanceState.getInt("seconds", 0)
        clock.text = seconds.toString()
//        gameState = savedInstanceState.getInt("gameState", gameState)
//        blueRingIndex= savedInstanceState.getInt("blueRingIndex", blueRing.id)
//        redRingIndex= savedInstanceState.getInt("redRingIndex", redRing.id)
//        orangeRingIndex = savedInstanceState.getInt("orangeRingIndex", orangeRing.id)
        val leftRed = savedInstanceState.getInt("leftRed", left.indexOfChild(redRing))
        val leftBlue = savedInstanceState.getInt("leftBlue", left.indexOfChild(blueRing))
        val leftOrange = savedInstanceState.getInt("leftOrange", left.indexOfChild(orangeRing))
        val middleRed = savedInstanceState.getInt("middleRed", middle.indexOfChild(redRing))
        val middleBlue = savedInstanceState.getInt("middleBlue", middle.indexOfChild(blueRing))
        val middleOrange = savedInstanceState.getInt("middleOrange", middle.indexOfChild(orangeRing))
        val rightRed = savedInstanceState.getInt("rightRed", right.indexOfChild(redRing))
        val rightBlue = savedInstanceState.getInt("rightBlue", right.indexOfChild(blueRing))
        val rightOrange = savedInstanceState.getInt("rightOrange", right.indexOfChild(orangeRing))

        left.removeAllViews()
        middle.removeAllViews()
        right.removeAllViews()

        if (leftRed != -1) {
            left.addView(redRing)
        }
        if (leftBlue != -1) {
            left.addView(blueRing)
        }
        if (leftOrange != -1) {
            left.addView(orangeRing)
        }
        if (middleRed != -1) {
            middle.addView(redRing)
        }
        if (middleBlue != -1) {
            middle.addView(blueRing)
        }
        if (middleOrange != -1) {
            middle.addView(orangeRing)
        }
        if (rightRed != -1) {
            right.addView(redRing)
        }
        if (rightBlue != -1) {
            right.addView(blueRing)
        }
        if (rightOrange != -1) {
            right.addView(orangeRing)
        }



//        println("onRestoreInstanceState")
//        println(seconds)
//        println(movesInt)
//        println(blueRingIndex)
//        println(redRingIndex)
//        println(orangeRingIndex)
//
//
//
//        println("onRestoreInstanceState")
//        println(seconds)
//        println(movesInt)

    }

    inner class MyDragListener : View.OnDragListener {

        var enterShape: Drawable? =
            getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
        var normalShape: Drawable? = getDrawable(this@MainActivity, R.drawable.tower_shape)

        @SuppressLint("ResourceType")
        override fun onDrag(view: View, event: DragEvent): Boolean {
            val action = event.action

            draggedView = event.localState as ImageView

            val receiveContainer = view as LinearLayout

            when (action) {

                DragEvent.ACTION_DRAG_STARTED -> {
                }
                DragEvent.ACTION_DRAG_ENTERED ->
                    view.setBackground(enterShape)
                DragEvent.ACTION_DRAG_EXITED ->
                    view.setBackground(normalShape)
                DragEvent.ACTION_DROP -> {
                    toTower = view as LinearLayout
                    val topElement: View? = toTower.getChildAt(0) ?: null
                    val topElementWidth = topElement?.width ?: 0
                    val draggedViewWidth = draggedView.width

                    if (draggedView.id == R.id.blueRing
                        || draggedView.id == R.id.redRing
                        || draggedView.id == R.id.orangeRing
                    ) {
                        moves = findViewById<TextView>(R.id.moves)
                        movesInt = moves.text.toString().toInt()
                        movesInt++
                        moves.setText(movesInt.toString())
                    }



                    if (topElement == null || draggedViewWidth < topElementWidth) {
                        val fromTower = draggedView.parent as LinearLayout
                        fromTower.removeView(draggedView)
                        toTower.addView(draggedView, 0)

//                        gameState =
//                            when (draggedView.id) {
//                            R.id.blueRing -> blueRingIndex
//                            R.id.redRing -> redRingIndex
//                            R.id.orangeRing -> orangeRingIndex
//                            else -> gameState
//                        }


                        if (toTower.id == R.id.tower3 && toTower.childCount == 3) {
                            Toast.makeText(
                                this@MainActivity,
                                "Du har vunnet!",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("vinner")
                            redRing.isEnabled = false


                        }
                    } else {
                        if (draggedViewWidth > topElementWidth)
                            Toast.makeText(
                                this@MainActivity,
                                "Du kan ikke legge en større ring over en mindre!",
                                Toast.LENGTH_SHORT
                            ).show()
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