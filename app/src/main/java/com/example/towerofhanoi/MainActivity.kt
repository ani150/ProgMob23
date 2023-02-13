package com.example.towerofhanoi

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setter onTouchListener på alle fire ImageView. Brukes til å starte "dragAndDrop".
        val blueRing = findViewById<ImageView>(R.id.blueRing)
        blueRing.setOnTouchListener(MyTouchListener())

        val redRing = findViewById<ImageView>(R.id.redRing)
        redRing.setOnTouchListener(MyTouchListener())

        val orangeRing = findViewById<ImageView>(R.id.orangeRing)
        orangeRing.setOnTouchListener(MyTouchListener())

        // Setter onDraListener på de fire konteinerne (alle av type LinearLayout):
        val left = findViewById<LinearLayout>(R.id.tower1)
        left.setOnDragListener(MyDragListener())

        val middle = findViewById<LinearLayout>(R.id.tower2)
        middle.setOnDragListener(MyDragListener())

        val right = findViewById<LinearLayout>(R.id.tower3)
        right.setOnDragListener(MyDragListener())

        var moves = findViewById<TextView>(R.id.moves)
        moves.text = 0.toString()

        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener { this.recreate() }

        var clock = findViewById<TextView>(R.id.textClock)
        clock.text = "00:00:00"


    }

    // Ikonene/bildene håndterer onTouch & start "drag":
    inner class MyTouchListener : View.OnTouchListener {
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {
            // Starter "drag"-operasjon:
            val owner = viewToBeDragged.parent as LinearLayout
            val top = owner.getChildAt(0)
            return if (viewToBeDragged === top || owner.childCount == 1) {
                // Data som kan sendes med til "drag-receiver" (brukes ikke her):
                val data = ClipData.newPlainText("", "")

                // Lager en "drag-skygge" av viewet som skal dras (gjør opprinnelig viewToBeDragged usynlig):
                val shadowBuilder = View.DragShadowBuilder(viewToBeDragged)

                // Starter Drag&Drop, alle View som implemenenterer OnDragListener vil motta onDrag-events.
                viewToBeDragged.startDragAndDrop(data, shadowBuilder, viewToBeDragged, 0)

                // Gjør bildet som "dragges" usynlig (kun skyggen er nå synlig):
                viewToBeDragged.visibility = View.INVISIBLE
                true
            } else {
                Toast.makeText(this@MainActivity, "Du kan kun flytte toppen!", Toast.LENGTH_SHORT)
                    .show()
                false
            }
        }
    }

    // Denne håndterer drag-drop events for rektanglende/konteinerne:
    inner class MyDragListener : View.OnDragListener {
        // Alle fire rektangler (LinearLayout) lytter etter drag-events.
        // Følgende drawables brukes til å sette korrekt bakgrunn.
        var enterShape: Drawable? =
            getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
        var normalShape: Drawable? = getDrawable(this@MainActivity, R.drawable.tower_shape)

        override fun onDrag(view: View, event: DragEvent): Boolean {
            val action = event.action
            var cancelDrag = false
            //Bildet som blir dradd:.
            val draggedView = event.localState as ImageView
            // Konteiner som draggedView dras til (som er en av de fire LinearLayout-ene):
            val receiveContainer = view as LinearLayout
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                   var clock = findViewById<TextView>(R.id.textClock)

                }
                DragEvent.ACTION_DRAG_ENTERED ->                     // view er konteineren (her: en av de fire LinearLayout-ene).
                    view.setBackground(enterShape)
                DragEvent.ACTION_DRAG_EXITED ->                     // view er konteineren (her: en av de fire LinearLayout-ene).
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
                        var movesInt = moves.text.toString().toInt()
                        movesInt++
                        moves.setText(movesInt.toString()) }

                    if (topElement == null || draggedViewWidth < topElementWidth) {
                        //Remove draggedView from its tower
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
                    // view er konteineren (her: en av de fire LinearLayout-ene).
                    receiveContainer.background = normalShape
                    // Gjør viewen som blir dradd synlig igjen (kun skyggen er nå synlig):
                    draggedView.visibility = View.VISIBLE
                }
                else -> {}

            }
            return true
        }
    }
}