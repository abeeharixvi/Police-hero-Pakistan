package com.example.data

enum class EvidenceType {
    STOLEN_PHONE,
    PURSE,
    BICYCLE_FRAME,
    SHOP_LOCKPICK,
    WEAPON_CASING,
    TIRE_MARK,
    FOOTPRINT,
    WITNESS_STATEMENT,
    CASH_BAG,
    CONVOY_LEDGER,
    SMUGGLED_CRATE
}

data class ClueItem(
    val id: String,
    val type: EvidenceType,
    val title: String,
    val description: String,
    val worldX: Float,
    val worldY: Float,
    var isInvestigated: Boolean = false
)
