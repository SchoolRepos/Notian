package me.profiluefter.profinote.data

object ServiceLocator {
    val dataLoader: DataLoader = CSVDataLoader("notes.csv")
}