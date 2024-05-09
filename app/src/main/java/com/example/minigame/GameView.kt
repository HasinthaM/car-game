package com.example.minigame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import java.lang.Exception
//main view of the game
//Context,GameTask object as constructor parameters
class GameView(var c :Context,var gameTask: GameTask ):View(c)
{
    //declare variables and initialize
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myCarPosition = 0
    private val otherCars = ArrayList<HashMap<String,Any>>()

    //Variables to store the width and height of the view.
    var viewWidth = 0
    var viewHeight = 0
    init {
        myPaint = Paint()
    }

    //This method call when the view needs to be drawn
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        //Generates random cars based on time elapsed and updates the time
        if(time % 700 < 10 +speed){
            val map = HashMap<String,Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCars.add(map) }
        time = time + 10 + speed

        //Calculates the dimensions of the car and sets the paint style
        val carWidth = viewWidth / 5
        val carHeight = carWidth + 10
        myPaint!!.style = Paint.Style.FILL
        val d = resources.getDrawable(R.drawable.redcar, null)

        //Sets the position of the player's car and draws it on the canvas.
        d.setBounds(
            myCarPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight- 2 - carHeight,
            myCarPosition * viewWidth / 3 + viewWidth / 15 + carWidth - 25,
            viewHeight - 2
        )
        d.draw(canvas!!)
        myPaint!!.color = Color.GREEN
        var highScore = 0 //Initializes the variable highScore to 0

        //Iterates through the list of other cars,calculates their positions,and draws them on the canvas
        for (i in otherCars.indices){
            try {
                val carX = otherCars[i]["lane"] as Int * viewWidth / 3 + viewWidth /15
                var carY = time - otherCars[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.whitecar, null)
                d2.setBounds(
                    carX + 25 , carY - carHeight , carX + carWidth - 25 , carY
                )
                d2.draw(canvas)

                //Collisions between the player's car and other cars and end the game if a collision is detected
                if (otherCars[i]["lane"] as Int == myCarPosition){
                    if (carY > viewHeight - 2 - carHeight
                        && carY < viewHeight - 2){

                        gameTask.closeGame(score)
                    }
                }
                //Removes cars that have passed the player's car
                if (carY > viewHeight + carHeight){
                    otherCars.removeAt(i)
                    score++ //Update score
                    speed = 1 + Math.abs(score / 8) //Update speed
                    if ( score > highScore){
                        highScore = score
                    }
                }
            }
            catch (e:Exception){
                e. printStackTrace()
            }
        }

        //Sets the color and text size for displaying the score and speed on the canvas
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score : $score", 80f,80f,myPaint!!)
        canvas.drawText("Speed : $speed", 380f,80f,myPaint!!)
        invalidate()

    }

    //Handles touch events to control the movement of the player's car
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN ->{
                val x1 = event.x
                if (x1 < viewWidth / 2){
                    if (myCarPosition > 0){
                        myCarPosition--
                    }
                }
                if (x1 > viewWidth / 2){
                    if (myCarPosition < 2){
                        myCarPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP ->{}
        }
        return true
    }

}