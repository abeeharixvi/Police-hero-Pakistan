package com.example.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.example.data.EvidenceType
import kotlin.math.cos
import kotlin.math.sin

class GameRenderer {
    // Reusable paints to avoid allocations in draw loop
    private val groundPaint = Paint().apply { color = Color.parseColor("#EFEBE9"); style = Paint.Style.FILL }
    private val roadPaint = Paint().apply { color = Color.parseColor("#37474F"); style = Paint.Style.FILL }
    private val roadStripePaint = Paint().apply { color = Color.parseColor("#ECEFF1"); strokeWidth = 3f; style = Paint.Style.STROKE }
    private val buildingPaint = Paint().apply { style = Paint.Style.FILL }
    private val buildingRoofPaint = Paint().apply { style = Paint.Style.FILL }
    private val buildingBorderPaint = Paint().apply { color = Color.parseColor("#1A252C"); strokeWidth = 2f; style = Paint.Style.STROKE }
    private val textPaint = Paint().apply { color = Color.WHITE; textSize = 16f; isAntiAlias = true; textAlign = Paint.Align.CENTER }
    private val zoneLabelPaint = Paint().apply { color = Color.parseColor("#546E7A"); textSize = 28f; isAntiAlias = true; textAlign = Paint.Align.CENTER }

    private val playerBodyPaint = Paint().apply { color = Color.parseColor("#0D47A1"); style = Paint.Style.FILL; isAntiAlias = true }
    private val playerCapPaint = Paint().apply { color = Color.parseColor("#FFD700"); style = Paint.Style.FILL; isAntiAlias = true }
    private val suspectBodyPaint = Paint().apply { color = Color.parseColor("#C62828"); style = Paint.Style.FILL; isAntiAlias = true }
    private val civilianPaint = Paint().apply { color = Color.parseColor("#FF8F00"); style = Paint.Style.FILL; isAntiAlias = true }
    private val backupPaint = Paint().apply { color = Color.parseColor("#0288D1"); style = Paint.Style.FILL; isAntiAlias = true }
    private val bulletPaint = Paint().apply { color = Color.parseColor("#FFEB3B"); style = Paint.Style.FILL; isAntiAlias = true }
    private val cluePaint = Paint().apply { color = Color.parseColor("#FFC107"); style = Paint.Style.FILL; isAntiAlias = true }
    private val rangeCirclePaint = Paint().apply { color = Color.parseColor("#44FFC107"); style = Paint.Style.FILL; isAntiAlias = true }

    // Minimap paints
    private val minimapBgPaint = Paint().apply { color = Color.parseColor("#D90B192C"); style = Paint.Style.FILL }
    private val minimapBorderPaint = Paint().apply { color = Color.parseColor("#FFD700"); strokeWidth = 3f; style = Paint.Style.STROKE }
    private val minimapRoadPaint = Paint().apply { color = Color.parseColor("#455A64"); style = Paint.Style.FILL }
    private val minimapPlayerPaint = Paint().apply { color = Color.parseColor("#29B6F6"); style = Paint.Style.FILL }
    private val minimapSuspectPaint = Paint().apply { color = Color.parseColor("#F44336"); style = Paint.Style.FILL }
    private val minimapTargetPaint = Paint().apply { color = Color.parseColor("#FFCA28"); style = Paint.Style.FILL }
    private val minimapHqPaint = Paint().apply { color = Color.parseColor("#4CAF50"); style = Paint.Style.FILL }

    private var strobeTimer: Float = 0f

