package com.fatihparkin.filmora.presentation.home

enum class SortOption(val displayName: String) {
    RATING_HIGH_TO_LOW("IMDB Puanı: Yüksekten Düşüğe"),
    RATING_LOW_TO_HIGH("IMDB Puanı: Düşükten Yükseğe"),
    DATE_NEW_TO_OLD("Çıkış Tarihi: Yeniden Eskiye"),
    DATE_OLD_TO_NEW("Çıkış Tarihi: Eskiden Yeniye")
}
