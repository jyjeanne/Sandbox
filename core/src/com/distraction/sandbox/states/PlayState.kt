package com.distraction.sandbox.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.distraction.sandbox.Background
import com.distraction.sandbox.Constants
import com.distraction.sandbox.Context
import com.distraction.sandbox.tilemap.Player
import com.distraction.sandbox.tilemap.TileMap
import com.distraction.sandbox.tilemap.TileMapData

interface MoveListener {
    fun onMoved()
}

class PlayState(context: Context, private val level: Int) : GameState(context), MoveListener {

    private val tileMap = TileMap(context, TileMapData.levelData[level - 1])
    private val player = Player(context, tileMap, this)
    private val bg = Background(context)
    private val bgCam = OrthographicCamera().apply {
        setToOrtho(false, Constants.WIDTH, Constants.HEIGHT)
    }

    init {
        camera.position.set(0f, -1000f, 0f)
        camera.update()
    }

    override fun onMoved() {
        if (tileMap.isFinished()) {
            context.gsm.replace(PlayState(context, level + 1))
        }
    }

    override fun update(dt: Float) {
        when {
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> player.moveTile(0, 1)
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> player.moveTile(0, -1)
            Gdx.input.isKeyPressed(Input.Keys.UP) -> player.moveTile(-1, 0)
            Gdx.input.isKeyPressed(Input.Keys.DOWN) -> player.moveTile(1, 0)
        }

        player.update(dt)

        camera.position.set(camera.position.lerp(player.pp, 0.1f))
        camera.update()

        bg.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        sb.projectionMatrix = bgCam.combined
        sb.begin()
        bg.render(sb)
        sb.end()

        sb.projectionMatrix = camera.combined
        sb.begin()
        tileMap.render(sb)
        player.render(sb)
        sb.end()
    }
}