    fun drawWorld(canvas: Canvas, session: GameSession, screenWidth: Float, screenHeight: Float) {
        strobeTimer += 0.05f
        val player = session.player

        // Camera center on player or vehicle
        val camX = player.x
        val camY = player.y

        canvas.save()
        canvas.translate(screenWidth / 2f - camX, screenHeight / 2f - camY)

        // 1. Background terrain (warm subcontinental dust/sand stone)
        val viewLeft = camX - screenWidth / 2f - 100f
        val viewTop = camY - screenHeight / 2f - 100f
        val viewRight = camX + screenWidth / 2f + 100f
        val viewBottom = camY + screenHeight / 2f + 100f
        canvas.drawRect(viewLeft, viewTop, viewRight, viewBottom, groundPaint)

        // 2. City Roads
        for (road in CityMap.ROADS) {
            if (RectF.intersects(road.bounds, RectF(viewLeft, viewTop, viewRight, viewBottom))) {
                canvas.drawRect(road.bounds, roadPaint)
                // Center dotted stripe
                if (road.isHighway || (!road.isAlley && road.bounds.width() > 100f)) {
                    val midY = road.bounds.centerY()
                    canvas.drawLine(road.bounds.left, midY, road.bounds.right, midY, roadStripePaint)
                }
            }
        }

        // 3. Zone Watermark Labels
        for (zone in CityMap.ZONES) {
            if (RectF.intersects(zone.bounds, RectF(viewLeft, viewTop, viewRight, viewBottom))) {
                canvas.drawText(zone.name.uppercase(), zone.bounds.centerX(), zone.bounds.centerY(), zoneLabelPaint)
            }
        }

        // 4. Buildings & Bazaar Stalls
        for (obs in CityMap.OBSTACLES) {
            if (RectF.intersects(obs.bounds, RectF(viewLeft, viewTop, viewRight, viewBottom))) {
                buildingPaint.color = (obs.colorHex and 0xFFFFFFFFL).toInt()
                canvas.drawRect(obs.bounds, buildingPaint)
                canvas.drawRect(obs.bounds, buildingBorderPaint)

                // Rooftop / Awning detail
                buildingRoofPaint.color = Color.argb(40, 255, 255, 255)
                canvas.drawRect(
                    obs.bounds.left + 8f,
                    obs.bounds.top + 8f,
                    obs.bounds.right - 8f,
                    obs.bounds.bottom - 8f,
                    buildingRoofPaint
                )
            }
        }

        // 5. Evidence Clues
        for (clue in session.clues) {
            if (!clue.isInvestigated) {
                canvas.drawCircle(clue.worldX, clue.worldY, 24f, rangeCirclePaint)
                canvas.drawCircle(clue.worldX, clue.worldY, 10f, cluePaint)
                textPaint.color = Color.YELLOW
                canvas.drawText("?", clue.worldX, clue.worldY + 5f, textPaint)
            }
        }

        // 6. Vehicles in World
        for (veh in session.vehicles) {
            drawVehicle(canvas, veh)
        }

        // 7. Ambient Civilians
        for (civ in session.civilians) {
            canvas.drawCircle(civ.x, civ.y, 14f, civilianPaint)
        }

        // 8. Police Backup AI Units
        for (backup in session.backupUnits) {
            canvas.drawCircle(backup.x, backup.y, 16f, backupPaint)
            // Cap
            val capX = backup.x + cos(backup.angle) * 8f
            val capY = backup.y + sin(backup.angle) * 8f
            canvas.drawCircle(capX, capY, 6f, playerCapPaint)
        }

        // 9. Criminal Suspect
        session.criminal?.let { crim ->
            drawCriminal(canvas, crim)
        }

        // 10. Player (if on foot)
        if (player.inVehicle == null) {
            drawPlayer(canvas, player)
        }

        // 11. Bullets
        for (b in session.bullets) {
            canvas.drawCircle(b.x, b.y, 4f, bulletPaint)
        }

        canvas.restore()

        // 12. Minimap Radar (top-right overlay)
        drawMinimap(canvas, session, screenWidth)
    }

    private fun drawPlayer(canvas: Canvas, player: PlayerEntity) {
        canvas.save()
        canvas.translate(player.x, player.y)
        canvas.rotate((player.angle * 180f / Math.PI).toFloat())

        // Uniform Body
        canvas.drawCircle(0f, 0f, GameConstants.PLAYER_RADIUS, playerBodyPaint)

        // Hands & Weapon
        val weaponPaint = Paint().apply { color = Color.DKGRAY; style = Paint.Style.FILL }
        canvas.drawRect(10f, -4f, 26f, 4f, weaponPaint)

        // Police Peaked Cap / Beret badge
        canvas.drawCircle(8f, 0f, 7f, playerCapPaint)

        canvas.restore()
    }

    private fun drawCriminal(canvas: Canvas, crim: CriminalEntity) {
        canvas.save()
        canvas.translate(crim.x, crim.y)
        canvas.rotate((crim.angle * 180f / Math.PI).toFloat())

        // Criminal Body
        canvas.drawCircle(0f, 0f, GameConstants.CRIMINAL_RADIUS, suspectBodyPaint)

        // Arrested indicator or wanted skull mark
        if (crim.aiState == CriminalAIState.ARRESTED) {
            val cuffPaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 3f; style = Paint.Style.STROKE }
            canvas.drawCircle(0f, 0f, 26f, cuffPaint)
            textPaint.color = Color.CYAN
            canvas.drawText("CUFFED", 0f, -28f, textPaint)
        } else {
            textPaint.color = Color.RED
            canvas.drawText("WANTED", 0f, -24f, textPaint)
        }

