package PubVote

import com.geospock.pubvote.GroupSplitter
import com.geospock.pubvote.people.People
import com.geospock.pubvote.people.People.ANDRE
import com.geospock.pubvote.people.People.ARTEM
import com.geospock.pubvote.people.People.BENITA
import com.geospock.pubvote.people.People.BOB
import com.geospock.pubvote.people.People.CHRISTOPH
import com.geospock.pubvote.people.People.DAVID_B
import com.geospock.pubvote.people.People.DAVID_W
import com.geospock.pubvote.people.People.FELIX
import com.geospock.pubvote.people.People.HUW
import com.geospock.pubvote.people.People.JAMES_G
import com.geospock.pubvote.people.People.JON
import com.geospock.pubvote.people.People.KAI
import com.geospock.pubvote.people.People.KATIE
import com.geospock.pubvote.people.People.STEVE
import com.geospock.pubvote.people.People.XAVI
import com.geospock.pubvote.places.Place
import com.geospock.pubvote.places.Place.FORT_SAINT_GEORGE
import com.geospock.pubvote.places.Place.GREEN_DRAGON
import com.geospock.pubvote.places.Place.HAYMAKERS
import com.geospock.pubvote.places.Place.POLONIA
import com.geospock.pubvote.places.Place.THE_OLD_SPRING
import com.geospock.pubvote.places.Place.WRESTLERS
import com.geospock.pubvote.voters.StandardVoteInput
import com.geospock.pubvote.voters.WeightedRandomVoter

fun main(args: Array<String>) {

    val votes: List<Vote> = listOf(
            STEVE.voted {
                5 to WRESTLERS
                5 to FORT_SAINT_GEORGE
            },
            DAVID_W.voted {
                3 to HAYMAKERS
                3 to WRESTLERS
                4 to FORT_SAINT_GEORGE
            },
            JAMES_G.voted {
                5 to WRESTLERS
                5 to FORT_SAINT_GEORGE
            },
            ANDRE.voted {
                5 to FORT_SAINT_GEORGE
                5 to GREEN_DRAGON
            },
            BENITA.voted {
                5 to WRESTLERS
                5 to THE_OLD_SPRING
            },
            CHRISTOPH.voted {
                5 to THE_OLD_SPRING
                5 to FORT_SAINT_GEORGE
            },
            KATIE.voted {
                3 to WRESTLERS
                4 to THE_OLD_SPRING
                3 to POLONIA
            },
            KAI.voted { },
            XAVI.voted { },
            JON.voted { },
            BOB.voted { },
            ARTEM.voted { },
            HUW.voted { },
            DAVID_B.voted { },
            FELIX.voted { }
    )

    val groups = GroupSplitter(12).splitGroups(votes.map { it.person })

    val voteMap = votes
            .map { it.person to it.vote }
            .toMap()
    val groupVotes = consolidateGroupVotes(groups, voteMap)

    runVote(groupVotes)

}

private fun runVote(groupVotes: Map<List<People>, Map<Place, Int>>) {
    val voters = groupVotes.mapValues { WeightedRandomVoter() }
    val groupWinners = mutableMapOf<List<People>, Place>()

    var complete = false
    while (!complete) {
        complete = true
        val winners = mutableSetOf<Place>()
        for ((people, voter) in voters) {
            val winner = voter.runVote(StandardVoteInput(groupVotes.getOrDefault(people, mapOf<Place, Int>())))
            if (winners.contains(winner)) {
                complete = false
            }
            groupWinners[people] = winner
            winners.add(winner)
        }
    }

    groupWinners.forEach({ (people, place) ->
        println(voters[people]?.getOutput())
        println("$people are going to $place")
    })
}

private fun consolidateGroupVotes(groups: List<List<People>>, votes: Map<People, Map<Place, Int>>): MutableMap<List<People>, Map<Place, Int>> {
    val groupVotes = mutableMapOf<List<People>, Map<Place, Int>>()
    for (group in groups) {
        val totals = mutableMapOf<Place, Int>()
        group.forEach {
            votes[it]?.forEach { (place, value) ->
                val current = totals.getOrDefault(place, 0)
                totals.put(place, current + value)
            }
        }
        groupVotes.put(group, totals)
    }
    return groupVotes
}