package ui.anwesome.com.blockcircleanimview

/**
 * Created by anweshmishra on 18/04/18.
 */

import android.app.Activity
import android.content.*
import android.graphics.*
import android.view.*

class BlockCircleAnimView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var j : Int = 0, var dir : Float = 0f) {
        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate (updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BlockAnimCircle (var i : Int , private val state : State = State()) {
        fun draw (canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val sizeH : Float = w/6
            val r : Float = Math.min(w, h)/20
            canvas.save()
            canvas.translate(w/2, h/2)
            for (i in 0..1) {
                canvas.save()
                canvas.translate(0f, sizeH * state.scales[0] * (1 - 2 * i))
                canvas.drawRect(-sizeH, -sizeH, sizeH, sizeH, paint)
                canvas.restore()
            }
            for (i in 0..1) {
                canvas.save()
                canvas.translate(-sizeH * state.scales[2] * (1 - 2 * i), sizeH + r)
                canvas.drawCircle(0f, 0f, r * state.scales[1], paint)
                canvas.restore()
            }
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class Renderer (var view : BlockCircleAnimView) {
        private val blockAnimCircle : BlockAnimCircle = BlockAnimCircle(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            paint.color = Color.parseColor("#2980b9")
            blockAnimCircle.draw(canvas, paint)
            animator.animate {
                blockAnimCircle.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            blockAnimCircle.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : BlockCircleAnimView {
            val view : BlockCircleAnimView = BlockCircleAnimView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
