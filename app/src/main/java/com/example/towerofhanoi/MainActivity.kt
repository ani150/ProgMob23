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
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable

class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setter onTouchListener på alle fire ImageView. Brukes til å starte "dragAndDrop".
        val imageFace = findViewById<ImageView>(R.id.blueRing)
        imageFace.setOnTouchListener(MyTouchListener())

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
    }

    // Ikonene/bildene håndterer onTouch & start "drag":
    inner class MyTouchListener : View.OnTouchListener {
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {
            // Starter "drag"-operasjon:
            return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
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
                false
            }
        }
    }

    // Denne håndterer drag-drop events for rektanglende/konteinerne:
    inner class MyDragListener : View.OnDragListener {
        // Alle fire rektangler (LinearLayout) lytter etter drag-events.
        // Følgende drawables brukes til å sette korrekt bakgrunn.
        var enterShape: Drawable? = getDrawable(this@MainActivity, R.drawable.tower_shape_droptarget)
        var normalShape: Drawable? = getDrawable(this@MainActivity, R.drawable.tower_shape)

        override fun onDrag(view: View, event: DragEvent): Boolean {
            val action = event.action
            var dragInterrupted = false

            //Bildet som blir dradd:.
            val draggedView = event.localState as View
            // Konteiner som draggedView dras til (som er en av de fire LinearLayout-ene):
            val receiveContainer = view as LinearLayout
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED ->                     // view er konteineren (her: en av de fire LinearLayout-ene).
                    view.setBackground(enterShape)
                DragEvent.ACTION_DRAG_EXITED ->                     // view er konteineren (her: en av de fire LinearLayout-ene).
                    view.setBackground(normalShape)
                DragEvent.ACTION_DROP -> {
                    // Settes denne til true avbrytes drag - her kan man altså sette en betingelse for avbrutt operasjon eller ikke!
                    dragInterrupted = false
                    if (dragInterrupted) {
                        return false
                    } else {
                        // owner er her foreldreview (en av de fire LinearLayout-objektene) til bildet som blir dradd,
                        // fjerner bildet fra ownerview:
                        val owner = draggedView.parent as ViewGroup
                        owner.removeView(draggedView)
                        // Legger draggedView til motakkskonteiner:
                        receiveContainer.addView(draggedView)
//                        toTower.addView(draggedRing, 0)
                    }
                    draggedView.visibility = View.VISIBLE
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    draggedView.visibility = View.VISIBLE //Sett view synlig igjen.
                    receiveContainer.background = normalShape //Sett korrekt bakgrunn.
                }
                else -> {}
            }
            return true
        }
    }
}