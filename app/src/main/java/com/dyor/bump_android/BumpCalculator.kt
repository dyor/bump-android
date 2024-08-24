class BumpCalculator {
    fun calculateBumps(golfers: List<Golfer>, holes: List<Hole>): Map<String, List<Int>> {
        // Sort the holes by difficulty, descending
        val sortedHoles = holes.sortedBy { it.difficulty }

        // Create a map to hold the result
        val golferBumps = mutableMapOf<String, List<Int>>()

        // Assign bumps for each golfer
        golfers.forEach { golfer ->
            var thisBump = golfer.bumps
            if (golfer.bumps > 18) {
                thisBump = thisBump-18
            }

            val bumps = sortedHoles.take(thisBump).map { it.number }
            golferBumps[golfer.name] = bumps
        }

        return golferBumps
    }
}