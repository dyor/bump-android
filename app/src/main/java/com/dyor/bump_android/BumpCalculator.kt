class BumpCalculator {
    fun calculateBumps(golfers: List<Golfer>, holes: List<Hole>): Map<String, List<Int>> {
        val sortedHoles = holes.sortedByDescending { it.difficulty }
        return golfers.associate { golfer ->
            golfer.name to sortedHoles.take(golfer.bumps).map { it.number }
        }
    }
}