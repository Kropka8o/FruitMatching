package com.example.fruitmatching

import android.content.ClipData
import android.content.ClipDescription
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.lastIndexOf
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.fruitmatching.databinding.ActivityGameBinding
import com.example.fruitmatching.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.collections.removeAll
import kotlin.compareTo
import kotlin.getValue
import kotlin.inc
import kotlin.random.Random
import kotlin.toString

class GameActivity : AppCompatActivity() {

    val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityGameBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupFruits()
        setupPlates()
        val scoreNumber = findViewById<TextView>(R.id.scoreNumber)
        scoreNumber.text = "0"


        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private val fruits = mutableListOf<Fruit>()
    private val placedFruits = mutableListOf<Fruit>()
    private val plates = mutableListOf<Plate>()
    private val colours = mutableListOf("pink", "yellow", "green",  "blue", "violet", "red", "orange", "darkGreen", "darkBlue", "darkViolet")
    private var fullPlates = mutableListOf<Plate>()


    private fun createPlateView(plate: Plate): GridLayout {
        val gridLayout = GridLayout(this)
        gridLayout.setBackgroundResource(R.drawable.ffffff)

        gridLayout.columnCount = 2
        gridLayout.rowCount = 2
        val params = LinearLayout.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.plate_size),
            resources.getDimensionPixelSize(R.dimen.plate_size)
        )

        params.marginStart = resources.getDimensionPixelSize(R.dimen.plate_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.plate_margin)
        params.topMargin = resources.getDimensionPixelSize(R.dimen.plate_margin)
        params.bottomMargin = resources.getDimensionPixelSize(R.dimen.plate_margin)
        params.gravity = Gravity.CENTER
        gridLayout.layoutParams = params
        plate.id = View.generateViewId()
        gridLayout.tag = plate.id
        setupDrop(gridLayout)
        return gridLayout
    }

    private fun setupPlates() {
        plates.clear()
        repeat(6) { plates.add(Plate()) }
        renderPlates()
    }

    private fun renderPlates() {
        val plateContainer = findViewById<GridLayout>(R.id.plate_container)
        plateContainer.removeAllViews()
        for (plate in plates) {
            val plateView = createPlateView(plate)
            plateContainer.addView(plateView)
        }
    }

    private fun createFruitView(fruit: Fruit): ImageView {
        val imageView = ImageView(this)
        var randomNumber = Random.nextInt(0, 10)
        fruit.colour = colours[randomNumber]
        when (fruit.colour) {
            "pink" -> imageView.setImageResource(R.drawable.ffdddd)
            "yellow" -> imageView.setImageResource(R.drawable.ffffdd)
            "green" -> imageView.setImageResource(R.drawable.ddffdd)
            "blue" -> imageView.setImageResource(R.drawable.ddffff)
            "violet" -> imageView.setImageResource(R.drawable.eeddff)
            "red" -> imageView.setImageResource(R.drawable.ff9191)
            "orange" -> imageView.setImageResource(R.drawable.ffe1c4)
            "darkGreen" -> imageView.setImageResource(R.drawable.aaffaa)
            "darkBlue" -> imageView.setImageResource(R.drawable.aaffff)
            "darkViolet" -> imageView.setImageResource(R.drawable.d5aaff)
            else -> imageView.setImageResource(R.drawable.b7b7b7)
        }

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
        fruit.id = View.generateViewId()
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

    private fun onFruitPlaced(fruitView: View) {
        val fruitId = fruitView.tag as Int
        val fruit = fruits.find { it.id == fruitId } ?: placedFruits.find { it.id == fruitId }
        val parentPlateView = fruitView.parent as? GridLayout
        val plateId = parentPlateView?.tag as? Int

        if (fruit != null && plateId != null) {
            fruit.plateId = plateId
            if (!placedFruits.contains(fruit)) {
                placedFruits.add(fruit)
            }
        }

        val fruitContainer = findViewById<LinearLayout>(R.id.fruit_container)
        if (fruitContainer.childCount == 0) {
            setupFruits()
        }

        checkPlatesForMatches()
    }

    private fun onGameOver() {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("All plates are full.")
            .setPositiveButton("Play again") { dialog, _ ->
                val plateContainer = findViewById<GridLayout>(R.id.plate_container)
                plateContainer.removeAllViewsInLayout()
                placedFruits.clear()
                fruits.clear()
                fullPlates.clear()
                val scoreNumber = findViewById<TextView>(R.id.scoreNumber)
                scoreNumber.text = "0"
                setupPlates()
                setupFruits()
                dialog.dismiss()
            }
            .setNegativeButton("Home") { dialog, _ ->
                val intent = Intent(this, MainActivity::class.java)
                val scoreNumber = findViewById<TextView>(R.id.scoreNumber)
                var score = scoreNumber.text.toString()
                intent.putExtra("score", score)
                startActivity(intent)
                finish()
            }
            .show()

    }

    private fun checkPlatesForMatches() {
        val plateContainer = findViewById<GridLayout>(R.id.plate_container)
        fullPlates.clear()
        for (plate in plates) {
            val fruitsOnPlate = placedFruits.filter { it.plateId == plate.id }
            if (fruitsOnPlate.size >= 4) {
                val firstColour = fruitsOnPlate.first().colour
                val allSameColour = fruitsOnPlate.all { it.colour == firstColour }
                val gridLayout: GridLayout = plateContainer.findViewWithTag<GridLayout>(plate.id)
                if (allSameColour) {
                    gridLayout.removeAllViewsInLayout()
                    updateScore()
                    placedFruits.removeAll(fruitsOnPlate)
                } else {
                    fullPlates.add(plate)
                }
            }
        }
        if (fullPlates.count() == 6) {
            onGameOver()
            val scoreNumber = findViewById<TextView>(R.id.scoreNumber)
            val currentScore = scoreNumber.text.toString().toIntOrNull() ?: 0
            if (currentScore > getHighScore(this)) {
                saveHighScore(this, currentScore)
            }
        }
    }


    fun updateScore() {
        val scoreNumber = findViewById<TextView>(R.id.scoreNumber)
        var currentScore = scoreNumber.text.toString()
        var newScore = currentScore.toInt()
        newScore += 4
        scoreNumber.text = newScore.toString()
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
                    val gridLayout = v as GridLayout
                    if (gridLayout.childCount < 4) {
                        val owner = draggedView.parent as ViewGroup
                        owner.removeView(draggedView)
                        gridLayout.addView(draggedView)
                        draggedView.visibility = View.VISIBLE
                        onFruitPlaced(draggedView)
                    }
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