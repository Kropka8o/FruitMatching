package com.example.fruitmatching

import android.content.ClipData
import android.content.ClipDescription
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.fruitmatching.databinding.ActivityGameBinding
import com.example.fruitmatching.databinding.ActivityMainBinding
import kotlin.getValue
import kotlin.toString

class GameActivity : AppCompatActivity() {

    val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityGameBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupFruits()

        setupDrop(binding.plateOne)
        setupDrop(binding.plateTwo)
        setupDrop(binding.plateThree)
        setupDrop(binding.plateFour)
        setupDrop(binding.plateFive)
        setupDrop(binding.plateSix)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private val fruits = mutableListOf<Fruit>()
    var placedFruitCount = 0

    private fun createFruitView(fruit: Fruit): ImageView {
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.ffdddd)
        val params = LinearLayout.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.fruit_size),
            resources.getDimensionPixelSize(R.dimen.fruit_size)
        )

        params.marginStart = resources.getDimensionPixelSize(R.dimen.fruit_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.fruit_margin)
        params.topMargin = resources.getDimensionPixelSize(R.dimen.fruit_margin)
        params.bottomMargin = resources.getDimensionPixelSize(R.dimen.fruit_margin)
        params.gravity = Gravity.CENTER
        imageView.layoutParams = params
        imageView.tag = fruit.id
        setupDrag(imageView)
        return imageView
    }

    private fun setupFruits() {
        fruits.clear()
        repeat(4) { fruits.add(Fruit()) }
        renderFruits()
    }

    private fun renderFruits() {
        val fruitContainer = findViewById<LinearLayout>(R.id.fruit_container)
        fruitContainer.removeAllViews()
        for (fruit in fruits) {
            val fruitView = createFruitView(fruit)
            fruitContainer.addView(fruitView)
        }
    }

    private fun onFruitPlaced() {
        if (placedFruitCount >= 4) {
            setupFruits()
            placedFruitCount = 0
        }
    }

    fun setupDrag(draggableView: View) {
        draggableView.setOnLongClickListener { v ->
            val label = "Dragged Fruit"
            val clipItem = ClipData.Item(v.tag as? String)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val draggedData = ClipData(label, mimeTypes, clipItem)
            v.startDragAndDrop(
                draggedData,
                View.DragShadowBuilder(v),
                v,
                0
            )
//            v.visibility = View.INVISIBLE
            true
        }
    }


    private fun setupDrop(dropTarget: View) {
        dropTarget.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d(TAG, "ON DRAG STARTED")
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        (v as? ImageView)?.alpha = 0.5F
                        v.invalidate()
                        true
                    } else {
                        false
                    }
                }
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as View
                    val owner = draggedView.parent as ViewGroup
                    owner.removeView(draggedView)
                    (v as ViewGroup).addView(draggedView)
                    draggedView.visibility = View.VISIBLE
                    (v as? ImageView)?.alpha = 1.0F
                    placedFruitCount++
                    onFruitPlaced()
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.d(TAG, "ON DRAG ENTERED")
                    (v as? ImageView)?.alpha = 0.3F
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    Log.d(TAG, "ON DRAG EXISTED")
                    (v as? ImageView)?.alpha = 0.5F
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.d(TAG, "ON DRAG ENDED")
                    (v as? ImageView)?.alpha = 1.0F
                    true
                }
            }
            true
        }
    }
}