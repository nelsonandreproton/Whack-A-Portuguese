package pt.whackaportugues.app.model

import pt.whackaportugues.app.R

/**
 * Represents a playable character (political/sports figure).
 * Each character has their own caricature image, themed background and colors.
 *
 * NOTE FOR DESIGNERS: Replace caricature_* drawables with actual recognizable caricatures.
 * Each caricature must clearly identify the person and will be shown with their name label.
 */
enum class Character(
    val id: Int,
    val displayName: String,
    val title: String,
    val caricatureRes: Int,
    val backgroundRes: Int,
    val primaryColor: Int,   // color resource id
    val secondaryColor: Int  // color resource id
) {
    RUI_COSTA(
        id = 0,
        displayName = "Rui Costa",
        title = "Presidente do Benfica",
        caricatureRes = R.drawable.caricature_rui_costa,
        backgroundRes = R.drawable.bg_benfica,
        primaryColor = R.color.benfica_red,
        secondaryColor = R.color.benfica_white
    ),
    VILLAS_BOAS(
        id = 1,
        displayName = "Villas-Boas",
        title = "Presidente do Porto",
        caricatureRes = R.drawable.caricature_villas_boas,
        backgroundRes = R.drawable.bg_porto,
        primaryColor = R.color.porto_blue,
        secondaryColor = R.color.porto_white
    ),
    VARANDAS(
        id = 2,
        displayName = "Varandas",
        title = "Presidente do Sporting",
        caricatureRes = R.drawable.caricature_varandas,
        backgroundRes = R.drawable.bg_sporting,
        primaryColor = R.color.sporting_green,
        secondaryColor = R.color.sporting_white
    ),
    MONTENEGRO(
        id = 3,
        displayName = "Montenegro",
        title = "Primeiro-Ministro",
        caricatureRes = R.drawable.caricature_montenegro,
        backgroundRes = R.drawable.bg_governo,
        primaryColor = R.color.psd_orange,
        secondaryColor = R.color.governo_white
    ),
    SEGURO(
        id = 4,
        displayName = "Seguro",
        title = "Presidente Eleito",
        caricatureRes = R.drawable.caricature_seguro,
        backgroundRes = R.drawable.bg_presidencia,
        primaryColor = R.color.presidencia_gold,
        secondaryColor = R.color.presidencia_white
    ),
    ANDRE_VENTURA(
        id = 5,
        displayName = "André Ventura",
        title = "Chega",
        caricatureRes = R.drawable.caricature_andre_ventura,
        backgroundRes = R.drawable.bg_chega,
        primaryColor = R.color.chega_red,
        secondaryColor = R.color.chega_black
    ),
    MARIANA_MORTAGUA(
        id = 6,
        displayName = "Mariana Mortágua",
        title = "Bloco de Esquerda",
        caricatureRes = R.drawable.caricature_mariana_mortagua,
        backgroundRes = R.drawable.bg_bloco,
        primaryColor = R.color.bloco_red,
        secondaryColor = R.color.bloco_black
    );

    companion object {
        fun fromId(id: Int): Character = values().firstOrNull { it.id == id } ?: RUI_COSTA
    }
}