        canvas.restore()
    }

    private fun drawVehicle(canvas: Canvas, veh: VehicleEntity) {
        canvas.save()
        canvas.translate(veh.x, veh.y)
        canvas.rotate((veh.angle * 180f / Math.PI).toFloat())

        val bodyPaint = Paint().apply {
            color = if (veh.isPolice) Color.parseColor("#0D47A1") else Color.parseColor("#B71C1C")
            style = Paint.Style.FILL
        }
        val roofPaint = Paint().apply {
            color = if (veh.isPolice) Color.WHITE else Color.parseColor("#333333")
            style = Paint.Style.FILL
        }

        // Chassis
        val halfL = veh.length / 2f
        val halfW = veh.width / 2f
        canvas.drawRoundRect(RectF(-halfL, -halfW, halfL, halfW), 6f, 6f, bodyPaint)

        // Roof cabin
        canvas.drawRoundRect(RectF(-halfL * 0.4f, -halfW * 0.7f, halfL * 0.4f, halfW * 0.7f), 4f, 4f, roofPaint)

        // Headlights
        val lightPaint = Paint().apply { color = Color.parseColor("#FFF9C4"); style = Paint.Style.FILL }
        canvas.drawCircle(halfL - 2f, -halfW + 4f, 3f, lightPaint)
        canvas.drawCircle(halfL - 2f, halfW - 4f, 3f, lightPaint)

        // Police Siren Strobe Lightbar
        if (veh.isPolice && veh.isSirenOn) {
            val isRed = (strobeTimer.toInt() % 2 == 0)
            val sirenLeftPaint = Paint().apply { color = if (isRed) Color.RED else Color.BLUE; style = Paint.Style.FILL }
            val sirenRightPaint = Paint().apply { color = if (isRed) Color.BLUE else Color.RED; style = Paint.Style.FILL }
            canvas.drawCircle(0f, -6f, 4f, sirenLeftPaint)
            canvas.drawCircle(0f, 6f, 4f, sirenRightPaint)
        }

        canvas.restore()
    }

    private fun drawMinimap(canvas: Canvas, session: GameSession, screenWidth: Float) {
        val mapSize = 140f
        val margin = 16f
        val mapLeft = screenWidth - mapSize - margin
        val mapTop = margin
        val mapRect = RectF(mapLeft, mapTop, mapLeft + mapSize, mapTop + mapSize)

        // Background
        canvas.drawRoundRect(mapRect, 14f, 14f, minimapBgPaint)
        canvas.drawRoundRect(mapRect, 14f, 14f, minimapBorderPaint)

        // Scale factors: 5000 world units into 140 pixels
        val scaleX = mapSize / GameConstants.WORLD_WIDTH
        val scaleY = mapSize / GameConstants.WORLD_HEIGHT

        // Draw major roads on minimap
        for (road in CityMap.ROADS) {
            val rLeft = mapLeft + road.bounds.left * scaleX
            val rTop = mapTop + road.bounds.top * scaleY
            val rRight = mapLeft + road.bounds.right * scaleX
            val rBottom = mapTop + road.bounds.bottom * scaleY
            canvas.drawRect(rLeft, rTop, rRight, rBottom, minimapRoadPaint)
        }

        // Police Station HQ marker
        val hqX = mapLeft + GameConstants.POLICE_STATION_X * scaleX
        val hqY = mapTop + GameConstants.POLICE_STATION_Y * scaleY
        canvas.drawCircle(hqX, hqY, 5f, minimapHqPaint)

        // Mission Target marker
        val tgtX = mapLeft + session.mission.targetX * scaleX
        val tgtY = mapTop + session.mission.targetY * scaleY
        canvas.drawCircle(tgtX, tgtY, 5f, minimapTargetPaint)

        // Suspect marker
        session.criminal?.let { c ->
            val cX = mapLeft + c.x * scaleX
            val cY = mapTop + c.y * scaleY
            canvas.drawCircle(cX, cY, 4f, minimapSuspectPaint)
        }

        // Player marker
        val pX = mapLeft + session.player.x * scaleX
        val pY = mapTop + session.player.y * scaleY
        canvas.drawCircle(pX, pY, 5f, minimapPlayerPaint)
    }
}
